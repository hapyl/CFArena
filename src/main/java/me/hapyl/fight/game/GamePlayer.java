package me.hapyl.fight.game;

import me.hapyl.fight.game.cosmetic.Cosmetics;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.game.database.Award;
import me.hapyl.fight.game.database.Database;
import me.hapyl.fight.game.effect.ActiveGameEffect;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.gamemode.CFGameMode;
import me.hapyl.fight.game.heroes.ComplexHero;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.talents.ChargedTalent;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.util.Nulls;
import me.hapyl.spigotutils.module.annotate.Super;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.chat.Gradient;
import me.hapyl.spigotutils.module.chat.gradient.Interpolators;
import me.hapyl.spigotutils.module.math.Numbers;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scoreboard.Team;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class controls all in-game player data.
 *
 * <b>A single instance should exist per game bases and cleared after the game ends.</b>
 */
public class GamePlayer extends AbstractGamePlayer {

    private final double maxHealth = 100.0d;
    private final double maxShield = 50.0d;

    private Player player;
    private PlayerProfile profile;
    private final Hero hero; // this represents hero that player locked in game with, cannot be changed

    private double health;
    private double shield;
    private LivingEntity lastDamager;
    private EnumDamageCause lastDamageCause = null;

    private final StatContainer stats;

    private final Map<GameEffectType, ActiveGameEffect> gameEffects;
    private final Map<GamePlayer, Team> healthTeamMap = new HashMap<>();

    // 		[Kinda Important Note]
    //  These two can never be both true.
    //  dead means if player has died in game when
    //  spectator means if player started as spectator
    private boolean isDead;
    private boolean isSpectator;

    // Had to introduce this flag to
    // prevent spectator checks.
    private boolean isRespawning;

    private boolean valid = true; // valid means if game player is being used somewhere, should probably rework how this works
    private int ultPoints;

    private double cdModifier;
    private double ultimateModifier;

    private long lastMoved;

    public GamePlayer(PlayerProfile profile, Hero hero) {
        this.profile = profile;
        this.player = profile.getPlayer();
        this.hero = hero;
        this.health = maxHealth;
        this.isDead = false;
        this.cdModifier = 1.0d;
        this.ultimateModifier = 1.0d;
        this.isSpectator = false;
        this.gameEffects = new ConcurrentHashMap<>();
        this.stats = new StatContainer(player);
        this.lastMoved = System.currentTimeMillis();

        // supply to profile
        profile.setGamePlayer(this);
    }

    protected GamePlayer(boolean fake) {
        if (!fake) {
            throw new IllegalArgumentException("validate fake player");
        }

        this.player = new BukkitPlayer();
        this.profile = null;
        this.stats = null;
        this.hero = Heroes.randomHero().getHero();

        this.gameEffects = null;
        this.isDead = false;
        this.isSpectator = false;

        Debugger.warning("Created fake game player instance, expect errors!");
    }

    public void resetPlayer(Ignore... ignores) {
        if (isNotIgnored(ignores, Ignore.DAMAGER)) {
            lastDamager = null;
        }
        if (isNotIgnored(ignores, Ignore.DAMAGE_CAUSE)) {
            lastDamageCause = null;
        }

        setHealth(getMaxHealth());
        player.getInventory().clear();
        player.setMaxHealth(40.0d);
        player.setHealth(40.0d);
        player.setFireTicks(0);
        player.setWalkSpeed(0.2f);
        player.setVisualFire(false);
        player.setFlying(false);
        player.setSaturation(0.0f);
        player.setFoodLevel(20);
        player.setInvulnerable(false);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        Nulls.runIfNotNull(player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE), att -> att.setBaseValue(0.0f));

        if (isNotIgnored(ignores, Ignore.GAME_MODE)) {
            player.setGameMode(GameMode.SURVIVAL);
        }

        // reset all cooldowns as well
        if (isNotIgnored(ignores, Ignore.COOLDOWNS)) {
            for (final Material value : Material.values()) {
                if (value.isItem() && player.hasCooldown(value)) {
                    player.setCooldown(value, 0);
                }
            }
        }

    }

    public double getShield() {
        return shield;
    }

    public boolean hasShield() {
        return getShield() > 0.0d;
    }

    public void setShield(Shield shield) {
        this.shield = shield.getAmount();
    }

    public void markLastMoved() {
        this.lastMoved = System.currentTimeMillis();
    }

    @Override
    public double getCooldownAccelerationModifier() {
        return cdModifier;
    }

    @Override
    public double getUltimateAccelerationModifier() {
        return ultimateModifier;
    }

    /**
     * Returns formatted string of ultimate status.
     *
     * @return formatted string of ultimate status.
     */
    public String getUltimateString() {
        final Player player = getPlayer();
        final UltimateTalent ultimate = getUltimate();
        final String pointsString = "&b&l%s&b/&b&l%s".formatted(getUltPoints(), getUltPointsNeeded());

        if (getHero().isUsingUltimate(player)) {
            return "&b&lIN USE";
        }

        if (ultimate.hasCd(player)) {
            return "&7%s &b(%ss)".formatted(pointsString, BukkitUtils.roundTick(ultimate.getCdTimeLeft(player)));
        }

        else if (isUltimateReady()) {
            return "&b&lREADY";
        }

        return pointsString;
    }

    public void setCooldownAccelerationModifier(double cdModifier) {
        this.cdModifier = cdModifier;
    }

    public void setUltimateAccelerationModifier(double ultimateModifier) {
        this.ultimateModifier = ultimateModifier;
    }

    @Override
    public boolean isAbstract() {
        return false;
    }

    public long getLastMoved() {
        return lastMoved;
    }

    private boolean isNotIgnored(Ignore[] ignores, Ignore target) {
        for (final Ignore ignore : ignores) {
            if (ignore == target) {
                return false;
            }
        }
        return true;
    }

    public void addEffect(GameEffectType type, int ticks) {
        addEffect(type, ticks, false);
    }

    @Override
    public void addPotionEffect(PotionEffectType type, int duration, int amplifier) {
        PlayerLib.addEffect(player, type, duration, amplifier);
    }

    public void removePotionEffect(PotionEffectType type) {
        PlayerLib.removeEffect(player, type);
    }

    public void addEffect(GameEffectType type, int ticks, boolean override) {
        final ActiveGameEffect effect = gameEffects.get(type);
        if (effect != null) {
            effect.triggerUpdate();
            if (override) {
                effect.setRemainingTicks(ticks);
            }
            else {
                effect.addRemainingTicks(ticks);
            }
        }
        else {
            gameEffects.put(type, new ActiveGameEffect(player, type, ticks));
        }
    }

    public Map<GameEffectType, ActiveGameEffect> getActiveEffects() {
        return gameEffects;
    }

    public boolean hasEffect(GameEffectType type) {
        return gameEffects.containsKey(type);
    }

    @Nonnull
    public StatContainer getStats() {
        return stats;
    }

    public void clearEffects() {
        this.gameEffects.clear();
    }

    public void removeEffect(GameEffectType type) {
        final ActiveGameEffect gameEffect = gameEffects.get(type);
        if (gameEffect != null) {
            gameEffect.forceStop();
        }
    }

    public void clearEffect(GameEffectType type) {
        gameEffects.remove(type);
    }

    public boolean isAlive() {
        return !isDead && !isSpectator;
    }

    public boolean isUltimateReady() {
        return this.ultPoints >= this.hero.getUltimate().getCost();
    }

    @Override
    public void setHealth(double health) {
        this.health = health;
        this.updateHealth();
    }

    @Override
    public void damage(double d) {
        damage(d, null, EnumDamageCause.WATER);
    }

    public void damage(double d, EnumDamageCause cause) {
        damage(d, null, cause);
    }

    @Override
    public void damage(double d, LivingEntity damager) {
        damage(d, damager, null);
    }

    public void damage(double damage, @Nullable LivingEntity damager, @Nullable EnumDamageCause cause) {
        if (damager != null) {
            lastDamager = damager;
        }
        if (cause != null) {
            lastDamageCause = cause;
        }
        this.player.damage(damage, damager);
    }

    /**
     * This should only be called in the calculations, do not call it otherwise.
     */
    public void decreaseHealth(double damage, @Nullable LivingEntity damager) {
        this.decreaseHealth(damage);
        if (damager != null) {
            this.lastDamager = damager;
        }
    }

    /**
     * This should only be called in the calculations, do not call it otherwise.
     */
    public void decreaseHealth(double damage) {
        this.health -= damage;
        if (this.health <= 0.0d) {
            this.die(true);
        }

        this.updateHealth();
    }

    @Override
    public void sendMessage(String message, Object... objects) {
        Chat.sendMessage(player, message, objects);
    }

    @Override
    public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        Chat.sendTitle(player, title, subtitle, fadeIn, stay, fadeOut);
    }

    @Override
    public void sendActionbar(String text, Object... objects) {
        Chat.sendActionbar(player, text, objects);
    }

    @Override
    public void playSound(Sound sound, float pitch) {
        pitch = Numbers.clamp(pitch, 0.0f, 2.0f);
        PlayerLib.playSound(player, sound, pitch);
    }

    public void updateScoreboard(boolean toLobby) {
        profile.getScoreboardTeams().populate(toLobby);
    }

    private void showHealth() {
        final AbstractGameInstance game = Manager.current().getCurrentGame();

        // Create player teams
        game.getAlivePlayers().forEach(other -> {
            //            final Team team = getPlayerTeam(other);
            //            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
            //            healthTeamMap.put(other, team);
        });

        // Update team names
        new GameTask() {
            @Override
            public void run() {
                healthTeamMap.forEach((other, team) -> {
                    team.setPrefix("&6%s &e".formatted(other.getHero().getName()));
                    team.setSuffix(" &c&l%s &c❤".formatted(BukkitUtils.decimalFormat(other.getHealth())));

                    final String playerName = other.getPlayer().getName();
                    if (team.getEntries().contains(playerName)) {
                        team.addEntry(playerName);
                    }
                });
            }
        }.runTaskTimer(0, 10);

    }

    public void updateHealth() {
        // update player visual health
        player.setMaxHealth(40.d);
        player.setHealth(Math.max(0.5d, 40.0d * this.health / maxHealth));
    }

    public void heal(double amount) {
        this.health = Numbers.clamp(health + amount, 0.5d, maxHealth);
        this.updateHealth();
    }

    public void die(boolean force) {
        if (this.health > 0.0d && !force) {
            return;
        }

        // Don't kill creative players
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        this.isDead = true;
        this.isSpectator = false;

        this.resetPlayer(Ignore.DAMAGE_CAUSE, Ignore.DAMAGER);
        player.setGameMode(GameMode.SPECTATOR);

        PlayerLib.playSound(player, Sound.ENTITY_BLAZE_DEATH, 2.0f);
        Chat.sendTitle(player, "&c&lYOU DIED", "", 5, 25, 10);

        this.getHero().onDeath(player);
        executeTalentsOnDeath();

        // Award killer coins for kill
        if (lastDamager != null) {
            Player killer = null;
            if (lastDamager instanceof Player) {
                killer = (Player) lastDamager;
            }
            else if (lastDamager instanceof Projectile projectile && projectile.getShooter() instanceof Player target) {
                killer = target;
            }

            if (killer != null) {
                final StatContainer killerStats = GamePlayer.getPlayer(killer).getStats();

                if (killerStats != null) {
                    killerStats.addValue(StatContainer.Type.KILLS, 1);
                }

                // Award elimination to killer
                Award.PLAYER_ELIMINATION.award(killer);

                // Display cosmetics
                final Cosmetics killCosmetic = Cosmetics.getSelected(killer, Type.KILL);
                if (killCosmetic != null) {
                    killCosmetic.getCosmetic().onDisplay(new Display(killer, player.getLocation()));
                }
            }
        }

        stats.addValue(StatContainer.Type.DEATHS, 1);

        // broadcast death
        final String deathMessage = new Gradient(concat("☠ %s ".formatted(player.getName()), getRandomDeathMessage(), lastDamager)).rgb(
                new Color(160, 0, 0),
                new Color(255, 51, 51),
                Interpolators.LINEAR
        );

        // send death info to manager
        final GameInstance gameInstance = Manager.current().getGameInstance();
        if (gameInstance != null) {
            final CFGameMode mode = gameInstance.getMode();

            mode.onDeath(gameInstance, this);
            gameInstance.checkWinCondition();

            // Handle respawn
            if (mode.isAllowRespawn()) {
                respawnIn(mode.getRespawnTime() + 1);
            }
        }

        // Display death cosmetics
        final Cosmetics deathCosmetic = Cosmetics.getSelected(player, Type.DEATH);
        if (deathCosmetic != null) {
            deathCosmetic.getCosmetic().onDisplay(player);
        }

        Chat.broadcast(deathMessage);
    }

    public EnumDamageCause.DeathMessage getRandomDeathMessage() {
        return getLastDamageCause().getRandomIfMultiple();
    }

    @Override
    @Nonnull
    public EnumDamageCause getLastDamageCause() {
        return lastDamageCause == null ? EnumDamageCause.ENTITY_ATTACK : lastDamageCause;
    }

    private String concat(String original, EnumDamageCause.DeathMessage message, Entity killer) {
        String suffix = "";
        if (killer != null) {
            final String pronoun = getValidPronoun(killer);
            if (!message.hasSuffix()) {
                return original + message.formatMessage(pronoun);
            }
            else {
                suffix = message.getDamagerSuffix() + " " + pronoun;
            }
        }
        return original + message.getMessage() + " " + suffix;
    }

    private String getValidPronoun(Entity entity) {
        if (entity instanceof Projectile) {
            final ProjectileSource shooter = ((Projectile) entity).getShooter();
            if (shooter instanceof LivingEntity) {
                return ((LivingEntity) shooter).getName() + "'s " + entity.getName();
            }
        }
        return entity.getName();
    }

    private void executeTalentsOnDeath() {
        executeOnDeathIfTalentIsNotNull(hero.getFirstTalent());
        executeOnDeathIfTalentIsNotNull(hero.getSecondTalent());
        if (hero instanceof ComplexHero complex) {
            executeOnDeathIfTalentIsNotNull(complex.getThirdTalent());
            executeOnDeathIfTalentIsNotNull(complex.getFourthTalent());
            executeOnDeathIfTalentIsNotNull(complex.getFifthTalent());
        }
    }

    private void executeOnDeathIfTalentIsNotNull(Talent talent) {
        if (talent != null) {
            talent.onDeath(player);
            if (talent instanceof ChargedTalent chargedTalent) {
                chargedTalent.onDeathCharged(player);
            }
        }
    }

    @Override
    public double getMaxHealth() {
        return maxHealth;
    }

    @Override
    public boolean isRespawning() {
        return isRespawning;
    }

    @Override
    public String getHealthFormatted() {
        return "" + Math.ceil(health);
    }

    public UltimateTalent getUltimate() {
        return this.hero.getUltimate();
    }

    public void setLastDamageCause(EnumDamageCause lastDamageCause) {
        this.lastDamageCause = lastDamageCause;
    }

    public void setLastDamager(LivingEntity lastDamager) {
        if (lastDamager != null) {
            this.lastDamager = lastDamager;
        }
    }

    public void addUltimatePoints(int points) {
        // cannot give points if using ultimate or dead
        if (hero.isUsingUltimate(player) || !this.isAlive() || this.ultPoints >= this.getUltPointsNeeded()) {
            return;
        }

        this.ultPoints = Numbers.clamp(this.ultPoints + points, 0, this.hero.getUltimate().getCost());

        // show once at broadcast
        if (this.ultPoints >= this.getUltPointsNeeded()) {
            Chat.sendMessage(player, "&b&l※ &bYou ultimate is ready! Press &e&lF &bto use it!");
            Chat.sendTitle(player, "", "&aYou ultimate is ready!", 5, 15, 5);
            PlayerLib.playSound(player, Sound.BLOCK_CONDUIT_DEACTIVATE, 2.0f);
        }
    }

    @Nullable
    public LivingEntity getLastDamager() {
        return lastDamager;
    }

    @Override
    public double getHealth() {
        return health;
    }

    // This is a shortcut that returns an GamePlayer from a game instance if there is one.
    // One should not use this method unless checked for null before calling.
    @Nullable
    public static GamePlayer getAlivePlayer(Player player) {
        final GameInstance gameInstance = Manager.current().getGameInstance();
        if (gameInstance == null) {
            return null;
        }
        return gameInstance.getPlayer(player);
    }

    /**
     * Returns either an actual GamePlayer instance if there is a GameInstance, otherwise AbstractGamePlayer.
     *
     * @param player bukkit player.
     * @return either an actual GamePlayer instance if there is a GameInstance, otherwise AbstractGamePlayer.
     */
    @Nonnull
    public static AbstractGamePlayer getPlayer(Player player) {
        final GamePlayer gamePlayer = getAlivePlayer(player);
        return gamePlayer == null ? AbstractGamePlayer.NULL_GAME_PLAYER : gamePlayer;
    }

    public Player getPlayer() {
        return player;
    }

    @Nonnull
    public Hero getHero() {
        return hero;
    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        isDead = dead;
        isSpectator = !dead;
    }

    public boolean isSpectator() {
        return isSpectator;
    }

    public void setSpectator(boolean spectator) {
        isSpectator = spectator;
        isDead = !spectator;
    }

    public void respawn() {
        this.isRespawning = false;
        this.isDead = false;
        this.isSpectator = false;
        this.ultPoints = 0;
        this.hero.setUsingUltimate(player, false);
        this.setHealth(this.getMaxHealth());

        // charged attack fix
        //        hero.resetTalents(player);

        Manager.current().equipPlayer(player);

        this.player.setGameMode(GameMode.SURVIVAL);

        // Add spawn protection
        addEffect(GameEffectType.RESPAWN_RESISTANCE, 60);

        // Respawn location
        final AbstractGameInstance gameInstance = Manager.current().getCurrentGame();
        final Location location = gameInstance.getCurrentMap().getMap().getLocation();

        BukkitUtils.mergePitchYaw(player.getLocation(), location);
        sendTitle("&aRespawned!", "", 0, 20, 5);
        player.teleport(location);

        addPotionEffect(PotionEffectType.BLINDNESS, 20, 1);
    }

    public int getUltPointsNeeded() {
        return this.hero == null ? 999 : this.hero.getUltimate().getCost();
    }

    public int getUltPoints() {
        return ultPoints;
    }

    public void setUltPoints(int ultPoints) {
        this.ultPoints = ultPoints;
    }

    public boolean compare(GamePlayer gamePlayer) {
        return gamePlayer == this;
    }

    public boolean compare(Player player) {
        return this.getPlayer() == player;
    }

    // static members
    public static void damageEntity(LivingEntity entity, double damage) {
        damageEntity(entity, damage, null, EnumDamageCause.ENTITY_ATTACK);
    }

    public static void damageEntity(LivingEntity entity, double damage, LivingEntity damager) {
        damageEntity(entity, damage, damager, EnumDamageCause.ENTITY_ATTACK);
    }

    public static void damageEntityTick(LivingEntity entity, double damage, int tick) {
        damageEntityTick(entity, damage, null, EnumDamageCause.ENTITY_ATTACK, tick);
    }

    public static void damageEntityTick(LivingEntity entity, double damage, @Nullable LivingEntity damager, int tick) {
        damageEntityTick(entity, damage, damager, EnumDamageCause.ENTITY_ATTACK, tick);
    }

    public static void damageEntityTick(LivingEntity entity, double damage, @Nullable LivingEntity damager, @Nullable EnumDamageCause cause, int tick) {
        final int maximumNoDamageTicks = entity.getMaximumNoDamageTicks();
        tick = Numbers.clamp(tick, 0, maximumNoDamageTicks);

        entity.setMaximumNoDamageTicks(tick);
        damageEntity(entity, damage, damager, cause == null ? EnumDamageCause.ENTITY_ATTACK : cause);
        entity.setMaximumNoDamageTicks(maximumNoDamageTicks);
    }

    @Super
    public static void damageEntity(LivingEntity entity, double damage, LivingEntity damager, EnumDamageCause cause) {
        if (entity instanceof Player player) {
            //            // Check for teammate
            //            if (damager instanceof Player playerDamager && GameTeam.isTeammate(player, playerDamager)) {
            //                Chat.sendMessage(playerDamager, "&cCannot damage teammates!");
            //                return;
            //            }
            getPlayer(player).damage(damage, damager, cause);
        }
        else {
            entity.damage(damage, damager);
        }
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("GamePlayer{");
        sb.append("player=").append(player);
        sb.append(", hero=").append(hero);
        sb.append(", health=").append(health);
        sb.append(", isDead=").append(isDead);
        sb.append(", isSpectator=").append(isSpectator);
        sb.append(", ultPoints=").append(ultPoints);
        sb.append('}');
        return sb.toString();
    }

    public void setValid(boolean flag) {
        valid = flag;
    }

    public boolean isValid() {
        return valid;
    }

    public Database getDatabase() {
        return Manager.current().getProfile(player).getDatabase();
    }

    public PlayerProfile getProfile() {
        return profile;
    }

    public void respawnIn(int tick) {
        isRespawning = true;
        new GameTask() {
            private int tickBeforeRespawn = tick;

            @Override
            public void run() {
                if (tickBeforeRespawn < 0) {
                    respawn();
                    this.cancel();
                    return;
                }

                sendTitle("&aRespawning in", "&a&l" + BukkitUtils.roundTick(tickBeforeRespawn) + "s", 0, 25, 0);
                if (tickBeforeRespawn % 20 == 0) {
                    playSound(Sound.BLOCK_NOTE_BLOCK_PLING, 2.0f - (0.2f * (tickBeforeRespawn / 20f)));
                }
                --tickBeforeRespawn;
            }
        }.runTaskTimer(0, 1);
    }

    @Override
    public GameTeam getTeam() {
        return GameTeam.getPlayerTeam(player);
    }

    public boolean isTeammate(Player player) {
        return GameTeam.isTeammate(this.player, player);
    }

    public String getName() {
        return player.getName();
    }

    public void setHandle(Player player) {
        this.player = player;
        this.profile = Manager.current().getProfile(player);
    }

    public enum Ignore {
        GAME_MODE,
        DAMAGER,
        DAMAGE_CAUSE,
        COOLDOWNS
    }

    public enum Shield {
        SMALL(50.0d),
        BIG(25.0d);

        final double amount;

        Shield(double amount) {
            this.amount = amount;
        }

        public double getAmount() {
            return amount;
        }
    }

}

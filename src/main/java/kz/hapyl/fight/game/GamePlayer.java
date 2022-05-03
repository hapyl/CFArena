package kz.hapyl.fight.game;

import kz.hapyl.fight.game.database.Database;
import kz.hapyl.fight.game.database.entry.CurrencyEntry;
import kz.hapyl.fight.game.effect.ActiveGameEffect;
import kz.hapyl.fight.game.effect.GameEffectType;
import kz.hapyl.fight.game.heroes.ComplexHero;
import kz.hapyl.fight.game.heroes.Hero;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.fight.game.talents.UltimateTalent;
import kz.hapyl.fight.game.task.GameTask;
import kz.hapyl.fight.util.Nulls;
import kz.hapyl.spigotutils.module.annotate.Super;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.chat.Gradient;
import kz.hapyl.spigotutils.module.chat.gradient.Interpolators;
import kz.hapyl.spigotutils.module.math.Numbers;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// TODO: 019. 10/19/2021 - Maybe rework it to be a online player instance instead of only gameInstance
public class GamePlayer extends AbstractGamePlayer {

    /**
     * A single instance should exist per game bases and cleared after the game ends.
     */

    private final double maxHealth = 100.0d;

    private final Player player;
    private final Hero hero; // this represents hero that player locked in game with, cannot be changed

    private double health;
    private LivingEntity lastDamager;
    private EnumDamageCause lastDamageCause = null;

    private final StatContainer stats;
    private final Database database;

    private final Map<GameEffectType, ActiveGameEffect> gameEffects;
    private final Map<GamePlayer, Team> healthTeamMap = new HashMap<>();

    // 		[Kinda Important Note]
    //  These two can never be both true.
    //  dead means if player has died in game when
    //  spectator means if player started as spectator
    private boolean isDead;
    private boolean isSpectator;

    private boolean valid = true; // valid means if game player is being used somewhere, should probably rework how this works
    private int ultPoints;

    private long lastMoved;

    public GamePlayer(Player player, Hero hero) {
        this.player = player;
        this.hero = hero;
        this.health = maxHealth;
        this.isDead = false;
        this.isSpectator = false;
        this.gameEffects = new ConcurrentHashMap<>();
        this.stats = new StatContainer(player);
        this.database = Database.getDatabase(player);
        this.lastMoved = System.currentTimeMillis();

        // supply self to GamePlayerUI
        Nulls.runIfNotNull(Manager.current().getPlayerUI(player), ui -> ui.supplyGamePlayer(this));
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
        player.setVisualFire(false);
        player.setFlying(false);
        player.setSaturation(0.0f);
        player.setFoodLevel(20);
        player.setInvulnerable(false);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));

        if (isNotIgnored(ignores, Ignore.GAMEMODE)) {
            player.setGameMode(GameMode.SURVIVAL);
        }

        // reset all cooldowns as well
        if (isNotIgnored(ignores, Ignore.COOLDOWNS)) {
            for (final Material value : Material.values()) {
                if (player.hasCooldown(value)) {
                    player.setCooldown(value, 0);
                }
            }
        }

    }

    public void markLastMoved() {
        this.lastMoved = System.currentTimeMillis();
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

    /**
     * @see GamePlayer#resetPlayer(Ignore...)
     * @deprecated
     */
    @Deprecated
    public void resetPlayer() {
        lastDamager = null;
        lastDamageCause = null;
        setHealth(getMaxHealth());
        player.getInventory().clear();
        player.setMaxHealth(40.0d);
        player.setHealth(40.0d);
        player.setGameMode(GameMode.SURVIVAL);
        player.setFireTicks(0);
        player.setVisualFire(false);
        player.setFlying(false);
        player.setSaturation(0.0f);
        player.setFoodLevel(20);
        player.setInvulnerable(false);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));

        // reset all cooldowns as well
        for (final Material value : Material.values()) {
            if (value.isItem() && player.hasCooldown(value)) {
                player.setCooldown(value, 0);
            }
        }
    }

    public Database getDatabase() {
        return database;
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
        gameEffect.forceStop();
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
        damage(d, null, null);
    }

    public void damage(double d, EnumDamageCause cause) {
        damage(d, null, cause);
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

    public void updateScoreboard(boolean flag) {
        final Team team = getOrCreateTeam();

        // turn on nicknames and turn off collisions
        if (!flag) {
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
        }
        else {
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.ALWAYS);
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        }

        if (!healthTeamMap.isEmpty()) {
            healthTeamMap.values().forEach(Team::unregister);
            healthTeamMap.clear();
        }

        populateTeam(team);
    }


    private void showHealth() {
        final AbstractGameInstance game = Manager.current().getCurrentGame();

        // Create player teams
        game.getAlivePlayers().forEach(other -> {
            final Team team = getPlayerTeam(other);
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
            healthTeamMap.put(other, team);
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

    private Team getPlayerTeam(GamePlayer other) {
        return getOrCreateTeam(("%" + other.getPlayer().getName()).substring(16));
    }

    private Team getOrCreateTeam(String name) {
        final Scoreboard scoreboard = player.getScoreboard();
        Team team = scoreboard.getTeam("%" + name);
        if (team == null) {
            team = scoreboard.registerNewTeam("%" + name);
        }
        return team;
    }

    private Team getOrCreateTeam() {
        return getOrCreateTeam("Team");
    }

    private void populateTeam(Team team) {
        for (final String entry : team.getEntries()) {
            team.removeEntry(entry);
        }

        Bukkit.getOnlinePlayers().forEach(player -> team.addEntry(player.getName()));
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
                final CurrencyEntry.Award playerKill = CurrencyEntry.Award.PLAYER_KILL;
                final StatContainer killerStats = GamePlayer.getPlayer(killer).getStats();

                if (killerStats != null) {
                    killerStats.addValue(StatContainer.Type.COINS, playerKill.getAmount());
                    killerStats.addValue(StatContainer.Type.KILLS, 1);
                }

                Database.getDatabase(killer).getCurrency().awardCoins(playerKill);
            }
        }

        stats.addValue(StatContainer.Type.DEATHS, 1);

        // broadcast death
        final String deathMessage = new Gradient(concat(
                "☠ %s ".formatted(player.getName()),
                getRandomDeathMessage(),
                lastDamager
        ))
                .rgb(new Color(160, 0, 0), new Color(255, 51, 51), Interpolators.LINEAR);

        // send death info to manager
        final GameInstance gameInstance = Manager.current().getGameInstance();
        if (gameInstance != null) {
            gameInstance.getMode().onDeath(gameInstance, this);
            gameInstance.checkWinCondition();
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
        }
    }

    @Override
    public double getMaxHealth() {
        return maxHealth;
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
     * @return An actual GamePlayer instance of a player or a null if called in IllegalState.
     */
    @Nonnull
    public static AbstractGamePlayer getPlayer(Player player) {
        final GamePlayer gamePlayer = getAlivePlayer(player);
        return gamePlayer == null ? AbstractGamePlayer.NULL_GAME_PLAYER : gamePlayer;
    }

    public Player getPlayer() {
        return player;
    }

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
        this.isDead = false;
        this.isSpectator = false;
        this.ultPoints = 0;
        this.hero.setUsingUltimate(player, false);
        this.setHealth(this.getMaxHealth());

        Manager.current().equipPlayer(player);

        this.player.setGameMode(GameMode.SURVIVAL);
        addEffect(GameEffectType.RESPAWN_RESISTANCE, 60);
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

    public enum Ignore {

        GAMEMODE,
        DAMAGER,
        DAMAGE_CAUSE,
        COOLDOWNS

    }

}

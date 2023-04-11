package me.hapyl.fight.game;

import me.hapyl.fight.database.Award;
import me.hapyl.fight.database.Database;
import me.hapyl.fight.game.cosmetic.Cosmetics;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.game.cosmetic.skin.Skins;
import me.hapyl.fight.game.effect.ActiveGameEffect;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.gamemode.CFGameMode;
import me.hapyl.fight.game.heroes.ComplexHero;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.talents.InputTalent;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentQueue;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.util.Nulls;
import me.hapyl.spigotutils.module.annotate.Super;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.chat.Gradient;
import me.hapyl.spigotutils.module.chat.gradient.Interpolators;
import me.hapyl.spigotutils.module.math.Numbers;
import me.hapyl.spigotutils.module.math.Tick;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.reflect.Reflect;
import me.hapyl.spigotutils.module.reflect.ReflectPacket;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import net.minecraft.network.protocol.game.PacketPlayOutAnimation;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class controls all in-game player data.
 *
 * <b>A single instance should exist per game bases and cleared after the game ends.</b>
 */
public class GamePlayer implements IGamePlayer {

    public static final long COMBAT_TAG_DURATION = 5000L;

    private final double maxHealth = 100.0d;
    private final double maxShield = 50.0d;
    private final Heroes enumHero;
    private final Hero hero; // this represents hero that player locked in game with, cannot be changed
    private final Skins skin;
    private final StatContainer stats;
    private final Map<GameEffectType, ActiveGameEffect> gameEffects;
    private final TalentQueue talentQueue;
    private boolean wasHit;
    private Player player;
    private PlayerProfile profile;
    private double health;
    private double shield;
    private LivingEntity lastDamager;
    private EnumDamageCause lastDamageCause = null;
    // 		[Kinda Important Note]
    //  These two can never be both true.
    //  dead means if player has died in game when
    //  spectator means if player started as spectator
    private boolean isDead;
    private boolean isSpectator;
    private boolean isRespawning; // Had to introduce this flag to prevent spectator checks.
    private boolean valid = true; // valid means if game player is being used somewhere, should probably rework how this works
    private boolean canMove;
    private int ultPoints;
    private double ultimateModifier;
    private long lastMoved;
    private long combatTag;
    private int killStreak;
    private InputTalent inputTalent;

    public GamePlayer(PlayerProfile profile, Heroes enumHero) {
        this.profile = profile;
        this.player = profile.getPlayer();
        this.enumHero = enumHero;
        this.hero = enumHero.getHero();
        this.health = maxHealth;
        this.isDead = false;
        this.ultimateModifier = 1.0d;
        this.isSpectator = false;
        this.gameEffects = new ConcurrentHashMap<>();
        this.talentQueue = new TalentQueue(this);
        this.stats = new StatContainer(player);
        this.lastMoved = System.currentTimeMillis();
        this.combatTag = 0L;
        this.skin = Database.getDatabase(player).getHeroEntry().getSkin(enumHero);
        this.canMove = true;
        this.wasHit = false;

        // supply to profile
        profile.setGamePlayer(this);
    }

    protected GamePlayer(boolean fake) {
        if (!fake) {
            throw new IllegalArgumentException("validate fake player");
        }

        this.player = new FakeBukkitPlayer();
        this.hero = Heroes.randomHero().getHero();

        this.enumHero = null;
        this.profile = null;
        this.stats = null;
        this.skin = null;
        this.talentQueue = null;
        this.gameEffects = null;
        this.isDead = false;
        this.isSpectator = false;

        Debugger.warn("Created fake game player instance, expect errors!");
    }

    public void resetPlayer(Ignore... ignores) {
        if (isNotIgnored(ignores, Ignore.DAMAGER)) {
            lastDamager = null;
        }
        if (isNotIgnored(ignores, Ignore.DAMAGE_CAUSE)) {
            lastDamageCause = null;
        }

        killStreak = 0;
        combatTag = 0;
        markLastMoved();

        setHealth(getMaxHealth());
        player.setLastDamageCause(null);
        player.getInventory().clear();
        player.setMaxHealth(40.0d); // why deprecate
        player.setHealth(40.0d);
        player.setFireTicks(0);
        player.setWalkSpeed(0.2f);
        player.setVisualFire(false);
        player.setFlying(false);
        player.setSaturation(0.0f);
        player.setFoodLevel(20);
        player.setInvulnerable(false);
        player.setArrowsInBody(0);
        player.setMaximumNoDamageTicks(20);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
        Nulls.runIfNotNull(player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE), att -> att.setBaseValue(0.0f));

        wasHit = false;
        inputTalent = null;

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

    @Nonnull
    public TalentQueue getTalentQueue() {
        return talentQueue;
    }

    public int getKillStreak() {
        return killStreak;
    }

    @Nullable
    @Override
    public Skins getSkin() {
        return skin;
    }

    public double getShield() {
        return shield;
    }

    public void setShield(Shield shield) {
        this.shield = shield.getAmount();
    }

    public boolean hasShield() {
        return getShield() > 0.0d;
    }

    public void markLastMoved() {
        this.lastMoved = System.currentTimeMillis();
    }

    @Override
    public double getUltimateAccelerationModifier() {
        return ultimateModifier;
    }

    public void setUltimateAccelerationModifier(double ultimateModifier) {
        this.ultimateModifier = ultimateModifier;
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
            final long durationLeft = hero.getUltimateDurationLeft(player);
            return "&b&lIN USE &b(%s&b)".formatted(durationLeft == 0 ? "&lUSE" : BukkitUtils.roundTick(Tick.fromMillis(durationLeft)) + "s");
        }

        if (ultimate.hasCd(player)) {
            return "&7%s &b(%ss)".formatted(pointsString, BukkitUtils.roundTick(ultimate.getCdTimeLeft(player)));
        }

        else if (isUltimateReady()) {
            return "&b&lREADY";
        }

        return pointsString;
    }

    @Override
    public boolean isReal() {
        return true;
    }

    public long getLastMoved() {
        return lastMoved;
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
    public void damage(double d) {
        damage(d, null, null);
    }

    public void damage(double d, EnumDamageCause cause) {
        damage(d, null, cause);
    }

    @Override
    public void damage(double d, LivingEntity damager) {
        damage(d, damager, null);
    }

    @Super
    public void damage(double damage, @Nullable LivingEntity damager, @Nullable EnumDamageCause cause) {
        if (damager != null && damager != player) {
            lastDamager = damager;
        }
        if (cause != null) {
            lastDamageCause = cause;
        }

        //this.player.setLastDamageCause(null); // mark as custom damage
        wasHit = true;
        player.damage(damage, damager);
        wasHit = false;
    }

    /**
     * This should only be called in the calculations, do not call it otherwise.
     */
    public void decreaseHealth(double damage, @Nullable LivingEntity damager) {
        decreaseHealth(damage);

        if (damager != null && damager != player) {
            lastDamager = damager;
        }

        markCombatTag();
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

    public void updateHealth() {
        // update player visual health
        player.setMaxHealth(40.d);
        player.setHealth(Numbers.clamp(40.0d * health / maxHealth, getMinHealth(), getMaxHealth()));
        //player.setHealth(Math.max(0.5d, 40.0d * health / maxHealth));
    }

    public void interrupt() {
        final PlayerInventory inventory = player.getInventory();
        inventory.setHeldItemSlot(inventory.firstEmpty());

        ReflectPacket.wrapAndSend(new PacketPlayOutAnimation(Reflect.getMinecraftPlayer(player), 1), player);

        GameTask.runLater(() -> {
            inventory.setHeldItemSlot(0);
        }, 1);
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

        player.setGameMode(GameMode.SPECTATOR);

        PlayerLib.playSound(player, Sound.ENTITY_BLAZE_DEATH, 2.0f);
        Chat.sendTitle(player, "&c&lYOU DIED", "", 5, 25, 10);

        triggerOnDeath();

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
                final GamePlayer gameKiller = GamePlayer.getExistingPlayer(killer);
                if (gameKiller != null && player != killer) { // should never be the case
                    final StatContainer killerStats = gameKiller.getStats();

                    killerStats.addValue(StatContainer.Type.KILLS, 1);

                    // Add kill streak for killer
                    gameKiller.killStreak++;

                    // Award elimination to killer
                    Award.PLAYER_ELIMINATION.award(killer);

                    // Display cosmetics
                    final Cosmetics killCosmetic = Cosmetics.getSelected(killer, Type.KILL);
                    if (killCosmetic != null) {
                        killCosmetic.getCosmetic().onDisplay(new Display(killer, player.getLocation()));
                    }
                }
            }
        }

        stats.addValue(StatContainer.Type.DEATHS, 1);

        // broadcast death
        final String deathMessage = new Gradient(concat("☠ %s ".formatted(player.getName()), getRandomDeathMessage(), lastDamager))
                .rgb(
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

        resetPlayer(Ignore.GAME_MODE);
        Chat.broadcast(deathMessage);
    }

    public void triggerOnDeath() {
        final IGameInstance currentGame = Manager.current().getCurrentGame();

        currentGame.getMap().getMap().onDeath(player);
        getHero().onDeath(player);
        executeTalentsOnDeath();

        currentGame.getActiveHeroes().forEach(heroes -> {
            heroes.getHero().onDeathGlobal(player, lastDamager, lastDamageCause);
        });
    }

    public EnumDamageCause.DeathMessage getRandomDeathMessage() {
        return getLastDamageCause().getRandomIfMultiple();
    }

    @Override
    @Nonnull
    public EnumDamageCause getLastDamageCause() {
        return lastDamageCause == null ? EnumDamageCause.ENTITY_ATTACK : lastDamageCause;
    }

    public void setLastDamageCause(EnumDamageCause lastDamageCause) {
        this.lastDamageCause = lastDamageCause;
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

    public void setLastDamager(LivingEntity lastDamager) {
        if (lastDamager != null) {
            this.lastDamager = lastDamager;
        }
    }

    @Override
    public double getHealth() {
        return health;
    }

    @Override
    public void setHealth(double health) {
        this.health = health;
        this.updateHealth();
    }

    public Player getPlayer() {
        return player;
    }

    @Nonnull
    public Hero getHero() {
        return hero;
    }

    public Heroes getEnumHero() {
        return enumHero;
    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        isDead = dead;
        isSpectator = !dead;

        triggerOnDeath();
    }

    @Override
    public boolean canMove() {
        return canMove;
    }

    @Override
    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }

    @Override
    public long getCombatTag() {
        final long timeLeft = (combatTag + COMBAT_TAG_DURATION) - System.currentTimeMillis();
        return timeLeft < 0 ? 0 : timeLeft;
    }

    @Override
    public void markCombatTag() {
        this.combatTag = System.currentTimeMillis();
    }

    @Override
    public boolean isCombatTagged() {
        return getCombatTag() > 0;
    }

    public boolean isNativeDamage() {
        return !wasHit;
    }

    @Nullable
    @Override
    public InputTalent getInputTalent() {
        return inputTalent;
    }

    @Override
    public void setInputTalent(@Nullable InputTalent inputTalent) {
        if (inputTalent == null) {
            player.sendTitle("", "", 0, 0, 10);
        }
        this.inputTalent = inputTalent;
    }

    public boolean isSpectator() {
        return isSpectator;
    }

    public void setSpectator(boolean spectator) {
        isSpectator = spectator;
        isDead = !spectator;

        player.setGameMode(GameMode.SPECTATOR);
    }

    public void respawn() {
        resetPlayer(Ignore.DAMAGE_CAUSE, Ignore.DAMAGER, Ignore.GAME_MODE);

        isRespawning = false;
        isDead = false;
        isSpectator = false;
        ultPoints = 0;
        hero.setUsingUltimate(player, false);
        setHealth(this.getMaxHealth());

        player.getInventory().clear();
        Manager.current().equipPlayer(player, hero);

        hero.onRespawn(player);

        player.setGameMode(GameMode.SURVIVAL);

        // Add spawn protection
        addEffect(GameEffectType.RESPAWN_RESISTANCE, 60);

        // Respawn location
        final IGameInstance gameInstance = Manager.current().getCurrentGame();
        final Location location = gameInstance.getMap().getMap().getLocation();

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

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean flag) {
        valid = flag;
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
                // stop respawning if the game has ended
                if (Manager.current().getCurrentGame().getGameState() != State.IN_GAME) {
                    cancel();
                    return;
                }

                if (tickBeforeRespawn < 0) {
                    respawn();
                    cancel();
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

    @Override
    public String toString() {
        return "GamePlayer{" + player.getName() + "}";
    }

    @Override
    public double getMinHealth() {
        return 0.5d;
    }

    private boolean isNotIgnored(Ignore[] ignores, Ignore target) {
        for (final Ignore ignore : ignores) {
            if (ignore == target) {
                return false;
            }
        }
        return true;
    }

    private String concat(String original, EnumDamageCause.DeathMessage message, Entity killer) {
        if (killer == null) {
            return original + message.message();
        }

        final String pronoun = getValidPronoun(killer);
        return (original + message.formatMessage(pronoun) + " " + message.formatSuffix(pronoun)).trim();
    }

    private String getValidPronoun(Entity entity) {
        if (entity == null) {
            return "";
        }

        if (entity instanceof Projectile projectile) {
            final ProjectileSource shooter = projectile.getShooter();

            if (shooter instanceof LivingEntity livingShooter) {
                return livingShooter.getName() + "'s " + entity.getName();
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

    /**
     * Returns a player from existing instance, no matter if they're alive or not.
     *
     * @param player - bukkit player.
     * @return GamePlayer instance if there is a GameInstance, otherwise null.
     */
    @Nullable
    public static GamePlayer getExistingPlayer(Player player) {
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
    public static IGamePlayer getPlayer(Player player) {
        final GamePlayer gamePlayer = getExistingPlayer(player);
        return gamePlayer == null ? IGamePlayer.NULL_GAME_PLAYER : gamePlayer;
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
        if (entity == null) {
            return;
        }

        if (entity instanceof Player player) {
            getPlayer(player).damage(damage, damager, cause);
        }
        else {
            entity.damage(damage, damager);
        }
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

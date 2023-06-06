package me.hapyl.fight.game;

import me.hapyl.fight.database.Award;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.game.achievement.Achievements;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.PlayerAttributes;
import me.hapyl.fight.game.cosmetic.Cosmetics;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.game.cosmetic.skin.Skins;
import me.hapyl.fight.game.damage.EntityData;
import me.hapyl.fight.game.effect.ActiveGameEffect;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.gamemode.CFGameMode;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.stats.StatContainer;
import me.hapyl.fight.game.stats.StatType;
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
import org.bukkit.*;
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
import java.awt.Color;
import java.util.Map;

/**
 * This class controls all in-game player data.
 *
 * <b>A single instance should exist per game bases and cleared after the game ends.</b>
 */
public class GamePlayer implements IGamePlayer {

    public static final long COMBAT_TAG_DURATION = 5000L;

    private final Heroes enumHero;
    private final Hero hero; // this represents hero that player locked in game with, cannot be changed
    private final Skins skin;
    private final StatContainer stats;
    private final TalentQueue talentQueue;
    private final PlayerAttributes attributes;
    private boolean wasHit; // Used to check if player was hit by custom damage
    @Nonnull
    private Player player;
    @Nonnull
    private PlayerProfile profile;
    private double health;
    private double shield;
    @Nonnull
    private PlayerState state;
    private boolean valid = true; // valid means if a game player is being used somewhere, should probably rework how this works
    private boolean canMove;
    private int ultPoints;
    private double ultimateModifier;
    private long lastMoved;
    private long combatTag;
    private int killStreak;
    private InputTalent inputTalent;

    @SuppressWarnings("all")
    public GamePlayer(@Nonnull PlayerProfile profile, @Nonnull Heroes enumHero) {
        this.attributes = new PlayerAttributes(this, enumHero.getHero().getAttributes());
        this.profile = profile;
        this.player = profile.getPlayer();
        this.enumHero = enumHero;
        this.hero = enumHero.getHero();
        this.health = attributes.get(AttributeType.HEALTH);
        this.state = PlayerState.ALIVE;
        this.ultimateModifier = 1.0d;
        this.talentQueue = new TalentQueue(this);
        this.stats = new StatContainer(this);
        this.lastMoved = System.currentTimeMillis();
        this.combatTag = 0L;
        this.skin = PlayerDatabase.getDatabase(player).getHeroEntry().getSkin(enumHero);
        this.canMove = true;
        this.wasHit = false;

        // supply to profile
        profile.setGamePlayer(this);
    }

    @Nonnull
    @Override
    public PlayerAttributes getAttributes() {
        return attributes;
    }

    public void resetPlayer(Ignore... ignores) {
        final EntityData playerData = EntityData.getEntityData(player);
        if (isNotIgnored(ignores, Ignore.DAMAGER)) {
            playerData.setLastDamager(null);
        }
        if (isNotIgnored(ignores, Ignore.DAMAGE_CAUSE)) {
            playerData.setLastDamageCause(null);
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
        player.setVisualFire(false);
        player.setFlying(false);
        player.setSaturation(0.0f);
        player.setFoodLevel(20);
        player.setInvulnerable(false);
        player.setArrowsInBody(0);
        player.setWalkSpeed((float) attributes.get(AttributeType.SPEED));
        player.setMaximumNoDamageTicks(20);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));

        // Reset attributes
        resetAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE, 0.0d);
        resetAttribute(Attribute.GENERIC_ATTACK_SPEED, 4.0d);
        resetAttribute(Attribute.GENERIC_ATTACK_DAMAGE, 1.0d);

        playerData.getGameEffects().values().forEach(ActiveGameEffect::forceStop);

        // Reset attributes
        attributes.reset();

        playerData.getDamageTaken().clear();
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
    public PlayerInventory getInventory() {
        return player.getInventory();
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
        return getUltimateString(ChatColor.AQUA);
    }

    public String getUltimateString(ChatColor readyColor) {
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
            return readyColor + "&lREADY";
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

    @Nonnull
    public EntityData getData() {
        return EntityData.getEntityData(player);
    }

    public void addEffect(GameEffectType type, int ticks, boolean override) {
        getData().addEffect(type, ticks, override);
    }

    public Map<GameEffectType, ActiveGameEffect> getActiveEffects() {
        return getData().getGameEffects();
    }

    public boolean hasEffect(GameEffectType type) {
        return getData().hasEffect(type);
    }

    @Nonnull
    public StatContainer getStats() {
        return stats;
    }

    public void clearEffects() {
        getData().clearEffects();
    }

    public void removeEffect(GameEffectType type) {
        getData().removeEffect(type);
    }

    public void clearEffect(GameEffectType type) {
        getData().clearEffect(type);
    }

    public boolean isAlive() {
        return state == PlayerState.ALIVE;
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
        EntityData.damage(player, damage, damager, cause);
    }

    /**
     * This should only be called in the calculations, do not call it otherwise.
     */
    public void decreaseHealth(double damage, @Nullable LivingEntity damager) {
        decreaseHealth(damage);
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
        player.setHealth(Numbers.clamp(40.0d * health / getMaxHealth(), getMinHealth(), getMaxHealth()));
        //player.setHealth(Math.max(0.5d, 40.0d * health / maxHealth));
    }

    public void interrupt() {
        final PlayerInventory inventory = player.getInventory();
        inventory.setHeldItemSlot(inventory.firstEmpty());

        ReflectPacket.wrapAndSend(new PacketPlayOutAnimation(Reflect.getMinecraftPlayer(player), 1), player);

        GameTask.runLater(() -> {
            inventory.setHeldItemSlot(0);
        }, 1);

        // Fx
        playSound(Sound.ENTITY_ELDER_GUARDIAN_CURSE, 2.0f);
        playSound(Sound.ENCHANT_THORNS_HIT, 0.0f);
    }

    public void heal(double amount) {
        this.health = Numbers.clamp(health + amount, 0.5d, getMaxHealth());
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

        state = PlayerState.DEAD;
        player.setGameMode(GameMode.SPECTATOR);

        PlayerLib.playSound(player, Sound.ENTITY_BLAZE_DEATH, 2.0f);
        Chat.sendTitle(player, "&c&lYOU DIED", "", 5, 25, 10);

        triggerOnDeath();

        final EntityData data = EntityData.getEntityData(player);

        // Award killer coins for kill
        if (data.getLastDamager() != null) {
            final Player killer = data.getLastDamager(Player.class);

            if (killer != null) {
                final GamePlayer gameKiller = GamePlayer.getExistingPlayer(killer);
                if (gameKiller != null && player != killer) {
                    final IGameInstance gameInstance = Manager.current().getCurrentGame();
                    final StatContainer killerStats = gameKiller.getStats();

                    killerStats.addValue(StatType.KILLS, 1);
                    gameKiller.getTeam().kills++;

                    // Check for first blood
                    if (gameInstance.getTotalKills() == 1) {
                        Achievements.FIRST_BLOOD.complete(gameKiller.getTeam());
                    }

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

        final LivingEntity lastDamager = data.getLastDamager();

        // Award assists
        data.getDamageTaken().forEach((damager, damage) -> {
            if (damager == lastDamager || damager == player/*should not happen*/) {
                return;
            }

            final double percentDamageDealt = damage / getMaxHealth();
            final StatContainer damagerStats = GamePlayer.getPlayer(damager).getStats();

            if (damagerStats == null || percentDamageDealt < 0.5d) {
                return;
            }

            Award.PLAYER_ASSISTED.award(damager);
            damagerStats.addValue(StatType.ASSISTS, 1);
        });

        stats.addValue(StatType.DEATHS, 1);

        // Broadcast the death message
        final double distanceToDamager = Nulls.getIfNotNull(
                lastDamager,
                f -> lastDamager.getLocation().distance(player.getLocation()),
                0.0d
        );

        final String distanceSuffix = distanceToDamager >= 20.0d ? " (from %.2f meters away!)".formatted(distanceToDamager) : "";

        final String deathMessage =
                new Gradient(concat("☠ %s ".formatted(player.getName()), getRandomDeathMessage(), lastDamager) + distanceSuffix)
                        .rgb(
                                new Color(160, 0, 0),
                                new Color(255, 51, 51),
                                Interpolators.LINEAR
                        );

        // Send death info to manager
        final GameInstance gameInstance = Manager.current().getGameInstance(); /*ignore deprecation*/
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

        // KEEP LAST
        resetPlayer(Ignore.GAME_MODE);
        Chat.broadcast(deathMessage);
    }

    public void triggerOnDeath() {
        final IGameInstance currentGame = Manager.current().getCurrentGame();

        currentGame.getMap().getMap().onDeath(player);
        getHero().onDeath(player);
        attributes.onDeath(player);
        executeTalentsOnDeath();

        currentGame.getActiveHeroes().forEach(heroes -> {
            final EntityData data = getData();

            heroes.getHero().onDeathGlobal(player, data.getLastDamager(), data.getLastDamageCause());
        });
    }

    public DeathMessage getRandomDeathMessage() {
        return getLastDamageCause().getRandomIfMultiple();
    }

    @Override
    @Nonnull
    public EnumDamageCause getLastDamageCause() {
        return getData().getLastDamageCauseNonNull();
    }

    public void setLastDamageCause(EnumDamageCause lastDamageCause) {
        getData().setLastDamageCause(lastDamageCause);
        ;
    }

    @Override
    public double getMaxHealth() {
        return attributes.get(AttributeType.HEALTH);
    }

    @Override
    public boolean isRespawning() {
        return state == PlayerState.RESPAWNING;
    }

    @Override
    public String getHealthFormatted() {
        return String.valueOf(Math.ceil(health));
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
        return getData().getLastDamager();
    }

    public void setLastDamager(LivingEntity lastDamager) {
        if (lastDamager != null) {
            getData().setLastDamager(lastDamager);
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

    @Nonnull
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
        return state == PlayerState.DEAD;
    }

    public void setDead(boolean dead) {
        state = PlayerState.DEAD;

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
        return state == PlayerState.SPECTATOR;
    }

    public void setSpectator(boolean spectator) {
        state = PlayerState.SPECTATOR;
        player.setGameMode(GameMode.SPECTATOR);
    }

    public void respawn() {
        resetPlayer(Ignore.DAMAGE_CAUSE, Ignore.DAMAGER, Ignore.GAME_MODE);

        state = PlayerState.ALIVE;
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

    // Use this as 'onStop()'
    public void setValid(boolean flag) {
        valid = flag;
        talentQueue.clear();
    }

    public PlayerDatabase getDatabase() {
        return profile.getDatabase();
    }

    public PlayerProfile getProfile() {
        return profile;
    }

    public void respawnIn(int tick) {
        state = PlayerState.RESPAWNING;

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
        this.profile = Manager.current().getOrCreateProfile(player);
    }

    @Override
    public String toString() {
        return "GamePlayer{" + player.getName() + "}";
    }

    @Override
    public double getMinHealth() {
        return 0.5d;
    }

    private void resetAttribute(Attribute attribute, double value) {
        Nulls.runIfNotNull(player.getAttribute(attribute), t -> t.setBaseValue(value));
    }

    private String replaceColor(String string, ChatColor color) {
        return string.replace("$", color.toString());
    }

    private boolean isNotIgnored(Ignore[] ignores, Ignore target) {
        for (final Ignore ignore : ignores) {
            if (ignore == target) {
                return false;
            }
        }
        return true;
    }

    private String concat(String original, DeathMessage message, Entity killer) {
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

        // complex
        executeOnDeathIfTalentIsNotNull(hero.getThirdTalent());
        executeOnDeathIfTalentIsNotNull(hero.getFourthTalent());
        executeOnDeathIfTalentIsNotNull(hero.getFifthTalent());
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
     * <p>
     * fixme -> Add nullability or use Optional, this system sucks COCK (RISE PENIS)
     * Adding nullability will remove need for interfaces and updating NULL instance every FUCKING TIME
     * something is added.
     * Well, a base interface should probably still be used, but this NULL GAME PLAYER
     * is annoying thing to deal why did I implement it this way, just check for the fucking null, or, well,
     * use OPTIONAL.
     * Now the question is, if GameEntity is the base class, how should it be stored, still in game instance?
     * Should GamePlayer and GameEntity stored in the same map and use casting and checking to retrieve them?
     * -- (Considering GamePlayer extends GameEntity)
     * GamePlayer's are created upon game instance, what about GameEntity?
     * <p>
     * Or maybe separate shit into GameData or EntityData or something !!
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
        EntityData.getEntityData(entity).damageTick(entity, damage, damager, cause, tick);
    }

    @Super
    public static void damageEntity(LivingEntity entity, double damage, LivingEntity damager, EnumDamageCause cause) {
        EntityData.getEntityData(entity).damage(entity, damage, damager, cause);
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

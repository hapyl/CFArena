package me.hapyl.fight.game.entity;

import me.hapyl.fight.CF;
import me.hapyl.fight.database.Award;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.game.*;
import me.hapyl.fight.game.achievement.Achievements;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.cosmetic.Cosmetics;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Type;
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
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.math.Numbers;
import me.hapyl.spigotutils.module.math.Tick;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.reflect.Reflect;
import me.hapyl.spigotutils.module.reflect.ReflectPacket;
import me.hapyl.spigotutils.module.reflect.glow.Glowing;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import net.minecraft.network.protocol.game.PacketPlayOutAnimation;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * This class controls all in-game player data.
 * <p>
 * <b>A single instance should exist per game bases and cleared after the game ends.</b>
 */
public class GamePlayer extends GameEntity {

    public static final long COMBAT_TAG_DURATION = 5000L;

    private final StatContainer stats;
    private final TalentQueue talentQueue;
    @Nonnull
    private PlayerProfile profile;
    private int ultPoints;
    private double ultimateModifier;
    private long lastMoved;
    private long combatTag;
    private int killStreak;
    private InputTalent inputTalent;
    private double cdModifier;

    @SuppressWarnings("all")
    public GamePlayer(@Nonnull PlayerProfile profile) {
        super(profile.getPlayer());
        this.profile = profile;
        this.ultimateModifier = 1.0d;
        this.talentQueue = new TalentQueue(this);
        this.stats = new StatContainer(this);
        this.lastMoved = System.currentTimeMillis();
        this.combatTag = 0L;
        this.cdModifier = 1.0d;
        this.attributes = new EntityAttributes(this, profile.getHeroHandle().getAttributes());
    }

    public double getCooldownModifier() {
        return cdModifier;
    }

    public void setCooldownModifier(double cdModifier) {
        this.cdModifier = cdModifier;
    }

    @Override
    public void setState(@Nonnull EntityState state) {
        super.setState(state);

    }

    public void resetPlayer(Ignore... ignores) {
        final Player player = getEntity();

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
        player.getActivePotionEffects().forEach(effect -> this.entity.removePotionEffect(effect.getType()));

        // Reset attributes
        resetAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE, 0.0d);
        resetAttribute(Attribute.GENERIC_ATTACK_SPEED, 4.0d);
        resetAttribute(Attribute.GENERIC_ATTACK_DAMAGE, 1.0d);

        getData().getGameEffects().values().forEach(ActiveGameEffect::forceStop);

        // Reset attributes
        attributes.reset();

        getData().getDamageTaken().clear();
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

    @Override
    public boolean shouldDie() {
        return getEntity().getGameMode() == GameMode.CREATIVE;
    }

    @Override
    public void onStop(GameInstance instance) {
        final Heroes hero = getEnumHero();
        final StatContainer stats = getStats();
        final Player player = getEntity();

        Glowing.stopGlowing(player);
        updateScoreboardTeams(true);
        resetPlayer();
        getPlayer().setWalkSpeed(0.2f);

        Utils.showPlayer(player.getPlayer());

        // Keep winner in survival, so it's clear for them that they have won
        final boolean isWinner = instance.isWinner(player.getPlayer());
        if (!isWinner) {
            player.setGameMode(GameMode.SPECTATOR);
        }
        else {
            stats.markAsWinner();
        }

        // Reset game player
        getProfile().resetGamePlayer();

        // Save stats
        getDatabase().getStatistics().fromPlayerStatistic(hero, stats);
        hero.getStats().fromPlayerStatistic(stats);
    }

    @Override
    public void onDeath() {
    }

    @Override
    public void remove() {
        // don't remove players
    }

    @Override
    public void die(boolean force) {
        super.die(force);

        final Player player = getEntity();
        player.setGameMode(GameMode.SPECTATOR);

        PlayerLib.playSound(player, Sound.ENTITY_BLAZE_DEATH, 2.0f);
        Chat.sendTitle(player, "&c&lYOU DIED", "", 5, 25, 10);

        triggerOnDeath();

        // Award killer coins for kill
        if (entityData.getLastDamager() != null) {
            final Player killer = entityData.getLastDamager(Player.class);

            if (killer != null) {
                final GamePlayer gameKiller = GamePlayer.getExistingPlayer(killer);
                if (gameKiller != null && entity != killer) {
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
                        killCosmetic.getCosmetic().onDisplay(new Display(killer, entity.getLocation()));
                    }
                }
            }
        }

        final GameEntity lastDamager = entityData.getLastDamager();

        // Award assists
        entityData.getDamageTaken().forEach((damager, damage) -> {
            if (lastDamager.is(damager) || damager == entity/*should not happen*/) {
                return;
            }

            final double percentDamageDealt = damage / getMaxHealth();
            final StatContainer damagerStats = CF.getOrCreatePlayer(damager).getStats();

            if (percentDamageDealt < 0.5d) {
                return;
            }

            Award.PLAYER_ASSISTED.award(damager);
            damagerStats.addValue(StatType.ASSISTS, 1);
        });

        stats.addValue(StatType.DEATHS, 1);

        final String deathMessage = getRandomDeathMessage().format(
                this,
                lastDamager,
                lastDamager == null ? 0 : lastDamager.getLocation().distance(entity.getLocation())
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
        resetPlayer(GamePlayer.Ignore.GAME_MODE);
        Chat.broadcast(deathMessage);
    }

    @Override
    public void setHealth(double d) {
        super.setHealth(d);
        updateHealth();
    }

    @Nonnull
    public PlayerInventory getInventory() {
        return getEntity().getInventory();
    }

    @Nonnull
    public TalentQueue getTalentQueue() {
        return talentQueue;
    }

    public int getKillStreak() {
        return killStreak;
    }

    public void markLastMoved() {
        this.lastMoved = System.currentTimeMillis();
    }

    @Override
    public void heal(double amount) {
        super.heal(amount);
        updateHealth();
    }

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
            final long durationLeft = getHero().getUltimateDurationLeft(player);
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

    public long getLastMoved() {
        return lastMoved;
    }

    @Nonnull
    public StatContainer getStats() {
        return stats;
    }

    public void clearEffects() {
        getData().clearEffects();
    }

    public void clearEffect(GameEffectType type) {
        getData().clearEffect(type);
    }

    public boolean isUltimateReady() {
        return this.ultPoints >= getHero().getUltimate().getCost();
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

    public void updateScoreboardTeams(boolean toLobby) {
        profile.getScoreboardTeams().populate(toLobby);
    }

    public void updateHealth() {
        // update player visual health
        entity.setMaxHealth(40.d);
        entity.setHealth(Numbers.clamp(40.0d * health / getMaxHealth(), getMinHealth(), getMaxHealth()));
        //player.setHealth(Math.max(0.5d, 40.0d * health / maxHealth));
    }

    public void interrupt() {
        final Player player = getEntity();
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

    public void triggerOnDeath() {
        final IGameInstance currentGame = Manager.current().getCurrentGame();
        final Player player = getEntity();

        currentGame.getMap().getMap().onDeath(player);
        getHero().onDeath(player);
        attributes.onDeath(player);
        executeTalentsOnDeath();

        CF.getActiveHeroes().forEach(heroes -> {
            final EntityData data = getData();
            heroes.getHero().onDeathGlobal(this, data.getLastDamager(), data.getLastDamageCause());
        });
    }

    public DeathMessage getRandomDeathMessage() {
        return getLastDamageCause().getRandomIfMultiple();
    }

    public boolean isRespawning() {
        return state == EntityState.RESPAWNING;
    }

    public UltimateTalent getUltimate() {
        return getHero().getUltimate();
    }

    public void addUltimatePoints(int points) {
        final Player player = getEntity();
        // cannot give points if using ultimate or dead
        if (getHero().isUsingUltimate(player) || !this.isAlive() || this.ultPoints >= this.getUltPointsNeeded()) {
            return;
        }

        this.ultPoints = Numbers.clamp(this.ultPoints + points, 0, getHero().getUltimate().getCost());

        // show once at broadcast
        if (this.ultPoints >= this.getUltPointsNeeded()) {
            Chat.sendMessage(player, "&b&l※ &bYou ultimate is ready! Press &e&lF &bto use it!");
            Chat.sendTitle(player, "", "&aYou ultimate is ready!", 5, 15, 5);
            PlayerLib.playSound(player, Sound.BLOCK_CONDUIT_DEACTIVATE, 2.0f);
        }
    }

    @Nonnull
    public Player getPlayer() {
        return (Player) entity;
    }

    @Nonnull
    public Hero getHero() {
        return profile.getHeroHandle();
    }

    @Nonnull
    @Override
    public Player getEntity() {
        return (Player) super.getEntity();
    }

    @Nonnull
    public Heroes getEnumHero() {
        return profile.getHero();
    }

    public long getCombatTag() {
        final long timeLeft = (combatTag + COMBAT_TAG_DURATION) - System.currentTimeMillis();
        return timeLeft < 0 ? 0 : timeLeft;
    }

    public void markCombatTag() {
        this.combatTag = System.currentTimeMillis();
    }

    public boolean isCombatTagged() {
        return getCombatTag() > 0;
    }

    public boolean isNativeDamage() {
        return !wasHit;
    }

    @Nullable
    public InputTalent getInputTalent() {
        return inputTalent;
    }

    public void setInputTalent(@Nullable InputTalent inputTalent) {
        if (inputTalent == null) {
            getEntity().sendTitle("", "", 0, 0, 10);
        }
        this.inputTalent = inputTalent;
    }

    public boolean isSpectator() {
        return state == EntityState.SPECTATOR;
    }

    public void setSpectator(boolean spectator) {
        state = EntityState.SPECTATOR;
        getEntity().setGameMode(GameMode.SPECTATOR);
    }

    public void respawnIn(int tick) {
        state = EntityState.RESPAWNING;

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

    public void respawn() {
        resetPlayer(Ignore.GAME_MODE);
        final Player player = getEntity();

        final Hero hero = getHero();

        state = EntityState.ALIVE;
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

        BukkitUtils.mergePitchYaw(entity.getLocation(), location);
        sendTitle("&aRespawned!", "", 0, 20, 5);
        entity.teleport(location);

        addPotionEffect(PotionEffectType.BLINDNESS, 20, 1);
    }

    public int getUltPointsNeeded() {
        return getHero().getUltimate().getCost();
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

    public PlayerDatabase getDatabase() {
        return profile.getDatabase();
    }

    public PlayerProfile getProfile() {
        return profile;
    }

    public GameTeam getTeam() {
        return GameTeam.getPlayerTeam(getPlayer());
    }

    public boolean isTeammate(GamePlayer player) {
        return GameTeam.isTeammate(this, player);
    }

    public void setHandle(Player player) {
        this.entity = player;
        this.profile = Manager.current().getOrCreateProfile(player);
    }

    @Nonnull
    public Location getEyeLocation() {
        return entity.getEyeLocation();
    }

    public boolean hasCooldown(Material material) {
        return getEntity().hasCooldown(material);
    }

    public int getCooldown(Material material) {
        return getEntity().getCooldown(material);
    }

    public void setCooldown(Material material, int i) {
        getEntity().setCooldown(material, (int) Math.max(i * cdModifier, 0)); // not calling static method for obvious reasons
    }

    public boolean hasMovedInLast(long millis) {
        return getLastMoved() != 0 && (System.currentTimeMillis() - getLastMoved()) < millis;
    }

    public boolean isBlocking() {
        return getPlayer().isBlocking();
    }

    public boolean isSneaking() {
        return getPlayer().isSneaking();
    }

    private void resetAttribute(Attribute attribute, double value) {
        Nulls.runIfNotNull(entity.getAttribute(attribute), t -> t.setBaseValue(value));
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

    private void executeTalentsOnDeath() {
        final Hero hero = getHero();

        executeOnDeathIfTalentIsNotNull(hero.getFirstTalent());
        executeOnDeathIfTalentIsNotNull(hero.getSecondTalent());

        // complex
        executeOnDeathIfTalentIsNotNull(hero.getThirdTalent());
        executeOnDeathIfTalentIsNotNull(hero.getFourthTalent());
        executeOnDeathIfTalentIsNotNull(hero.getFifthTalent());
    }

    private void executeOnDeathIfTalentIsNotNull(Talent talent) {
        if (talent != null) {
            talent.onDeath(getEntity());
        }
    }

    /**
     * Sets player's material cooldown with support of {@link GamePlayer#cdModifier}.
     * If there is no game player for a given player, the native {@link Player#setCooldown(Material, int)} will be used.
     *
     * @param player   - Player.
     * @param material - Material.
     * @param i        - New cooldown.
     */
    public static void setCooldown(@Nonnull Player player, @Nonnull Material material, int i) {
        final GamePlayer gamePlayer = GamePlayer.getExistingPlayer(player);

        // if no player, use native set method
        if (gamePlayer == null) {
            player.setCooldown(material, i);
            return;
        }

        gamePlayer.setCooldown(material, i);
    }

    /**
     * Scaled the cooldown by cooldown modifier.
     *
     * @param player - Player.
     * @param cd     - Cooldown.
     * @return the scaled cooldown.
     */
    public static int scaleCooldown(Player player, int cd) {
        return (int) Math.max(cd * getPlayer(player).getCooldownModifier(), 0);
    }

    /**
     * Returns a player from existing instance, no matter if they're alive or not.
     *
     * @param player - bukkit player.
     * @return GamePlayer instance if there is a GameInstance, otherwise null.
     */
    @Nullable
    public static GamePlayer getExistingPlayer(Player player) {
        final PlayerProfile profile = PlayerProfile.getProfile(player);

        if (profile == null) {
            return null;
        }

        return profile.getGamePlayer();
    }

    /**
     * Returns either an actual GamePlayer instance if there is a GameInstance, otherwise AbstractGamePlayer.
     *
     * @param player bukkit player.
     * @return either an actual GamePlayer instance if there is a GameInstance, otherwise AbstractGamePlayer.
     * @deprecated Please migrate to using {@link #getPlayerOptional(Player)} or {@link #getExistingPlayer(Player)}.
     */
    @Nonnull
    @Deprecated // use CF.getOrCreatePlayer()
    public static GamePlayer getPlayer(Player player) {
        return CF.getOrCreatePlayer(player);
    }

    @Nonnull
    public static Optional<GamePlayer> getPlayerOptional(Player player) {
        final GamePlayer gamePlayer = getExistingPlayer(player);
        return gamePlayer == null ? Optional.empty() : Optional.of(gamePlayer);
    }

    @Override
    public String toString() {
        return "GamePlayer{" + getName() + "}";
    }

    public enum Ignore {
        GAME_MODE,
        COOLDOWNS
    }

}

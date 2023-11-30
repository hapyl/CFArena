package me.hapyl.fight.game.entity;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import me.hapyl.fight.database.Award;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.event.io.DamageInput;
import me.hapyl.fight.event.io.DamageOutput;
import me.hapyl.fight.game.*;
import me.hapyl.fight.game.achievement.Achievements;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.cosmetic.Cosmetics;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.game.effect.ActiveGameEffect;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.entity.shield.Shield;
import me.hapyl.fight.game.gamemode.CFGameMode;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.loadout.HotbarLoadout;
import me.hapyl.fight.game.loadout.HotbarSlots;
import me.hapyl.fight.game.playerskin.PlayerSkin;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.setting.Settings;
import me.hapyl.fight.game.stats.StatContainer;
import me.hapyl.fight.game.stats.StatType;
import me.hapyl.fight.game.talents.*;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.PlayerGameTask;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.game.weapons.RangeWeapon;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.ItemStacks;
import me.hapyl.fight.util.Nulls;
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
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * This class controls all in-game player data.
 * <p>
 * <b>A single instance should exist per game bases and cleared after the game ends.</b>
 */
public class GamePlayer extends LivingGameEntity implements Ticking {

    public static final long COMBAT_TAG_DURATION = 5000L;

    private final StatContainer stats;
    private final TalentQueue talentQueue;
    private final Set<PlayerGameTask> taskSet;
    private final TalentLock talentLock;

    public boolean blockDismount;
    @Nonnull
    private PlayerProfile profile;
    private int ultPoints;
    private double ultimateModifier;
    private long lastMoved;
    private long combatTag;
    private int killStreak;
    private InputTalent inputTalent;

    private Shield shield;

    @SuppressWarnings("all")
    public GamePlayer(@Nonnull PlayerProfile profile) {
        super(profile.getPlayer());

        this.profile = profile;
        this.ultimateModifier = 1.0d;
        this.talentQueue = new TalentQueue(this);
        this.stats = new StatContainer(this);
        this.lastMoved = System.currentTimeMillis();
        this.combatTag = 0L;
        this.attributes = new EntityAttributes(this, profile.getHeroHandle().getAttributes());
        this.taskSet = Sets.newHashSet();
        this.shield = null;
        this.talentLock = new TalentLock(this, profile.getHeroHandle());

        startTicking();
    }

    public int getTalentLock(@Nonnull HotbarSlots slot) {
        return talentLock.getLock(slot);
    }

    @Nonnull
    public TalentLock getTalentLock() {
        return talentLock;
    }

    @Override
    public void tick() {
        if (isDeadOrRespawning()) {
            return;
        }

        talentLock.tick();
    }

    @Override
    public void setState(@Nonnull EntityState state) {
        super.setState(state);
    }

    public void resetPlayer(Ignore... ignores) {
        final Player player = getEntity();

        killStreak = 0;
        combatTag = 0;
        talentLock.reset();

        markLastMoved();
        setHealth(getMaxHealth());

        player.setLastDamageCause(null);
        player.getInventory().clear();
        player.setMaxHealth(40.0d); // why deprecate
        player.setHealth(40.0d);
        player.setAbsorptionAmount(0.0d);
        player.setFireTicks(0);
        player.setVisualFire(false);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setSaturation(0.0f);
        player.setFoodLevel(20);
        player.setInvulnerable(false);
        player.setArrowsInBody(0);
        player.setGlowing(false);
        player.setWalkSpeed((float) attributes.get(AttributeType.SPEED));
        player.setMaximumNoDamageTicks(20);
        player.getActivePotionEffects().forEach(effect -> this.entity.removePotionEffect(effect.getType()));

        // Reset attributes
        resetAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE, 0.0d);
        resetAttribute(Attribute.GENERIC_ATTACK_SPEED, 4.0d);
        resetAttribute(Attribute.GENERIC_ATTACK_DAMAGE, 1.0d);

        setAttributeValue(Attribute.GENERIC_ARMOR, 0.0d);

        getData().getGameEffects().values().forEach(ActiveGameEffect::forceStop);

        // Reset attributes
        attributes.reset();

        setOutline(Outline.CLEAR);

        getData().getDamageTaken().clear();
        wasHit = false;
        inputTalent = null;
        shield = null;

        if (isNotIgnored(ignores, Ignore.GAME_MODE)) {
            player.setGameMode(GameMode.SURVIVAL);
        }

        // If a player is in spectator - forcefully enable flight
        if (player.getGameMode() == GameMode.SPECTATOR) {
            player.setAllowFlight(true);
            player.setFlying(true);
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
    public void remove() {
        // don't remove player
    }

    @Override
    public boolean shouldDie() {
        return getEntity().getGameMode() == GameMode.CREATIVE;
    }

    @Override
    public void onStop(@Nonnull GameInstance instance) {
        final Heroes hero = getEnumHero();
        final StatContainer stats = getStats();
        final Player player = getEntity();

        Glowing.stopGlowing(player);
        resetPlayer();
        getPlayer().setWalkSpeed(0.2f);

        // Reset skin if was applied
        final PlayerSkin skin = hero.getHero().getSkin();

        if (Settings.USE_SKINS_INSTEAD_OF_ARMOR.isEnabled(player) && skin != null) {
            PlayerSkin.reset(player);
        }

        show();

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

        // Update scoreboard
        GameTask.runLater(() -> {
            updateScoreboardTeams(true);
        }, 5);
    }

    @Override
    public void onDeath() {
        final GameEntity lastDamager = entityData.getLastDamager();

        if (!(lastDamager instanceof GamePlayer lastPlayerDamager)) {
            return;
        }

        lastPlayerDamager.heal(getMaxHealth() * 0.3d);
    }

    @Nullable
    @Override
    public DamageOutput onDamageTaken(@Nonnull DamageInput input) {
        final GameTeam team = getTeam();
        final DamageOutput output = new DamageOutput(0.0d);

        team.getPlayers().forEach(player -> {
            if (player.equals(this)) {
                return;
            }

            player.onDamageTakenByTeammate(player, input);
        });

        // TODO (hapyl): 004, Aug 4:
        return null;
    }

    @Nullable
    public DamageOutput onDamageTakenByTeammate(@Nonnull GamePlayer teammate, @Nonnull DamageInput input) {
        return null;
    }

    @Override
    public void die(boolean force) {
        super.die(force);

        final Player player = getEntity();
        player.setGameMode(GameMode.SPECTATOR);

        playSound(Sound.ENTITY_BLAZE_DEATH, 2.0f);
        sendTitle("&c&lʏᴏᴜ ᴅɪᴇᴅ", "", 5, 25, 10);

        triggerOnDeath();

        // Award killer coins for kill
        final GameEntity lastDamager = entityData.getLastDamager();
        if (lastDamager instanceof GamePlayer gameKiller) {
            final Player killer = gameKiller.getPlayer();

            if (entity != killer) {
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
                Award.PLAYER_ELIMINATION.award(gameKiller);

                // Display cosmetics
                final Cosmetics killCosmetic = Cosmetics.getSelected(killer, Type.KILL);
                if (killCosmetic != null) {
                    killCosmetic.getCosmetic().onDisplay0(new Display(killer, entity.getLocation()));
                }
            }
        }

        // Award assists
        entityData.getDamageTaken().forEach((damager, damage) -> {
            if (lastDamager == null || lastDamager.is(damager) || damager == entity/*should not happen*/) {
                return;
            }

            final double percentDamageDealt = damage / getMaxHealth();
            final GamePlayer damagerPlayer = CF.getPlayer(damager);

            if (damagerPlayer != null) {
                final StatContainer damagerStats = damagerPlayer.getStats();

                if (percentDamageDealt < 0.5d) {
                    return;
                }

                Award.PLAYER_ASSISTED.award(this);
                damagerStats.addValue(StatType.ASSISTS, 1);
            }
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
            if (mode.isAllowRespawn() && mode.shouldRespawn(this)) {
                respawnIn(mode.getRespawnTime() + 1);
            }
        }

        // Display death cosmetics
        final Cosmetics deathCosmetic = Cosmetics.getSelected(player, Type.DEATH);
        if (deathCosmetic != null) {
            deathCosmetic.getCosmetic().onDisplay0(new Display(player, player.getLocation()));
        }

        // Clear tasks
        taskSet.forEach(PlayerGameTask::cancelBecauseOfDeath);
        taskSet.clear();

        // KEEP LAST
        resetPlayer(GamePlayer.Ignore.GAME_MODE);
        Chat.broadcast(deathMessage);
    }

    @Override
    public void setHealth(double d) {
        super.setHealth(d);
        updateHealth();
    }

    /**
     * Gets player's inventory.
     *
     * @return player's inventory.
     * @see #setItem(HotbarSlots, ItemStack)
     * @see #snapTo(HotbarSlots)
     * @deprecated Not deprecated, just a hands up that setting items should be done using {@link HotbarSlots}.
     */
    @Deprecated
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
        final UltimateTalent ultimate = getUltimate();
        final String pointsString = "&b&l%s&b/&b&l%s".formatted(getUltPoints(), getUltPointsNeeded());

        if (getHero().isUsingUltimate(this)) {
            final long durationLeft = getHero().getUltimateDurationLeft(this);

            return "&b&lIN USE &b(%s&b)".formatted(BukkitUtils.roundTick(Tick.fromMillis(durationLeft)) + "s");
        }

        if (ultimate.hasCd(this)) {
            return "&7%s &b(%ss)".formatted(pointsString, BukkitUtils.roundTick(ultimate.getCdTimeLeft(this)));
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

    public void updateScoreboardTeams(boolean toLobby) {
        profile.getLocalTeamManager().updateAll(toLobby);
    }

    @Override
    public void decreaseHealth(@Nonnull DamageInstance instance) {
        final Player player = getPlayer();

        if (shield != null) {
            final double capacity = shield.getCapacity();
            double toAbsorb = instance.damage;

            if (toAbsorb - capacity > 0.0d) {
                toAbsorb -= (toAbsorb - capacity);
            }

            instance.damage -= toAbsorb;
            shield.takeDamage0(toAbsorb);

            final double capacityAfterHit = shield.getCapacity();

            // Shield broke
            if (capacityAfterHit <= 0.0d) {
                player.setAbsorptionAmount(0.0d);

                shield.onBreak();
                shield = null;
            }
        }

        super.decreaseHealth(instance);
        updateHealth();
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

        GameTask.runLater(this::snapToWeapon, 1);

        // Fx
        playSound(Sound.ENTITY_ELDER_GUARDIAN_CURSE, 2.0f);
        playSound(Sound.ENCHANT_THORNS_HIT, 0.0f);
    }

    public void triggerOnDeath() {
        final IGameInstance currentGame = Manager.current().getCurrentGame();
        final Player player = getEntity();
        final Hero hero = getHero();
        final Weapon weapon = hero.getWeapon();

        currentGame.getEnumMap().getMap().onDeath(this);
        hero.onDeath(this);
        attributes.onDeath(this);
        executeTalentsOnDeath();

        if (weapon instanceof RangeWeapon rangeWeapon) {
            rangeWeapon.onDeath(this);
        }

        weapon.getAbilities().forEach(ability -> ability.stopCooldown(this));

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
        if (getHero().isUsingUltimate(this) || !this.isAlive() || this.ultPoints >= this.getUltPointsNeeded()) {
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
    public String getScoreboardName() {
        return getName();
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

    @Override
    public float getWalkSpeed() {
        return getPlayer().getWalkSpeed();
    }

    @Override
    public void setWalkSpeed(double value) {
        getPlayer().setWalkSpeed((float) value);
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

                sendTitle("&e&lʀᴇsᴘᴀᴡɴɪɴɢ", "&b&l" + CFUtils.decimalFormatTick(tickBeforeRespawn), 0, 25, 0);
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
        hero.setUsingUltimate(this, false);
        setHealth(this.getMaxHealth());

        player.getInventory().clear();
        equipPlayer(hero);

        hero.onRespawn(this);

        player.setGameMode(GameMode.SURVIVAL);

        // Add spawn protection
        addEffect(GameEffectType.RESPAWN_RESISTANCE, 60);

        // Respawn location
        final IGameInstance gameInstance = Manager.current().getCurrentGame();
        final Location location = gameInstance.getEnumMap().getMap().getLocation();

        BukkitUtils.mergePitchYaw(entity.getLocation(), location);
        sendTitle("&a&lʀᴇsᴘᴀᴡɴᴇᴅ!", "", 0, 20, 5);
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

    @Nonnull
    public PlayerProfile getProfile() {
        return profile;
    }

    @Nonnull
    public GameTeam getTeam() {
        final GameTeam team = GameTeam.getPlayerTeam(getPlayer());

        if (team == null) {
            throw new IllegalStateException("game has no team?");
        }

        return team;
    }

    public boolean isTeammate(GamePlayer player) {
        return GameTeam.isTeammate(this, player);
    }

    public boolean isTeammate(GameEntity entity) {
        return GameTeam.isTeammate(this, entity);
    }

    public void setHandle(Player player) {
        this.entity = player;
        this.profile = Manager.current().getOrCreateProfile(player);
    }

    public boolean hasCooldown(Material material) {
        return getEntity().hasCooldown(material);
    }

    public int getCooldown(Material material) {
        return getEntity().getCooldown(material);
    }

    public void setCooldown(Material material, int cd) {
        final double cdModifier = attributes.get(AttributeType.COOLDOWN_MODIFIER);

        getEntity().setCooldown(material, (int) Math.max(cd * cdModifier, 0));
    }

    public void setCooldownIgnoreModifier(Material material, int cd) {
        getEntity().setCooldown(material, cd);
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

    public void setSneaking(boolean b) {
        getPlayer().setSneaking(b);
    }

    public void sendPacket(@Nonnull PacketContainer packet) {
        ProtocolLibrary.getProtocolManager().sendServerPacket(getPlayer(), packet);
    }

    public <T> void spawnParticle(Particle particle, Location location, int amount, double x, double y, double z, float speed, T data) {
        getPlayer().spawnParticle(particle, location, amount, x, y, z, speed, data);
    }

    public void hideEntity(@Nonnull Entity entity) {
        getPlayer().hideEntity(Main.getPlugin(), entity);
    }

    public void showEntity(@Nonnull Entity entity) {
        getPlayer().showEntity(Main.getPlugin(), entity);
    }

    public void addTask(PlayerGameTask task) {
        taskSet.add(task);
    }

    @Override
    public String toString() {
        return "GamePlayer{" + getName() + "}";
    }

    @Nullable
    public Shield getShield() {
        return shield;
    }

    public void setShield(@Nullable Shield shield) {
        if (this.shield != null) {
            this.shield.onRemove();
        }

        this.shield = shield;

        if (shield != null) {
            getPlayer().setAbsorptionAmount(20.0d);
            shield.onCreate();
        }
    }

    public void removeShield() {
        this.shield = null;
    }

    @Nonnull
    public String formatTeamName(@Nonnull String prefix, @Nonnull String suffix) {
        final String firstLetterCaps = getTeam().getFirstLetterCaps();

        return prefix + firstLetterCaps + " &f" + getName() + suffix;
    }

    @Nonnull
    public String formatTeamNameScoreboardPosition(int position, @Nonnull String suffix) {
        return formatTeamName(" &e#" + position + " ", suffix);
    }

    public void setFlying(boolean b) {
        getPlayer().setFlying(b);
    }

    public float getFlySpeed() {
        return getPlayer().getFlySpeed();
    }

    public void setFlySpeed(float v) {
        getPlayer().setFlySpeed(v);
    }

    public boolean isOnGround() {
        return getPlayer().isOnGround();
    }

    public void setGameMode(GameMode gameMode) {
        getPlayer().setGameMode(gameMode);
    }

    public void swingMainHand() {
        getPlayer().swingMainHand();
    }

    @Nullable
    public EntityEquipment getEquipment() {
        return getPlayer().getEquipment();
    }

    public boolean isSwimming() {
        return getPlayer().isSwimming();
    }

    @Nullable
    public Block getTargetBlockExact(int maxDistance) {
        return getPlayer().getTargetBlockExact(maxDistance);
    }

    public void hide() {
        CFUtils.hidePlayer(getPlayer());
    }

    public void show() {
        CFUtils.showPlayer(getPlayer());
    }

    public void clearTitle() {
        getPlayer().resetTitle();
    }

    @Nonnull
    public Scoreboard getScoreboard() {
        return getPlayer().getScoreboard();
    }

    public boolean isInWater() {
        return getPlayer().isInWater();
    }

    public void stopCooldown(@Nonnull Material material) {
        getEntity().setCooldown(material, 0);
    }

    public boolean isAbleToUseAbilities() {
        return Manager.current().isGameInProgress();
    }

    public void cancelInputTalent() {
        if (inputTalent != null && !inputTalent.hasCd(this)) {
            sendTitle("&cCancelled", inputTalent.getName(), 0, 10, 5);
            playSound(Sound.ENTITY_HORSE_SADDLE, 0.75f);

            inputTalent.onCancel(this);
        }

        inputTalent = null;
    }

    public void snapToWeapon() {
        final int slot = profile.getHotbarLoadout().getInventorySlotBySlot(HotbarSlots.WEAPON);

        if (slot < 0 || slot > 9) {
            return;
        }

        getInventory().setHeldItemSlot(slot);
    }

    @Nullable
    public ItemStack getItem(@Nonnull HotbarSlots hotbarSlots) {
        final int slot = profile.getHotbarLoadout().getInventorySlotBySlot(hotbarSlots);

        return getInventory().getItem(slot);
    }

    public void setItem(@Nonnull HotbarSlots slot, @Nullable ItemStack item) {
        final int index = profile.getHotbarLoadout().getInventorySlotBySlot(slot);

        if (index < 0 || index > 9) {
            return;
        }

        profile.getPlayer().getInventory().setItem(index, item == null ? ItemStacks.AIR : item);
    }

    public void setCooldownIfNotAlreadyOnCooldown(Material material, int cooldown) {
        if (hasCooldown(material)) {
            return;
        }

        setCooldown(material, cooldown);
    }

    public boolean getAllowFlight() {
        return getPlayer().getAllowFlight();
    }

    public void setAllowFlight(boolean b) {
        getPlayer().setAllowFlight(b);
    }

    @Nonnull
    public List<Block> getLastTwoTargetBlocks(int maxDistance) {
        return getPlayer().getLastTwoTargetBlocks(null, maxDistance);
    }

    @Nonnull
    public <T extends Projectile> T launchProjectile(@Nonnull Class<T> clazz, @Nullable Consumer<T> consumer) {
        final T projectile = getPlayer().launchProjectile(clazz);

        if (consumer != null) {
            consumer.accept(projectile);
        }

        return projectile;
    }

    @Nonnull
    public <T extends Projectile> T launchProjectile(@Nonnull Class<T> clazz) {
        return launchProjectile(clazz, null);
    }

    public void setGlowing(boolean b) {
        getPlayer().setGlowing(b);
    }

    public void eject() {
        getPlayer().eject();
    }

    @Nonnull
    public Team getOrCreateScoreboardTeam(String name) {
        final Scoreboard scoreboard = getScoreboard();
        Team team = scoreboard.getTeam(name);

        if (team == null) {
            team = scoreboard.registerNewTeam(name);
        }

        return team;

    }

    public boolean isSelfOrTeammate(LivingGameEntity victim) {
        return equals(victim) || isTeammate(victim);
    }

    /**
     * Scales the cooldown with player's cooldown modifier.
     *
     * @param cooldown - Cooldown.
     * @return scaled cooldown.
     */
    public int scaleCooldown(int cooldown) {
        final double cdModifier = attributes.get(AttributeType.COOLDOWN_MODIFIER);

        return (int) Math.max(cooldown * cdModifier, 0);
    }

    public void equipPlayer(@Nonnull Hero hero) {
        final PlayerInventory inventory = getInventory();
        final HotbarLoadout loadout = getProfile().getHotbarLoadout();
        final int weaponSlot = loadout.getInventorySlotBySlot(HotbarSlots.WEAPON);

        inventory.setHeldItemSlot(weaponSlot);
        setGameMode(GameMode.SURVIVAL);

        final PlayerSkin skin = hero.getSkin();
        final Equipment equipment = hero.getEquipment();

        // Apply equipment
        if (skin == null) {
            equipment.equip(this);
        }
        else if (Settings.USE_SKINS_INSTEAD_OF_ARMOR.isDisabled(getPlayer())) {
            equipment.equip(this);
        }

        hero.onStart(this);

        inventory.setItem(weaponSlot, hero.getWeapon().getItem());
        giveTalentItems();
    }

    public void giveTalentItem(@Nonnull HotbarSlots slot) {
        final Hero hero = getHero();
        final Talent talent = hero.getTalent(slot);

        if (talent == null) {
            return;
        }

        giveTalentItem(slot, talent);
    }

    public void giveTalentItem(@Nonnull HotbarSlots slot, @Nonnull Talent talent) {
        final PlayerInventory inventory = getInventory();
        final ItemStack talentItem = talent.getItem();

        if (!talent.isAutoAdd()) {
            return;
        }

        final HotbarLoadout loadout = profile.getHotbarLoadout();
        final int index = loadout.getInventorySlotBySlot(slot);

        inventory.setItem(index, talentItem);
        fixTalentItemAmount(index, talent);
    }

    public void giveTalentItems() {
        final HotbarLoadout loadout = profile.getHotbarLoadout();
        final Hero hero = getHero();

        loadout.forEachTalentSlot((slot, i) -> {
            final Talent talent = hero.getTalent(i + 1);

            if (talent == null) {
                return;
            }

            giveTalentItem(slot, talent);
        });
    }

    public void setItemAndSnap(@Nonnull HotbarSlots slot, @Nonnull ItemStack item) {
        setItem(slot, item);
        snapTo(slot);
    }

    public void snapTo(@Nonnull HotbarSlots slot) {
        final HotbarLoadout loadout = profile.getHotbarLoadout();

        getInventory().setHeldItemSlot(loadout.getInventorySlotBySlot(slot));
    }

    @Nullable
    public HotbarSlots getHeldSlot() {
        final HotbarLoadout loadout = profile.getHotbarLoadout();

        return loadout.bySlot(getInventory().getHeldItemSlot());
    }

    public boolean isHeldSlot(@Nullable HotbarSlots slot) {
        return getHeldSlot() == slot;
    }

    public boolean isSettingEnable(@Nonnull Settings settings) {
        return settings.isEnabled(getPlayer());
    }

    public boolean isSettingDisabled(@Nonnull Settings setting) {
        return setting.isDisabled(getPlayer());
    }

    public boolean hasBlocksAbove() {
        return !getBlocksAbove().isEmpty();
    }

    public boolean hasBlocksBelow() {
        return !getBlocksBelow().isEmpty();
    }

    @Nonnull
    public List<Block> getBlocksAbove() {
        return getBlocksRelative((location, world) -> location.getY() < world.getMaxHeight(), location -> location.add(0, 1, 0));
    }

    @Nonnull
    public List<Block> getBlocksBelow() {
        return getBlocksRelative((location, world) -> location.getY() > world.getMinHeight(), location -> location.subtract(0, 1, 0));
    }

    public void setOutline(@Nonnull Outline outline) {
        outline.set(getPlayer());
    }

    @Nonnull
    public Location getLocationAnchored() {
        final Location location = getLocation();
        return anchorLocation(location);
    }

    private List<Block> getBlocksRelative(BiFunction<Location, World, Boolean> fn, Consumer<Location> consumer) {
        final List<Block> blocks = Lists.newArrayList();
        final Location location = getEyeLocation();
        final World world = getWorld();

        while (fn.apply(location, world)) {
            final Block block = location.getBlock();

            if (!block.isEmpty()) {
                blocks.add(block);
            }

            consumer.accept(location);
        }

        return blocks;
    }

    private void fixTalentItemAmount(int slot, Talent talent) {
        if (!(talent instanceof ChargedTalent chargedTalent)) {
            return;
        }

        final PlayerInventory inventory = getInventory();
        final ItemStack item = inventory.getItem(slot);

        if (item == null) {
            return;
        }

        item.setAmount(chargedTalent.getMaxCharges());
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
        executeOnDeathIfTalentIsNotNull(hero.getThirdTalent());
        executeOnDeathIfTalentIsNotNull(hero.getFourthTalent());
        executeOnDeathIfTalentIsNotNull(hero.getFifthTalent());
    }

    private void executeOnDeathIfTalentIsNotNull(Talent talent) {
        if (talent != null) {
            talent.onDeath(this);
        }
    }

    @Nonnull
    public static Location anchorLocation(@Nonnull Location location) {
        final World world = location.getWorld();

        if (world == null) {
            return location;
        }

        while (true) {
            final Block block = location.getBlock();
            final Block blockBelow = block.getRelative(BlockFace.DOWN);

            if (block.getY() <= world.getMinHeight()) {
                break;
            }

            if (!blockBelow.isEmpty()) {
                if (isBlockSlab(blockBelow.getType())) {
                    location.subtract(0, 0.5, 0);
                }

                break;
            }

            location.subtract(0, 1, 0);
        }

        return location;
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

    @Nonnull
    public static Optional<GamePlayer> getPlayerOptional(Player player) {
        final GamePlayer gamePlayer = getExistingPlayer(player);
        return gamePlayer == null ? Optional.empty() : Optional.of(gamePlayer);
    }

    private static boolean isBlockSlab(Material material) {
        if (!material.isBlock()) {
            return false;
        }

        return material.name().endsWith("_SLAB");
    }

    public enum Ignore {
        GAME_MODE,
        COOLDOWNS
    }

}

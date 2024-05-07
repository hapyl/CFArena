package me.hapyl.fight.game.entity;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import me.hapyl.fight.database.Award;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.*;
import me.hapyl.fight.game.achievement.Achievements;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.challenge.ChallengeType;
import me.hapyl.fight.game.cosmetic.Cosmetics;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.game.cosmetic.skin.Skin;
import me.hapyl.fight.game.cosmetic.skin.Skins;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.effect.ActiveGameEffect;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.ping.PlayerPing;
import me.hapyl.fight.game.entity.shield.Shield;
import me.hapyl.fight.game.entity.task.PlayerTaskList;
import me.hapyl.fight.game.gamemode.CFGameMode;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.PlayerDataHandler;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.loadout.HotbarLoadout;
import me.hapyl.fight.game.loadout.HotbarSlots;
import me.hapyl.fight.game.playerskin.PlayerSkin;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.setting.Settings;
import me.hapyl.fight.game.stats.StatContainer;
import me.hapyl.fight.game.stats.StatType;
import me.hapyl.fight.game.talents.ChargedTalent;
import me.hapyl.fight.game.talents.InputTalent;
import me.hapyl.fight.game.talents.TalentQueue;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.player.IPlayerTask;
import me.hapyl.fight.game.task.player.PlayerGameTask;
import me.hapyl.fight.game.team.Entry;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.*;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.chat.messagebuilder.Format;
import me.hapyl.spigotutils.module.chat.messagebuilder.Keybind;
import me.hapyl.spigotutils.module.chat.messagebuilder.MessageBuilder;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.math.Numbers;
import me.hapyl.spigotutils.module.math.Tick;
import me.hapyl.spigotutils.module.reflect.Reflect;
import me.hapyl.spigotutils.module.reflect.glow.Glowing;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import net.minecraft.network.protocol.game.PacketPlayOutAnimation;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This class controls all in-game player data.
 * <p>
 * <b>A single instance should exist per game bases and cleared after the game ends.</b>
 */
public class GamePlayer extends LivingGameEntity implements Ticking, PlayerElement.Caller {

    public static final double HEALING_AT_KILL = 0.3d;
    public static final double ASSIST_DAMAGE_PERCENT = 0.5d;
    public static final long COMBAT_TAG_DURATION = 5000L;
    public static final String SHIELD_FORMAT = "&e&l%.0f &e🛡";

    private static final double MAX_HEARTS = 40.0d;

    private final StatContainer stats;
    private final TalentQueue talentQueue;
    private final PlayerTaskList taskList;
    private final TalentLock talentLock;
    private final PlayerPing playerPing;
    private final Map<MoveType, Long> lastMoved;
    public boolean blockDismount;
    public long usedUltimateAt;
    private int sneakTicks;
    private int deathWishTicks; // TODO (hapyl): 004, Mar 4: <<
    @Nonnull
    private PlayerProfile profile;
    private double energy;
    private double ultimateModifier;
    private long combatTag;
    private int killStreak;
    private InputTalent inputTalent;
    private Shield shield;
    private boolean deflecting;

    @SuppressWarnings("all")
    public GamePlayer(@Nonnull PlayerProfile profile) {
        super(profile.getPlayer());

        this.profile = profile;
        this.ultimateModifier = 1.0d;
        this.talentQueue = new TalentQueue(this);
        this.stats = new StatContainer(this);
        this.lastMoved = Maps.newHashMap();
        this.combatTag = 0L;
        this.attributes = new EntityAttributes(this, profile.getHeroHandle().getAttributes());
        this.taskList = new PlayerTaskList(this);
        this.shield = null;
        this.talentLock = new TalentLock(this, profile.getHeroHandle());
        this.playerPing = new PlayerPing(this);

        markLastMoved();
    }

    public int getTalentLock(@Nonnull HotbarSlots slot) {
        return talentLock.getLock(slot);
    }

    @Nonnull
    public TalentLock getTalentLock() {
        return talentLock;
    }

    public boolean isDeflecting() {
        return deflecting;
    }

    public void setDeflecting(boolean deflecting) {
        this.deflecting = deflecting;
    }

    public void callSkinIfHas(@Nonnull Consumer<Skin> consumer) {
        final Skins enumSkin = getSelectedSkin();

        if (enumSkin != null) {
            consumer.accept(enumSkin.getSkin());
        }
    }

    @Nullable
    public Skins getSelectedSkin() {
        return getSelectedSkin(getEnumHero());
    }

    @Nullable
    public Skins getSelectedSkin(@Nonnull Heroes hero) {
        final PlayerDatabase database = getDatabase();

        return database.skinEntry.getSelected(hero);
    }

    @Override
    public void tick() {
        if (isDeadOrRespawning()) {
            return;
        }

        super.tick();
        talentLock.tick();

        sneakTicks = isSneaking() ? sneakTicks + 1 : 0;
    }

    public int getSneakTicks() {
        return sneakTicks;
    }

    /**
     * Resets all the player data.
     * Called upon respawn or start of the game.
     */
    public void resetPlayer() {
        final Player player = getEntity();

        killStreak = 0;
        combatTag = 0;
        noCCTicks = 0;
        sneakTicks = 0;
        talentLock.reset();

        // Actually stop the effects before applying the data
        entityData.getGameEffects().values().forEach(ActiveGameEffect::forceStopIfNotInfinite);
        entityData.getDotMap().clear(); // if not needed, don't touch, else implement custom hash map

        // Reset attributes
        attributes.reset();

        markLastMoved();
        setHealth(getMaxHealth());

        player.setLastDamageCause(null); // FIXME (hapyl): 004, Mar 4: idk
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
        player.getActivePotionEffects().forEach(effect -> {
            if (effect.getDuration() == PotionEffect.INFINITE_DURATION) {
                return;
            }

            this.entity.removePotionEffect(effect.getType());
        });

        // Reset attributes
        applyAttributes();

        setOutline(Outline.CLEAR);

        getEntityData().getDamageTaken().clear();
        wasHit = false;
        inputTalent = null;
        shield = null;

        player.setGameMode(GameMode.SURVIVAL);

        // reset all cooldowns as well
        Materials.iterate(MaterialCategory.ITEM, item -> {
            if (player.hasCooldown(item)) {
                player.setCooldown(item, 0);
            }
        });
    }

    /**
     * @deprecated {@link #die}
     */
    @Override
    @Deprecated
    public void kill() {
        // don't kill players
    }

    /**
     * @deprecated {@link #die}
     */
    @Override
    @Deprecated
    public void remove() {
        // don't remove players
    }

    @Override
    public boolean shouldDie() {
        // players do not die, they're put in spectator
        return false;
    }

    @Override
    public void onStop(@Nonnull GameInstance instance) {
        final Heroes hero = getEnumHero();
        final StatContainer stats = getStats();
        final Player player = getEntity();

        Glowing.stopGlowing(player);
        //resetPlayer(); // maybe don't reset player here actually

        // Reset skin if was applied
        final PlayerSkin skin = hero.getHero().getSkin();

        if (Settings.USE_SKINS_INSTEAD_OF_ARMOR.isEnabled(player) && skin != null) {
            PlayerSkin.reset(player);
        }

        showPlayer();

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
        getDatabase().statisticEntry.fromPlayerStatistic(hero, stats);
        hero.getStats().fromPlayerStatistic(stats);

        // Reset pings
        playerPing.reset();

        // Update scoreboard
        GameTask.runLater(() -> {
            updateScoreboardTeams(true);
        }, 5);
    }

    @Override
    public void onDeath() {
        super.onDeath();

        final GameEntity lastDamager = entityData.getLastDamager();

        // Call skin
        callSkinIfHas(skin -> {
            skin.onDeath(this, lastDamager);
        });

        if (!(lastDamager instanceof GamePlayer lastPlayerDamager)) {
            return;
        }

        // Don't heal for self or teammate kills
        if (isSelfOrTeammate(lastPlayerDamager)) {
            return;
        }

        lastPlayerDamager.heal(lastPlayerDamager.getMaxHealth() * HEALING_AT_KILL);

        // Progress Bond
        ChallengeType.KILL_ENEMIES.progress(lastPlayerDamager);
    }

    @Override
    public void die(boolean force) {
        super.die(force);

        if (!isAlive()) {
            return;
        }

        final Player player = getEntity();
        boolean gameOver = false;

        state = EntityState.DEAD;
        player.setGameMode(GameMode.SPECTATOR);

        playSound(Sound.ENTITY_BLAZE_DEATH, 2.0f);
        playWorldSound(Sound.ENTITY_PLAYER_DEATH, 1.0f);
        sendTitle("&c&lʏᴏᴜ ᴅɪᴇᴅ", "", 5, 25, 10);

        // Award killer coins for kill
        final GameEntity lastDamager = entityData.getLastDamager();

        if (lastDamager instanceof LivingGameEntity gameKiller && !equals(gameKiller)) {
            // Don't add stats for teammates either, BUT display the kill cosmetic
            if (!isTeammate(gameKiller)) {
                final IGameInstance gameInstance = Manager.current().getCurrentGame();
                final GameTeam team = gameKiller.getTeam();

                // Add team kills
                if (team != null) {
                    team.data.kills++;
                }

                // Check for first blood AFTER adding a kill
                final boolean isFirstBlood = gameInstance.getTotalKills() == 1;

                if (gameKiller instanceof GamePlayer gamePlayerKiller) {
                    final StatContainer killerStats = gamePlayerKiller.getStats();

                    killerStats.addValue(StatType.KILLS, 1);
                    gamePlayerKiller.killStreak++;
                    Award.PLAYER_ELIMINATION.award(gamePlayerKiller);

                    // Progress bond
                    if (isFirstBlood) {
                        ChallengeType.FIRST_BLOOD.progress(gamePlayerKiller);
                    }
                }

                // Check for first blood
                if (isFirstBlood) {
                    Achievements.FIRST_BLOOD.complete(team);
                }
            }

            // Display cosmetics
            if (lastDamager instanceof GamePlayer gamePlayerDamager) {
                final Player damagerPlayer = gamePlayerDamager.getPlayer();
                final Cosmetics killCosmetic = Cosmetics.getSelected(damagerPlayer, Type.KILL);

                if (killCosmetic != null) {
                    killCosmetic.getCosmetic().onDisplay0(new Display(damagerPlayer, entity.getLocation()));
                }
            }
        }

        // Award assists
        Set<GamePlayer> assistingPlayers = Sets.newHashSet();

        entityData.getDamageTaken().forEach((damager, damage) -> {
            if (lastDamager.is(damager) || damager == entity) {
                return;
            }

            final GamePlayer damagerPlayer = CF.getPlayer(damager);

            if (damagerPlayer == null) {
                return;
            }

            final double percentDamageDealt = damage / getMaxHealth();

            if (percentDamageDealt < ASSIST_DAMAGE_PERCENT) {
                return;
            }

            assistingPlayers.add(damagerPlayer);
        });

        // Also award for buffs/debuffs


        // Award.PLAYER_ASSISTED.award(this);
        //                damagerStats.addValue(StatType.ASSISTS, 1);

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
            gameOver = gameInstance.checkWinCondition();

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
        taskList.cancelAll();

        if (!gameOver) {
            triggerOnDeath();
        }

        // KEEP LAST
        //resetPlayer(GamePlayer.Ignore.GAME_MODE); Don't reset player actually
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
     * @deprecated Not deprecated, just a heads-up that setting item should be done using {@link HotbarSlots}.
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
        for (MoveType moveType : MoveType.values()) {
            markLastMoved(moveType);
        }
    }

    public void markLastMoved(@Nonnull MoveType moveType) {
        final long currentTimeMillis = System.currentTimeMillis();

        lastMoved.put(moveType, currentTimeMillis);
    }

    public long getLastMoved(@Nonnull MoveType type) {
        return lastMoved.getOrDefault(type, System.currentTimeMillis());
    }

    @Override
    public boolean heal(double amount, @Nullable LivingGameEntity healer) {
        if (!super.heal(amount, healer)) {
            return false;
        }

        updateHealth();

        // Fx
        addPotionEffect(PotionEffectType.REGENERATION, 0, 25);
        return true;
    }

    @Nonnull
    @Override
    public GameTeam getTeam() {
        final GameTeam team = super.getTeam();

        if (team == null) {
            Debug.severe(this + " has no team somehow!");
            return GameTeam.WHITE;
        }

        return team;
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

    public float getUltimateChargePercent() {
        final int ultimateCost = getUltimateCost();

        return (float) (energy / ultimateCost);
    }

    public String getUltimateString(ChatColor readyColor) {
        final UltimateTalent ultimate = getUltimate();
        final String pointsString = "&b&l%.0f%%".formatted(getUltimateChargePercent() * 100);

        if (isUsingUltimate()) {
            final long durationLeft = getHero().getUltimateDurationLeft(this);

            return "&b&lIN USE &b(%s&b)".formatted(durationLeft < 0 ? "∞" : BukkitUtils.roundTick(Tick.fromMillis(durationLeft)) + "s");
        }

        if (ultimate.hasCd(this)) {
            return "&8%s &b(%ss)".formatted(pointsString, BukkitUtils.roundTick(ultimate.getCdTimeLeft(this)));
        }
        else if (isUltimateReady()) {
            return readyColor + "&lREADY";
        }

        return pointsString;
    }

    @Nonnull
    public StatContainer getStats() {
        return stats;
    }

    public void clearEffects() {
        getEntityData().clearEffects();
    }

    public void clearEffect(Effects type) {
        getEntityData().clearEffect(type);
    }

    public boolean isUltimateReady() {
        return this.energy >= getHero().getUltimate().getCost();
    }

    public void updateScoreboardTeams(boolean toLobby) {
        profile.getLocalTeamManager().updateAll(toLobby);
    }

    public void decreaseHealth(@Nonnull DamageInstance instance) {
        final Player player = getPlayer();
        final EnumDamageCause cause = instance.getCause();

        if (shield != null && shield.canShield(cause)) {
            final double damage = instance.getDamage();
            final double capacityAfterHit = shield.takeDamage0(damage);

            // Always display shield damage
            if (damage > 0) {
                shield.display(damage, player.getEyeLocation());
            }

            // Shield took damage
            if (capacityAfterHit > 0) {
                instance.setDamage(0);
            }
            // Shield broke
            else {
                instance.setDamage(-capacityAfterHit);

                shield.onBreak0();
                shield = null;
            }
        }

        super.decreaseHealth(instance);
        updateHealth();
    }

    // Update player visual health
    public void updateHealth() {
        final double maxHealth = getMaxHealth();
        final double maxHearts = Math.min(maxHealth, MAX_HEARTS);

        entity.setMaxHealth(maxHearts);
        entity.setHealth(Numbers.clamp(maxHearts / maxHealth * this.health, getMinHealth(), maxHealth));
    }

    @Nonnull
    @Override
    public String getHealthFormatted() {
        return getHealthFormatted(getPlayer());
    }

    @Nonnull
    public String getHealthFormatted(@Nonnull Player player) {
        if (shield != null) {
            if (Settings.SHOW_HEALTH_AND_SHIELD_SEPARATELY.isEnabled(player)) {
                return super.getHealthFormatted() + " " + SHIELD_FORMAT.formatted(shield.getCapacity());
            }
            else {
                return SHIELD_FORMAT.formatted(getHealth() + shield.getCapacity());
            }
        }

        return super.getHealthFormatted();
    }

    public void interrupt() {
        final Player player = getEntity();
        final PlayerInventory inventory = player.getInventory();
        inventory.setHeldItemSlot(inventory.firstEmpty());

        Reflect.sendPacket(player, new PacketPlayOutAnimation(Reflect.getMinecraftPlayer(player), 1));

        GameTask.runLater(this::snapToWeapon, 1);

        // Fx
        playSound(Sound.ENTITY_ELDER_GUARDIAN_CURSE, 2.0f);
        playSound(Sound.ENCHANT_THORNS_HIT, 0.0f);
    }

    public void interruptShield() {
        final PlayerInventory inventory = getInventory();
        final ItemStack offhandItem = inventory.getItem(org.bukkit.inventory.EquipmentSlot.OFF_HAND);

        if (offhandItem == null) {
            return;
        }

        inventory.setItem(org.bukkit.inventory.EquipmentSlot.OFF_HAND, null);
        schedule(() -> inventory.setItem(org.bukkit.inventory.EquipmentSlot.OFF_HAND, offhandItem), 3);
    }

    /**
     * Returns true if the player is using ultimate.
     *
     * @return true if the player is using ultimate; false otherwise.
     */
    public boolean isUsingUltimate() {
        return usedUltimateAt > 0;
    }

    /**
     * Sets if the player is using ultimate.
     *
     * @param usingUltimate - Is using ultimate.
     */
    public void setUsingUltimate(boolean usingUltimate) {
        if (usingUltimate) {
            usedUltimateAt = System.currentTimeMillis();
        }
        else {
            usedUltimateAt = 0L;
        }
    }

    /**
     * Sets that the player is using ultimate and unsets it after a given duration.
     * <br>
     * This is mostly used with manual ultimates, with dynamic duration.
     *
     * @param duration - Duration.
     */
    public void setUsingUltimate(int duration) {
        setUsingUltimate(true);

        new PlayerGameTask(this, EntityState.ALIVE) {
            @Override
            public void run() {
                setUsingUltimate(false);
            }
        }.runTaskLater(duration);
    }

    public void triggerOnDeath() {
        this.onDeath();

        final IGameInstance currentGame = Manager.current().getCurrentGame();
        final Hero hero = getHero();
        final Weapon weapon = hero.getWeapon();

        currentGame.getEnumMap().getMap().onDeath(this);
        hero.onDeath(this);
        usedUltimateAt = 0L;
        deflecting = false;

        if (hero instanceof PlayerDataHandler<?> handler) {
            handler.removePlayerData(this);
        }

        attributes.onDeath(this);
        executeTalentsOnDeath();

        weapon.onDeath(this);
        weapon.getAbilities().forEach(ability -> ability.stopCooldown(this));
    }

    public DeathMessage getRandomDeathMessage() {
        return getLastDamageCause().getDeathMessage();
    }

    public boolean isRespawning() {
        return state == EntityState.RESPAWNING;
    }

    @Nonnull
    public UltimateTalent getUltimate() {
        return getHero().getUltimate();
    }

    public void addEnergy(int points) {
        final Player player = getEntity();
        final int ultimateCost = this.getUltimateCost();

        // cannot give points if using ultimate or dead
        if (isUsingUltimate() || isDeadOrRespawning() || this.energy >= ultimateCost) {
            return;
        }

        final double energyScaled = points * getAttributes().get(AttributeType.ENERGY_RECHARGE);

        this.energy = Numbers.clamp(energy + energyScaled, 0, ultimateCost);

        // show once at broadcast
        if (this.energy >= ultimateCost) {
            final MessageBuilder builder = new MessageBuilder();

            builder.append("&b&l※ &bYour ultimate is ready! Press ");
            builder.append(Keybind.SWAP_HANDS).color(ChatColor.YELLOW).format(Format.BOLD);
            builder.append("&b to use it!");

            builder.send(player);

            sendTitle("&3※&b&l※&3※", "&a&lULTIMATE READY!", 5, 15, 5);
            playSound(Sound.BLOCK_CONDUIT_DEACTIVATE, 2.0f);
        }
    }

    /**
     * Sends a 'text block' message, each line will be sent a separate message.
     *
     * @param textBlock - Message.
     */
    public void sendTextBlockMessage(@Nonnull String textBlock) {
        final String[] strings = textBlock.split("\n");

        for (String string : strings) {
            string = string.replace("%", "%%");

            if (string.equalsIgnoreCase("")) { // paragraph
                sendMessage("");
                continue;
            }

            final int prefixIndex = string.lastIndexOf(";;");
            String prefix = "&7";

            if (prefixIndex > 0) {
                prefix = string.substring(0, prefixIndex);
                string = string.substring(prefixIndex + 2);
            }

            sendMessage(prefix + string);
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
        resetPlayer();

        final Player player = getEntity();
        final Hero hero = getHero();

        state = EntityState.ALIVE;
        energy = 0;
        setUsingUltimate(false);
        setHealth(this.getMaxHealth());

        player.getInventory().clear();
        equipPlayer(hero);

        hero.onRespawn(this);

        // Add spawn protection
        addEffect(Effects.RESPAWN_RESISTANCE, 60);

        // Respawn location
        final IGameInstance gameInstance = Manager.current().getCurrentGame();
        final Location location = gameInstance.getEnumMap().getMap().getLocation();

        BukkitUtils.mergePitchYaw(entity.getLocation(), location);
        sendTitle("&a&lʀᴇsᴘᴀᴡɴᴇᴅ!", "", 0, 20, 5);
        entity.teleport(location);

        addPotionEffect(PotionEffectType.BLINDNESS, 1, 20);
    }

    public int getUltimateCost() {
        return getHero().getUltimate().getCost();
    }

    public double getEnergy() {
        return energy;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    public boolean compare(GamePlayer gamePlayer) {
        return gamePlayer == this;
    }

    public boolean compare(Player player) {
        return this.getPlayer() == player;
    }

    @Nonnull
    public PlayerDatabase getDatabase() {
        return profile.getDatabase();
    }

    @Nonnull
    public PlayerProfile getProfile() {
        return profile;
    }

    @Nonnull
    public <T extends LivingEntity> LivingGameEntity spawnAlliedLivingEntity(@Nonnull Location location, @Nonnull Entities<T> type, @Nonnull Consumer<LivingGameEntity> consumer) {
        return spawnAlliedEntity(location, type, (ConsumerFunction<T, LivingGameEntity>) LivingGameEntity::new);
    }

    @Nonnull
    public <T extends LivingEntity, E extends LivingGameEntity> E spawnAlliedEntity(@Nonnull Location location, @Nonnull Entities<T> type, @Nonnull ConsumerFunction<T, E> consumer) {
        final GameTeam team = getTeam();
        final E entity = CF.createEntity(location, type, consumer);

        entity.addToTeam(team);

        // Glow the entity for all allies
        for (GamePlayer player : getTeam().getPlayers()) {
            entity.setGlowing(player, ChatColor.GREEN);
        }

        return entity;
    }

    public boolean isTeammate(@Nullable GamePlayer player) {
        return player != null && GameTeam.isTeammate(Entry.of(this), Entry.of(player));
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

    public boolean hasMovedInLast(@Nonnull MoveType type, long millis) {
        final long lastMoved = getLastMoved(type);

        return lastMoved != 0 && (System.currentTimeMillis() - lastMoved) < millis;
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

    public <T> void spawnParticle(Location location, Particle particle, int amount, double x, double y, double z, float speed, T data) {
        getPlayer().spawnParticle(particle, location, amount, x, y, z, speed, data);
    }

    public void hideEntity(@Nonnull Entity entity) {
        getPlayer().hideEntity(Main.getPlugin(), entity);
    }

    public void showEntity(@Nonnull Entity entity) {
        getPlayer().showEntity(Main.getPlugin(), entity);
    }

    public void showEntity(@Nonnull LivingGameEntity gameEntity) {
        showEntity(gameEntity.getEntity());
    }

    public void addTask(@Nonnull IPlayerTask task) {
        taskList.add(task);
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
            this.shield.onRemove0();
        }

        this.shield = shield;

        if (shield != null) {
            shield.onCreate0();
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
        final Player player = getPlayer();

        // Don't allow changing fly mode in spectator to avoid falling off the map
        if (player.getGameMode() == GameMode.SPECTATOR) {
            return;
        }

        player.setFlying(b);
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

    @Nonnull
    public EntityEquipment getEquipment() {
        return Objects.requireNonNull(getPlayer().getEquipment());
    }

    public boolean isSwimming() {
        return getPlayer().isSwimming();
    }

    @Nullable
    public Block getTargetBlockExact(int maxDistance) {
        return getPlayer().getTargetBlockExact(maxDistance);
    }

    /**
     * Hides this player from all in game player who:
     * <ul>
     *     <li>Is not self.
     *     <li>Is not a spectator.
     *     <li>Is not a teammate.
     * </ul>
     */
    public void hidePlayer() {
        CFUtils.hidePlayer(this);
    }

    /**
     * Shows this player from all in game player who:
     * <ul>
     *     <li>Is not self.
     * </ul>
     */
    public void showPlayer() {
        CFUtils.showPlayer(this);
    }

    @Nonnull
    public Scoreboard getScoreboard() {
        return getPlayer().getScoreboard();
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

    public void setItem(@Nonnull EquipmentSlots slot, @Nullable ItemStack item) {
        slot.setItem(getInventory(), item);
    }

    public void setItem(@Nonnull org.bukkit.inventory.EquipmentSlot slot, @Nullable ItemStack item) {
        getInventory().setItem(slot, item);
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
        if (skin == null || Settings.USE_SKINS_INSTEAD_OF_ARMOR.isDisabled(getPlayer())) {
            final Skins enumSkin = getSelectedSkin();

            if (enumSkin != null) {
                final Skin skinHandle = enumSkin.getSkin();

                // Don't select disabled skins
                if (skinHandle instanceof Disabled) {
                    getDatabase().skinEntry.setSelected(getEnumHero(), null);

                    sendMessage("");
                    sendMessage("&cYou have a disabled skin selected! We had to change it, sorry!");
                    sendMessage("");

                    equipment.equip(this);
                }
                else {
                    skinHandle.equip(this);
                }
            }
            else {
                equipment.equip(this);
            }
        }

        Manager.current().getCurrentMap().getMap().onStart(this);

        hero.onStart(this);
        hero.getWeapon().onStart(this);

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

    public boolean isSettingEnabled(@Nonnull Settings settings) {
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

    /**
     * Schedules a delayed {@link PlayerGameTask}.
     *
     * @param consumer - Consumer.
     * @param delay    - Delay in ticks.
     * @return the scheduled task.
     */
    @Nonnull
    public PlayerGameTask schedule(@Nonnull Consumer<GamePlayer> consumer, int delay) {
        return schedule(() -> consumer.accept(GamePlayer.this), delay);
    }

    @Nonnull
    public PlayerGameTask schedule(@Nonnull Runnable runnable, int delay) {
        final PlayerGameTask task = new PlayerGameTask(this) {
            @Override
            public void run() {
                runnable.run();
            }
        };

        task.runTaskLater(delay);
        return task;
    }

    @Nonnull
    public ItemStack getHeldItem() {
        return getInventory().getItemInMainHand();
    }

    @Nonnull
    public String getCooldownFormatted(@Nonnull Material material) {
        return CFUtils.decimalFormatTick(getCooldown(material));
    }

    @Nonnull
    public PlayerPing getPlayerPing() {
        return playerPing;
    }

    public int getPing() {
        return getPlayer().getPing();
    }

    @Nonnull
    public RaycastResult rayCast(double maxDistance, double entitySearchDistance) {
        return new Raycast(this)
                .setMaxDistance(maxDistance)
                .setEntitySearchRadius(entitySearchDistance)
                .cast();
    }

    @Nonnull
    public Raycast rayCast() {
        return new Raycast(this);
    }

    public boolean isInGameOrTrial() {
        return Manager.current().isInGameOrTrial(getPlayer());
    }

    public void sendBlockChange(@Nonnull Block block, @Nonnull Material material) {
        sendBlockChange(block.getLocation(), material);
    }

    public void sendBlockChange(@Nonnull Location location, @Nonnull Material material) {
        getPlayer().sendBlockChange(location, material.createBlockData());
    }

    /**
     * @deprecated raw
     */
    @Deprecated
    public int getHeldSlotRaw() {
        return getInventory().getHeldItemSlot();
    }

    @Override
    public void callOnStart() {

    }

    @Override
    public void callOnStop() {

    }

    @Override
    public void callOnDeath() {

    }

    @Override
    public void callOnPlayersRevealed() {
    }

    @Nonnull
    public String formatWinnerName() {
        final StatContainer stats = getStats();
        final GameTeam winnerTeam = getTeam();

        return Chat.bformat(
                "{Team} &7⁑ &6{Hero} &e&l{Name} &7⁑ &c&l{Health}  &b&l{Kills} &b🗡  &c&l{Deaths} &c☠",
                winnerTeam.getFirstLetterCaps(),
                getHero().getNameSmallCaps(),
                getName(),
                getHealthFormatted(),
                stats.getValue(StatType.KILLS),
                stats.getValue(StatType.DEATHS)
        );
    }

    public void setWorldBorder(@Nullable WorldBorder worldBorder) {
        getPlayer().setWorldBorder(worldBorder);
    }

    @Nonnull
    public Item throwItem(@Nonnull Material material, @Nonnull Vector vector) {
        final World world = getWorld();

        final Item item = world.dropItem(getEyeLocation(), new ItemStack(material));

        item.setPickupDelay(999998);
        item.setOwner(uuid);

        item.setVelocity(vector);

        // Fx
        playWorldSound(Sound.ENTITY_SNOWBALL_THROW, 0.75f);

        return item;
    }

    /**
     * Returns true if the player is presumably standing still.
     * <br>
     * This is checks if the player last {@link MoveType#KEYBOARD} was:
     * <pre>
     *     now() - lastMoved >= 100L
     * </pre>
     *
     * @return true if the player is presumably standing still.
     */
    public boolean isStandingStill() {
        final long lastMoved = getLastMoved(MoveType.KEYBOARD);
        final long timeSinceLastMoved = System.currentTimeMillis() - lastMoved;

        return timeSinceLastMoved >= 100L;
    }

    public boolean hasCooldown(@Nonnull Talent talent) {
        return hasCooldown(talent.getMaterial());
    }

    public void startCooldown(@Nonnull Talent talent) {
        talent.startCd(this);
    }

    @Nonnull
    public Vector getDirectionWithMovementError(double movementError) {
        final Location location = getEyeLocation();

        return location.getDirection();
    }

    public boolean isValidForCosmetics() {
        if (hasEffect(Effects.INVISIBILITY)) {
            return false;
        }

        if (isDeadOrRespawning()) {
            return false;
        }

        return true;
    }

    /**
     * Gets a value from the given skin, or default is skin is not selected.
     */
    @Nonnull
    public <S extends Skin, R> R getSkinValue(@Nonnull Class<S> clazz, @Nonnull Function<S, R> fn, @Nonnull R def) {
        final Skins enumSkin = getSelectedSkin();

        if (enumSkin == null) {
            return def;
        }

        final Skin skin = enumSkin.getSkin();

        if (!clazz.isInstance(skin)) {
            // Actually don't throw error because people are stupid, including me, just warn
            Debug.warn("Invalid skin cast! Expected '%s', got '%s'!".formatted(skin.getClass().getSimpleName(), clazz.getSimpleName()));
            //throw makeSkinCastError(enumSkin, clazz);
            return def;
        }

        final R value = fn.apply(clazz.cast(skin));

        // Default if the return value was null
        // because that means the skin does not
        // change the appearance of this item.
        if (value == null) {
            return def;
        }

        return value;
    }

    /**
     * Runs a code for the given skin.
     * <br>
     * This method is meant to be used like so:
     * <pre>
     *     if (!player.runSkin(Skin.class, skin -> skin.helloWorld())) {
     *         defaultHelloWorld();
     *     }
     * </pre>
     *
     * @return true if the skin code was executed; false otherwise.
     */
    public <S extends Skin> boolean runSkin(@Nonnull Class<S> clazz, @Nonnull Function<S, Boolean> consumer) {
        final Skins enumSkin = getSelectedSkin();

        if (enumSkin == null) {
            return false;
        }

        final Skin skin = enumSkin.getSkin();

        if (!clazz.isInstance(skin)) {
            Debug.warn("Invalid skin cast! Expected '%s', got '%s'!".formatted(skin.getClass().getSimpleName(), clazz.getSimpleName()));
            return false;
            //throw makeSkinCastError(enumSkin, clazz);
        }

        // Return the method value, the method can
        // return 'false' to indicate "Use default value."
        return consumer.apply(clazz.cast(skin));
    }

    private IllegalArgumentException makeSkinCastError(Skins skin, Class<?> clazz) {
        return new IllegalArgumentException("Skin '%s' must extend '%'!".formatted(skin.name(), clazz.getSimpleName()));
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
        return CFUtils.anchorLocation(location);
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

}

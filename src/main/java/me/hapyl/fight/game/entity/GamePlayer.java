package me.hapyl.fight.game.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.player.PlayerSkin;
import me.hapyl.eterna.module.reflect.Reflect;
import me.hapyl.eterna.module.reflect.glowing.Glowing;
import me.hapyl.eterna.module.reflect.glowing.GlowingColor;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.eterna.module.util.Opt;
import me.hapyl.eterna.module.util.Promise;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import me.hapyl.fight.Message;
import me.hapyl.fight.database.Award;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.event.custom.GamePlayerResetEvent;
import me.hapyl.fight.game.*;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.BaseAttributes;
import me.hapyl.fight.game.challenge.ChallengeType;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.damage.DeathMessage;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.element.ElementCaller;
import me.hapyl.fight.game.entity.ping.PlayerPing;
import me.hapyl.fight.game.entity.task.PlayerTaskList;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.PlayerData;
import me.hapyl.fight.game.heroes.PlayerDataHandler;
import me.hapyl.fight.game.heroes.equipment.HeroEquipment;
import me.hapyl.fight.game.heroes.ultimate.EnumResource;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.loadout.HotBarLoadout;
import me.hapyl.fight.game.loadout.HotBarSlot;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.setting.EnumSetting;
import me.hapyl.fight.game.skin.Skin;
import me.hapyl.fight.game.skin.Skins;
import me.hapyl.fight.game.stats.StatContainer;
import me.hapyl.fight.game.stats.StatType;
import me.hapyl.fight.game.talents.ChargedTalent;
import me.hapyl.fight.game.talents.InputTalent;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentQueue;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.player.IPlayerTask;
import me.hapyl.fight.game.task.player.PlayerGameTask;
import me.hapyl.fight.game.team.Entry;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.game.type.GameType;
import me.hapyl.fight.registry.Registries;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.ItemStacks;
import me.hapyl.fight.vehicle.Vehicle;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutAnimation;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
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
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This class controls all in-game player data.
 * <p>
 * <b>A single instance should exist per game bases and cleared after the game ends.</b>
 */
public class GamePlayer extends LivingGameEntity implements Ticking {
    
    public static final double HEALING_AT_KILL = 0.3d;
    public static final double ASSIST_DAMAGE_PERCENT = 0.5d;
    public static final long COMBAT_TAG_DURATION = 5000L;
    
    private static final double MAX_HEARTS = 40.0d;
    private static final ItemStack ARROW_ITEM = new ItemBuilder(Material.ARROW)
            .setName("Invisible Arrow")
            .addTextBlockLore("""
                              &8Special Item
                              
                              &7&o;;Due to the limitations of the greatest game in the world — Minecraft — it's impossible to shoot your bow without an arrow.
                              
                              &7&o;;I did my best to hide it in your inventory by making it invisible, but it seems like you found it, bravo!
                              """)
            .modifyMeta(meta -> meta.setItemModel(Material.AIR.getKey()))
            .asIcon();
    
    public final GamePlayerCooldownManager cooldownManager;
    
    private final StatContainer stats;
    private final TalentQueue talentQueue;
    private final PlayerTaskList taskList;
    private final TalentLock talentLock;
    private final PlayerPing playerPing;
    private final Map<MoveType, Long> lastMoved;
    private final UIComponentCache uiComponentCache;
    
    public boolean blockDismount;
    public long usedUltimateAt;
    private int deathWishTicks; // TODO (hapyl): 004, Mar 4: <<
    @Nonnull
    private PlayerProfile profile;
    private double energy;
    private long combatTag;
    private int killStreak;
    private InputTalent inputTalent;
    private HeartStyle heartStyle;
    
    public GamePlayer(@Nonnull PlayerProfile profile) {
        super(profile.getPlayer(), profile.getHero().getAttributes());
        
        this.profile = profile;
        this.talentQueue = new TalentQueue(this);
        this.stats = new StatContainer(this);
        this.lastMoved = Maps.newHashMap();
        this.combatTag = 0L;
        this.taskList = new PlayerTaskList(this);
        this.talentLock = new TalentLock(this, profile.getHero());
        this.playerPing = new PlayerPing(this);
        this.uiComponentCache = new UIComponentCache();
        this.cooldownManager = new GamePlayerCooldownManager(this);
        this.heartStyle = null;
        
        markLastMoved();
    }
    
    @Nullable
    public HeartStyle heartStyle() {
        return heartStyle;
    }
    
    public void heartStyle(@Nullable HeartStyle heartStyle) {
        this.heartStyle = heartStyle;
    }
    
    public int getTalentLock(@Nonnull HotBarSlot slot) {
        return talentLock.getLock(slot);
    }
    
    @Nonnull
    public TalentLock getTalentLock() {
        return talentLock;
    }
    
    public void callSkinIfHas(@Nonnull Consumer<Skin> consumer) {
        final Skins enumSkin = getSelectedSkin();
        
        if (enumSkin != null) {
            consumer.accept(enumSkin.getSkin());
        }
    }
    
    @Nullable
    public Skins getSelectedSkin() {
        return getSelectedSkin(profile.getHero());
    }
    
    @Nullable
    public Skins getSelectedSkin(@Nonnull Hero hero) {
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
        
        // Tick heart type
        if (heartStyle != null) {
            if (heartStyle.tick-- <= 0) {
                heartStyle = null;
            }
            else {
                heartStyle.apply(this);
            }
        }
    }
    
    public int getSneakTicks() {
        return ticker.sneakTicks.getTick();
    }
    
    /**
     * Resets all the player data.
     * Called upon respawn or start of the game.
     */
    public void resetPlayer() {
        final Player player = getEntity();
        
        killStreak = 0;
        combatTag = 0;
        talentLock.reset();
        
        ticker.sneakTicks.zero();
        ticker.noCCTicks.zero();
        
        // Actually stop the effects before applying the data
        effects.values().removeIf(effect -> {
            if (effect.isInfiniteDuration()) {
                return false;
            }
            
            effect.remove();
            return true;
        });
        
        // Clear DoTs
        dotInstanceMap.clear();
        
        // Reset attribute to hero defaults
        attributes.merge(getHero().getAttributes());
        
        markLastMoved();
        setHealth(getMaxHealth());
        
        // player.setLastDamageCause(null); // FIXME (hapyl): 004, Mar 4: idk
        player.getInventory().clear();
        player.setMaxHealth(40.0d); // why deprecate
        player.setHealth(40.0d);
        player.setAbsorptionAmount(0.0d);
        player.setFireTicks(0); // This doesn't seem to work
        player.setVisualFire(false);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setSaturation(0.0f);
        player.setFoodLevel(20);
        player.setInvulnerable(false);
        player.setArrowsInBody(0);
        player.setGlowing(false);
        player.getActivePotionEffects().forEach(effect -> {
            if (effect.getDuration() == PotionEffect.INFINITE_DURATION) {
                return;
            }
            
            this.entity.removePotionEffect(effect.getType());
        });
        
        // Reset attributes
        applyAttributes();
        
        setOutline(Outline.CLEAR);
        
        damageTaken.clear();
        inputTalent = null;
        
        player.setGameMode(GameMode.SURVIVAL);
        
        // reset all cooldowns as well
        PlayerLib.stopCooldowns(player);
        
        // Call event
        new GamePlayerResetEvent(this).callEvent();
    }
    
    /**
     * @deprecated {@link #die}
     */
    @Override
    @Deprecated
    public void remove() {
        // Don't remove player BUT do call onRemove()
        onRemove();
    }
    
    @Override
    public void onStop(@Nonnull GameInstance instance) {
        final Hero hero = getHero();
        final StatContainer stats = getStats();
        final Player player = getEntity();
        
        Glowing.stopGlowing(player);
        resetPlayer();
        
        // Reset skin if was applied
        final PlayerSkin skin = hero.getSkin();
        
        if (EnumSetting.USE_SKINS_INSTEAD_OF_ARMOR.isEnabled(player) && skin != null) {
            profile.resetSkin();
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
        getDatabase().statisticEntry.setFromPlayerStatistic(hero, stats);
        hero.getStats().fromPlayerStatistic(stats);
        
        // Reset pings
        playerPing.reset();
        
        // Update scoreboard
        GameTask.runLater(() -> updateScoreboardTeams(true), 5);
    }
    
    @Override
    public void onRemove() {
        super.onRemove();
        
        final GameEntity lastDamager = lastDamager();
        
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
        
        lastPlayerDamager.healRelativeToMaxHealth(HEALING_AT_KILL);
        lastPlayerDamager.incrementEnergy(lastPlayerDamager.getUltimate().resource().playerElimination());
        
        // Progress Bond
        ChallengeType.KILL_ENEMIES.progress(lastPlayerDamager);
    }
    
    @Override
    public void die(boolean force) {
        if (isDeadOrRespawning()) {
            return;
        }
        
        super.die(force);
        
        final Player player = getEntity();
        boolean gameOver = false;
        
        state = EntityState.DEAD;
        player.setGameMode(GameMode.SPECTATOR);
        
        playSound(Sound.ENTITY_BLAZE_DEATH, 2.0f);
        playWorldSound(Sound.ENTITY_PLAYER_DEATH, 1.0f);
        sendTitle("&c&lʏᴏᴜ ᴅɪᴇᴅ", "", 5, 25, 10);
        
        // Award killer coins for kill
        final GameEntity lastDamager = lastDamager();
        
        if (lastDamager instanceof LivingGameEntity gameKiller && !equals(gameKiller)) {
            // Don't add stats for teammates either, BUT display the kill cosmetic
            if (!isTeammate(gameKiller)) {
                final IGameInstance gameInstance = Manager.current().currentInstance();
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
                    
                    // Kill confirmation
                    if (gamePlayerKiller.isSettingEnabled(EnumSetting.KILL_CONFIRMATION)) {
                        gamePlayerKiller.notifyKillConfirmation(KillConfirmation.KILL, this);
                    }
                }
                
                // Check for first blood
                if (isFirstBlood && team != null) {
                    Registries.achievements().FIRST_BLOOD.complete(team);
                }
            }
            
            // Display cosmetics
            if (lastDamager instanceof GamePlayer gamePlayerDamager) {
                final Cosmetic killCosmetic = gamePlayerDamager.getDatabase().cosmeticEntry.getSelected(me.hapyl.fight.game.cosmetic.Type.KILL);
                
                if (killCosmetic != null) {
                    killCosmetic.onDisplay0(new Display(gamePlayerDamager.getEntity(), entity.getLocation()));
                }
            }
        }
        
        // Award assists
        final Set<GamePlayer> assistingPlayers = getAssistingPlayers();
        
        damageTaken.forEach((damager, damage) -> {
            if (damager.equals(lastDamager) || damager.equals(this)) {
                return;
            }
            
            final double percentDamageDealt = damage / getMaxHealth();
            
            if (percentDamageDealt < ASSIST_DAMAGE_PERCENT) {
                return;
            }
            
            assistingPlayers.add(damager);
        });
        
        // If the damager is a pet, add its owner to assists
        if (lastDamager instanceof Pet pet) {
            assistingPlayers.add(pet.owner());
        }
        
        // Actually award assisting players, not this player you dumbo
        assistingPlayers.forEach(assist -> {
            Award.PLAYER_ASSISTED.award(assist);
            assist.stats.addValue(StatType.ASSISTS, 1);
            
            assist.notifyKillConfirmation(KillConfirmation.ASSIST, this);
        });
        
        stats.addValue(StatType.DEATHS, 1);
        
        final String deathMessage = getRandomDeathMessage().format(
                this,
                lastDamager,
                lastDamager == null ? 0 : lastDamager.getLocation().distance(entity.getLocation())
        );
        
        // Send death info to manager
        final GameInstance gameInstance = Manager.current().currentInstanceOrNull(); /*ignore deprecation*/
        if (gameInstance != null) {
            final GameType mode = gameInstance.getMode();
            
            mode.onDeath(gameInstance, this);
            gameOver = gameInstance.checkWinCondition();
            
            // Handle respawn
            if (mode.isAllowRespawn() && mode.shouldRespawn(this)) {
                respawnIn(mode.getRespawnTime() + 1);
            }
        }
        
        // Display death cosmetics
        final Cosmetic deathCosmetic = getDatabase().cosmeticEntry.getSelected(me.hapyl.fight.game.cosmetic.Type.DEATH);
        
        if (deathCosmetic != null) {
            deathCosmetic.onDisplay0(new Display(player, player.getLocation()));
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
     * @see #setItem(HotBarSlot, ItemStack)
     * @see #snapTo(HotBarSlot)
     */
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
    
    @Nonnull
    @Override
    public HealingOutcome heal(double amount, @Nullable LivingGameEntity healer) {
        final HealingOutcome superOutcome = super.heal(amount, healer);
        
        if (!superOutcome.hasHealed()) {
            return superOutcome;
        }
        
        updateHealth();
        
        // Regen is just for the Fx to make the heart animate
        addPotionEffect(PotionEffectType.REGENERATION, 0, 25);
        return superOutcome;
    }
    
    @Nonnull
    @Override
    public GameTeam getTeam() {
        final GameTeam team = super.getTeam();
        
        if (team != null) {
            return team;
        }
        
        Debug.severe(this + " has no team somehow!");
        return GameTeam.WHITE;
    }
    
    public String getUltimateString() {
        return getUltimateString(UltimateTalent.DisplayColor.PRIMARY);
    }
    
    public String getUltimateString(@Nonnull UltimateTalent.DisplayColor displayColor) {
        return getUltimate().toString(this, displayColor, energy);
    }
    
    @Nonnull
    public StatContainer getStats() {
        return stats;
    }
    
    public boolean isUltimateReady() {
        return getUltimate().canUseUltimate(this);
    }
    
    public void updateScoreboardTeams(boolean toLobby) {
        profile.getLocalTeamManager().updateAll(toLobby);
    }
    
    public void decreaseHealth(@Nonnull DamageInstance instance) {
        super.decreaseHealth(instance);
        updateHealth();
    }
    
    // Update player visual health
    public void updateHealth() {
        final double maxHealth = getMaxHealth();
        final double maxHearts = Math.min(maxHealth, MAX_HEARTS);
        
        entity.setMaxHealth(maxHearts);
        entity.setHealth(Math.clamp(maxHearts / maxHealth * this.health, getMinHealth(), maxHealth));
    }
    
    @Nonnull
    @Override
    public String getHealthFormatted() {
        return getHealthFormatted(getEntity());
    }
    
    @Nonnull
    public String getHealthFormatted(@Nonnull Player player) {
        if (shield != null) {
            if (EnumSetting.SHOW_HEALTH_AND_SHIELD_SEPARATELY.isEnabled(player)) {
                return super.getHealthFormatted() + " " + shield.getCapacityFormatted();
            }
            else {
                return Shield.SHIELD_FORMAT.formatted(getHealth() + shield.getCapacity());
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
        
        new PlayerGameTask(this, GamePlayer.class) {
            @Override
            public void run() {
                setUsingUltimate(false);
            }
        }.runTaskLater(duration);
    }
    
    public void triggerOnDeath() {
        this.onRemove();
        
        usedUltimateAt = 0L;
        
        ElementCaller.CALLER.onDeath(this);
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
    public Hero getHero() {
        return profile.getHero();
    }
    
    @Nonnull
    @Override
    public String getScoreboardName() {
        return getName();
    }
    
    @Nonnull
    @Override
    public final Player getEntity() {
        return (Player) super.getEntity();
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
        return getEntity().getWalkSpeed();
    }
    
    @Override
    public void setWalkSpeed(double value) {
        getEntity().setWalkSpeed((float) value);
    }
    
    public void respawnIn(int tick) {
        state = EntityState.RESPAWNING;
        
        new GameTask() {
            private int tickBeforeRespawn = tick;
            
            @Override
            public void run() {
                // stop respawning if the game has ended
                if (Manager.current().currentInstance().getGameState() != State.IN_GAME) {
                    cancel();
                    return;
                }
                
                if (tickBeforeRespawn < 0) {
                    respawn();
                    cancel();
                    return;
                }
                
                sendTitle("&e&lʀᴇsᴘᴀᴡɴɪɴɢ", "&b&l" + CFUtils.formatTick(tickBeforeRespawn), 0, 25, 0);
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
        prepare(hero);
        
        // Add spawn protection
        addEffect(EffectType.RESPAWN_RESISTANCE, 60);
        
        // Respawn location
        final IGameInstance gameInstance = Manager.current().currentInstance();
        final Location location = gameInstance.getEnumMap().getLevel().getLocation();
        
        BukkitUtils.mergePitchYaw(entity.getLocation(), location);
        sendTitle("&a&lʀᴇsᴘᴀᴡɴᴇᴅ!", "", 0, 20, 5);
        entity.teleport(location);
        
        addPotionEffect(PotionEffectType.BLINDNESS, 1, 20);
        
        ElementCaller.CALLER.onRespawn(this);
    }
    
    public double getEnergy() {
        return energy;
    }
    
    /**
     * @param energy - Energy to set.
     * @see #incrementEnergy(double)
     * @see #decrementEnergy(double, GamePlayer)
     * @deprecated Don't directly set energy unless you know what you're doing.
     */
    @Deprecated
    public void setEnergy(double energy) {
        this.energy = Math.clamp(energy, 0, getUltimate().cost());
    }
    
    /**
     * Increments the player's current energy by the amount.
     * <p>The implementation is based on the player's ultimate resource type and is scaled if needed.</p>
     *
     * @param energy - The amount of energy to increment.
     */
    public void incrementEnergy(double energy) {
        if (energy <= 0) {
            return;
        }
        
        final UltimateTalent ultimate = getUltimate();
        final EnumResource resource = ultimate.resource();
        
        // Multiply by the relative attribute
        final AttributeType effectiveAttribute = resource.effectiveAttribute();
        
        if (effectiveAttribute != null) {
            energy *= attributes.normalized(effectiveAttribute);
        }
        
        final double ultimateCost = ultimate.cost();
        
        // Don't increase energy is ultimate is active, the player is dead or already at max
        if (isUsingUltimate() || isDeadOrRespawning() || this.energy >= ultimateCost) {
            return;
        }
        
        final double previousEnergy = this.energy;
        this.energy = Math.clamp(this.energy + energy, 0, ultimateCost);
        
        // Call the ultimate even because I couldn't find a better check
        ultimate.atIncrement(this, previousEnergy, this.energy);
    }
    
    /**
     * Decrements the given amount of energy from the player.
     *
     * @param energyToRemove - Amount of energy to remove.
     * @param remover        - Player who removed the energy.
     *                       If present, the player will be added to assisting players.
     */
    public void decrementEnergy(double energyToRemove, @Nullable GamePlayer remover) {
        this.energy = Math.max(0, this.energy - energyToRemove);
        
        if (remover != null) {
            addAssistingPlayer(remover);
        }
    }
    
    public boolean compare(GamePlayer gamePlayer) {
        return gamePlayer == this;
    }
    
    public boolean compare(Player player) {
        return this.getEntity() == player;
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
        return spawnAlliedEntity(
                location, type, t -> {
                    final LivingGameEntity entity = new LivingGameEntity(t);
                    consumer.accept(entity);
                    
                    return entity;
                }
        );
    }
    
    @Nonnull
    public <T extends LivingEntity, E extends LivingGameEntity> E spawnAlliedEntity(@Nonnull Location location, @Nonnull Entities<T> type, @Nonnull Function<T, E> consumer) {
        final GameTeam team = getTeam();
        final E entity = CF.createEntity(location, type, consumer);
        
        entity.addToTeam(team);
        
        // Glow the entity for all allies
        for (GamePlayer player : getTeam().getPlayers()) {
            entity.setGlowingFor(player, GlowingColor.GREEN);
        }
        
        return entity;
    }
    
    public boolean isTeammate(@Nullable GamePlayer player) {
        return player != null && GameTeam.isTeammate(Entry.of(this), Entry.of(player));
    }
    
    public void setHandle(@Nonnull PlayerProfile profile, @Nonnull Player player) {
        this.profile = profile;
        this.entity = player;
    }
    
    public boolean hasMovedInLast(@Nonnull MoveType type, long millis) {
        final long lastMoved = getLastMoved(type);
        
        return lastMoved != 0 && (System.currentTimeMillis() - lastMoved) < millis;
    }
    
    public boolean isBlocking() {
        return getEntity().isBlocking();
    }
    
    public boolean isSneaking() {
        return getEntity().isSneaking();
    }
    
    public void setSneaking(boolean b) {
        getEntity().setSneaking(b);
    }
    
    public <T> void spawnParticle(Location location, Particle particle, int amount, double x, double y, double z, float speed, T data) {
        getEntity().spawnParticle(particle, location, amount, x, y, z, speed, data);
    }
    
    public void hideEntity(@Nonnull Entity entity) {
        getEntity().hideEntity(Main.getPlugin(), entity);
    }
    
    public void showEntity(@Nonnull Entity entity) {
        getEntity().showEntity(Main.getPlugin(), entity);
    }
    
    public void showEntity(@Nonnull LivingGameEntity gameEntity) {
        showEntity(gameEntity.getEntity());
    }
    
    public void addTask(@Nonnull IPlayerTask task) {
        taskList.add(task);
    }
    
    @Override
    public String toString() {
        return getName(); // Stop following stupid spigot shit
    }
    
    @Nonnull
    public String formatTeamName(@Nonnull String prefix, @Nonnull String suffix) {
        final String firstLetterCaps = getTeam().getFirstLetterCaps();
        
        return prefix + firstLetterCaps + " &f" + getName() + suffix;
    }
    
    @Nonnull
    public String formatTeamNameScoreboardPosition(int position, @Nonnull String suffix) {
        return formatTeamName(" &f#&l%s ".formatted(position), suffix);
    }
    
    public void setFlying(boolean b) {
        final Player player = getEntity();
        
        // Don't allow changing fly mode in spectator to avoid falling off the map
        if (player.getGameMode() == GameMode.SPECTATOR) {
            return;
        }
        
        player.setFlying(b);
    }
    
    public float getFlySpeed() {
        return getEntity().getFlySpeed();
    }
    
    public void setFlySpeed(float v) {
        getEntity().setFlySpeed(v);
    }
    
    public boolean isOnGround() {
        return getEntity().isOnGround();
    }
    
    public void setGameMode(GameMode gameMode) {
        getEntity().setGameMode(gameMode);
    }
    
    @Nonnull
    public EntityEquipment getEquipment() {
        return getEntity().getEquipment();
    }
    
    public boolean isSwimming() {
        return getEntity().isSwimming();
    }
    
    @Nullable
    public Block getTargetBlockExact(int maxDistance) {
        return getEntity().getTargetBlockExact(maxDistance);
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
        return getEntity().getScoreboard();
    }
    
    @Deprecated
    public void stopCooldown(@Nonnull Material material) {
        getEntity().setCooldown(material, 0);
    }
    
    public boolean isAbleToUseAbilities() {
        return Manager.current().isGameInProgress();
    }
    
    public void cancelInputTalent() {
        if (inputTalent != null && !inputTalent.isOnCooldown(this)) {
            sendTitle("&c&lᴄᴀɴᴄᴇʟʟᴇᴅ", inputTalent.getName(), 0, 10, 5);
            playSound(Sound.ENTITY_HORSE_SADDLE, 0.75f);
            
            inputTalent.onCancel(this);
        }
        
        inputTalent = null;
    }
    
    public void snapToWeapon() {
        final int slot = profile.getHotbarLoadout().getInventorySlotBySlot(HotBarSlot.WEAPON);
        
        if (slot < 0 || slot > 9) {
            return;
        }
        
        getInventory().setHeldItemSlot(slot);
    }
    
    /**
     * Gets a copy of {@link ItemStack} at the given slot, or {@code null} if nothing on that slot.
     *
     * @param hotBarSlot - The slot.
     * @return a copy of {@link ItemStack} at the given slot, or {@code null} if nothing on that slot.
     */
    @Nullable
    public ItemStack getItem(@Nonnull HotBarSlot hotBarSlot) {
        final int slot = profile.getHotbarLoadout().getInventorySlotBySlot(hotBarSlot);
        
        return getInventory().getItem(slot);
    }
    
    public void setItem(@Nonnull HotBarSlot slot, @Nullable ItemStack item) {
        final int index = profile.getHotbarLoadout().getInventorySlotBySlot(slot);
        
        if (index < 0 || index > 9) {
            return;
        }
        
        profile.getPlayer().getInventory().setItem(index, item == null ? ItemStacks.AIR : item);
    }
    
    public void setItem(@Nonnull org.bukkit.inventory.EquipmentSlot slot, @Nullable ItemStack item) {
        getInventory().setItem(slot, item);
    }
    
    public boolean getAllowFlight() {
        return getEntity().getAllowFlight();
    }
    
    public void setAllowFlight(boolean b) {
        getEntity().setAllowFlight(b);
    }
    
    @Nonnull
    public List<Block> getLastTwoTargetBlocks(int maxDistance) {
        return getEntity().getLastTwoTargetBlocks(null, maxDistance);
    }
    
    public void setGlowing(boolean b) {
        getEntity().setGlowing(b);
    }
    
    public void eject() {
        getEntity().eject();
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
    
    public final void prepare(@Nonnull Hero hero) {
        final PlayerInventory inventory = getInventory();
        final HotBarLoadout loadout = getProfile().getHotbarLoadout();
        
        final int weaponSlot = loadout.getInventorySlotBySlot(HotBarSlot.WEAPON);
        
        inventory.setHeldItemSlot(weaponSlot);
        setGameMode(GameMode.SURVIVAL);
        
        final PlayerSkin skin = hero.getSkin();
        final HeroEquipment equipment = hero.getEquipment();
        
        // Apply equipment
        if (skin == null || EnumSetting.USE_SKINS_INSTEAD_OF_ARMOR.isDisabled(getEntity())) {
            final Skins enumSkin = getSelectedSkin();
            
            if (enumSkin != null) {
                final Skin skinHandle = enumSkin.getSkin();
                
                // Don't select disabled skins
                if (skinHandle instanceof Disabled disabled) {
                    getDatabase().skinEntry.setSelected(getHero(), null);
                    
                    sendMessage("");
                    Message.error(getEntity(), "Your skin was unequipped because it's currently disabled, sorry! " + disabled.disableReason());
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
        
        ElementCaller.CALLER.onStart(this);
        
        inventory.setItem(weaponSlot, hero.getWeapon().createItem());
        giveTalentItems();
    }
    
    public void giveTalentItem(@Nonnull HotBarSlot slot) {
        final Hero hero = getHero();
        final Talent talent = hero.getTalent(slot);
        
        if (talent == null) {
            return;
        }
        
        giveTalentItem(slot, talent);
    }
    
    public void giveTalentItem(@Nonnull HotBarSlot slot, @Nonnull Talent talent) {
        talent.giveItem(this, slot);
    }
    
    public void giveTalentItems() {
        final HotBarLoadout loadout = profile.getHotbarLoadout();
        final Hero hero = getHero();
        
        loadout.forEachTalentSlot((slot, i) -> {
            final Talent talent = hero.getTalent(i + 1);
            
            if (talent == null) {
                return;
            }
            
            giveTalentItem(slot, talent);
        });
    }
    
    /**
     * Gives all the {@link Talent} items from this player's hot bar.
     * <h1>This will not prevent player from using the talent!</h1>
     */
    public void hideTalentItems() {
        final HotBarLoadout loadout = profile.getHotbarLoadout();
        
        loadout.forEachTalentSlot((slot, i) -> {
            setItem(slot, null);
        });
    }
    
    public void setItemAndSnap(@Nonnull HotBarSlot slot, @Nonnull ItemStack item) {
        setItem(slot, item);
        snapTo(slot);
    }
    
    public void snapTo(@Nonnull HotBarSlot slot) {
        final HotBarLoadout loadout = profile.getHotbarLoadout();
        
        getInventory().setHeldItemSlot(loadout.getInventorySlotBySlot(slot));
    }
    
    @Nullable
    public HotBarSlot getHeldSlot() {
        final HotBarLoadout loadout = profile.getHotbarLoadout();
        
        return loadout.bySlot(getInventory().getHeldItemSlot());
    }
    
    public boolean isHeldSlot(@Nullable HotBarSlot slot) {
        return getHeldSlot() == slot;
    }
    
    public boolean isSettingEnabled(@Nonnull EnumSetting enumSetting) {
        return enumSetting.isEnabled(getEntity());
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
        outline.set(getEntity());
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
    public Promise async(@Nonnull Runnable runnable) {
        return Promise.promise(runnable);
    }
    
    @Nonnull
    public ItemStack getHeldItem() {
        return getInventory().getItemInMainHand();
    }
    
    @Nonnull
    public PlayerPing getPlayerPing() {
        return playerPing;
    }
    
    public int getPing() {
        return getEntity().getPing();
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
        return Manager.current().isInGameOrTrial(getEntity());
    }
    
    public void sendBlockChange(@Nonnull Block block, @Nonnull BlockData data) {
        getEntity().sendBlockChange(block.getLocation(), data);
    }
    
    public void sendBlockChange(@Nonnull Location location, @Nonnull Material material) {
        getEntity().sendBlockChange(location, material.createBlockData());
    }
    
    public void resetBlockChange(@Nonnull Block block) {
        getEntity().sendBlockChange(block.getLocation(), block.getBlockData());
    }
    
    /**
     * @deprecated raw
     */
    @Deprecated
    public int getHeldSlotRaw() {
        return getInventory().getHeldItemSlot();
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
        getEntity().setWorldBorder(worldBorder);
    }
    
    /**
     * Sends an exclamation as a title to the player, indicating that the player should be cautious.
     *
     * @param warningType - The warning type to display.
     * @param duration    - The duration of the warning.
     */
    public void sendWarning(@Nonnull WarningType warningType, int duration) {
        asPlayer(player -> Chat.sendTitle(player, warningType.toString(), "", 0, duration, 5));
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
    
    @Nonnull
    public Vector getDirectionWithMovementError(double movementError) {
        final Location location = getEyeLocation();
        
        return location.getDirection();
    }
    
    public boolean isValidForCosmetics() {
        if (hasEffect(EffectType.INVISIBLE)) {
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
    
    /**
     * Gets or computes the {@link PlayerData} for the given {@link Hero}.
     *
     * @param hero - Hero.
     * @param <D>  - Data type.
     * @return player data.
     */
    @Nonnull
    public <D extends PlayerData, H extends Hero & PlayerDataHandler<D>> D getPlayerData(@Nonnull H hero) {
        return hero.getPlayerData(this);
    }
    
    public void executeTalentsOnDeath() {
        final Hero hero = getHero();
        
        executeOnDeathIfTalentIsNotNull(hero.getFirstTalent());
        executeOnDeathIfTalentIsNotNull(hero.getSecondTalent());
        executeOnDeathIfTalentIsNotNull(hero.getThirdTalent());
        executeOnDeathIfTalentIsNotNull(hero.getFourthTalent());
        executeOnDeathIfTalentIsNotNull(hero.getFifthTalent());
    }
    
    public void ui(@Nonnull Class<?> origin, @Nonnull String string) {
        uiComponentCache.cache.put(origin, new UIComponentCache.UIComponent(string));
    }
    
    @Nonnull
    public UIComponentCache getUIComponentCache() {
        return uiComponentCache;
    }
    
    @ApiStatus.Internal
    public void sendPacket(@Nonnull Packet<?> packet) {
        Reflect.sendPacket(getEntity(), packet);
    }
    
    @Nonnull
    public <T extends Vehicle> T startRiding(@Nonnull Function<Player, T> fn) {
        return CF.getVehicleManager().startRiding(getEntity(), fn);
    }
    
    @Override
    public int getAttackCooldown() {
        return getHero().getWeapon().attackCooldown();
    }
    
    /**
     * @deprecated Cooldowns now support {@link Key}, {@link GamePlayerCooldownManager}
     */
    @Deprecated
    @ApiStatus.Internal
    public boolean hasCooldownInternal(@Nonnull Material material) {
        return getEntity().hasCooldown(material);
    }
    
    /**
     * @deprecated Cooldowns now support {@link Key}, {@link GamePlayerCooldownManager}
     */
    @Deprecated
    @ApiStatus.Internal
    public void setCooldownInternal(@Nonnull Material material, int cd) {
        getEntity().setCooldown(material, cd);
    }
    
    public void spectate(@Nullable Entity entity) {
        final Player player = getEntity();
        
        if (entity == null) {
            player.setGameMode(GameMode.ADVENTURE);
        }
        else {
            player.setGameMode(GameMode.SPECTATOR);
            player.setSpectatorTarget(entity);
        }
    }
    
    @Nonnull
    @SuppressWarnings("UnstableApiUsage")
    public Input input() {
        return getEntity().getCurrentInput();
    }
    
    public void chargeUltimate() {
        incrementEnergy(getUltimate().cost());
    }
    
    public void notifyKillConfirmation(@Nonnull KillConfirmation confirmation, @Nonnull GamePlayer player) {
        final GameTeam team = player.getTeam();
        
        sendSubtitle("%s %s".formatted(confirmation.prefix(), team.getColor() + player.getName()), 0, 10, 5);
        confirmation.onDisplay(this);
    }
    
    @Nonnull
    public BaseAttributes getEffectiveAttributes() {
        return attributes.snapshot();
    }
    
    public void setArrowItem() {
        getInventory().setItem(9, ARROW_ITEM);
    }
    
    @Nonnull
    public String getTabString(@Nonnull Player player) {
        if (!isAlive()) {
            return state.string;
        }
        
        return "&c&l%s  &b%s".formatted(
                getHealthFormatted(player),
                getUltimateString(UltimateTalent.DisplayColor.PRIMARY)
        );
    }
    
    public int getCommissionLevel() {
        return getDatabase().commissionEntry.level();
    }
    
    public void swingHand(boolean mainhand) {
        if (mainhand) {
            swingMainHand();
        }
        else {
            swingOffHand();
        }
    }
    
    public long getUltimateDurationTimeLeft() {
        final UltimateTalent ultimate = getUltimate();
        final int duration = ultimate.getDuration();
        final int castDuration = ultimate.getCastDuration();
        
        // Handle manual ultimates
        if (duration == Constants.INFINITE_DURATION) {
            return Constants.INFINITE_DURATION;
        }
        
        final long timeLeft = (duration + castDuration) * 50L;
        
        return timeLeft != 0 ? timeLeft - (System.currentTimeMillis() - usedUltimateAt) : 0L;
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
        
        // fixme >> item.setAmount(chargedTalent.getMaxCharges());
    }
    
    private void executeOnDeathIfTalentIsNotNull(Talent talent) {
        if (talent != null) {
            talent.onDeath(this);
        }
    }
    
    @Nonnull
    public static Location anchorLocation(@Nonnull Location location) {
        return BukkitUtils.anchorLocation(location);
    }
    
    /**
     * Returns a player from existing instance, no matter if they're alive or not.
     *
     * @param player - bukkit player.
     * @return GamePlayer instance if there is a GameInstance, otherwise null.
     */
    @Nullable
    public static GamePlayer getExistingPlayer(@Nonnull Player player) {
        return CF.getProfile(player).getGamePlayer();
    }
    
    @Nonnull
    public static Opt<GamePlayer> getOptionalPlayer(@Nonnull Player player) {
        return new Opt<>(getExistingPlayer(player)) {
            @Override
            public void ifPresent(@Nonnull Consumer<GamePlayer> action) {
                if (isPresent()) {
                    super.ifPresent(action);
                    return;
                }
                
                Message.error(player, "You must be in a game to use this!");
            }
        };
    }
    
}

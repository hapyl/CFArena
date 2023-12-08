package me.hapyl.fight.event;

import com.google.common.collect.Sets;
import me.hapyl.fight.CF;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.event.custom.GameDamageEvent;
import me.hapyl.fight.game.*;
import me.hapyl.fight.game.achievement.Achievements;
import me.hapyl.fight.game.attribute.CriticalResponse;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.entity.EntityData;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.entity.cooldown.Cooldown;
import me.hapyl.fight.game.entity.ping.PlayerPing;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.loadout.HotbarLoadout;
import me.hapyl.fight.game.loadout.HotbarSlots;
import me.hapyl.fight.game.maps.GameMaps;
import me.hapyl.fight.game.parkour.CFParkour;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.setting.Settings;
import me.hapyl.fight.game.stats.StatType;
import me.hapyl.fight.game.talents.ChargedTalent;
import me.hapyl.fight.game.talents.InputTalent;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.team.Entry;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.game.team.LocalTeamManager;
import me.hapyl.fight.game.tutorial.Tutorial;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.spigotutils.EternaPlugin;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.parkour.Data;
import me.hapyl.spigotutils.module.parkour.ParkourManager;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

/**
 * Handles all player related events.
 */
public class PlayerHandler implements Listener {

    public final double RANGE_SCALE = 8.933d;
    public final double DAMAGE_LIMIT = Integer.MAX_VALUE;
    public final double HEALING_AT_KILL = 0.3d;

    private final Set<PotionEffectType> disabledEffects;

    public PlayerHandler() {
        disabledEffects = Sets.newHashSet();
        disabledEffects.add(PotionEffectType.WEAKNESS);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handlePlayerJoin(PlayerJoinEvent ev) {
        final Player player = ev.getPlayer();
        final Manager manager = Manager.current();

        manager.handlePlayer(player);
        LocalTeamManager.updateAll(player);

        if (manager.isGameInProgress()) {
            final GameInstance gameInstance = (GameInstance) manager.getCurrentGame();

            gameInstance.getMode().onJoin(gameInstance, player);
            ev.setJoinMessage(null);
        }
        else {
            if (!player.hasPlayedBefore()) {
                new Tutorial(player);
            }

            // Only show the join message if the game is not in progress.
            // Game instance should modify and broadcast the join message.
            ev.setJoinMessage(Chat.format("&7[&a+&7] %s%s &ewants to fight!", player.isOp() ? "&c" : "", player.getName()));
        }
    }

    @EventHandler()
    public void handleDisabledEffect(EntityPotionEffectEvent ev) {
        final Entity entity = ev.getEntity();
        final PotionEffect newEffect = ev.getNewEffect();
        final EntityPotionEffectEvent.Action action = ev.getAction();

        if (newEffect == null || action != EntityPotionEffectEvent.Action.ADDED) {
            return;
        }

        final PotionEffectType type = newEffect.getType();

        if (!disabledEffects.contains(type)) {
            return;
        }

        Debug.warn("A disabled effect was applied to an entity, canceled!");
        Debug.warn(" Effect: " + type.getName());
        Debug.warn(" Entity: " + entity.getName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handlePlayerQuit(PlayerQuitEvent ev) {
        final Player player = ev.getPlayer();
        final Manager manager = Manager.current();

        if (manager.isGameInProgress()) {
            final IGameInstance game = manager.getCurrentGame();
            final GamePlayer gamePlayer = GamePlayer.getExistingPlayer(player);

            if (gamePlayer != null) {
                game.getMode().onLeave((GameInstance) game, player);
            }

            ev.setQuitMessage(null);
        }
        else {
            // Only show the quit message if the game is not in progress.
            // Game instance should modify and broadcast the quit message.
            ev.setQuitMessage(Chat.format("&7[&c-&7] %s%s &ehas fallen!", player.isOp() ? "&c" : "", player.getName()));
        }

        // Save database
        manager.getOrCreateProfile(player).getDatabase().save();

        // Delete database instance
        PlayerDatabase.removeDatabase(player.getUniqueId());

        // Delete profile
        manager.removeProfile(player);
    }

    // Prevent painting-breaking while the game is in progress
    @EventHandler()
    public void handlePaintingBreaking(HangingBreakEvent ev) {
        if (Manager.current().isGameInProgress()) {
            ev.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleItemDropEntity(EntityDropItemEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleItemDropPlayer(PlayerDropItemEvent ev) {
        ev.setCancelled(true);

        final GamePlayer player = CF.getPlayer(ev.getPlayer());

        if (player == null) {
            return;
        }

        final PlayerPing ping = player.getPlayerPing();
        if (ping.isOnCooldown()) {
            return;
        }

        ping.requestedPing();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleFoodLevel(FoodLevelChangeEvent ev) {
        ev.setCancelled(true);
        ev.setFoodLevel(20);
    }

    @EventHandler()
    public void handleBlockPlace(BlockPlaceEvent ev) {
        if (ev.getPlayer().getGameMode() != GameMode.CREATIVE) {
            ev.setCancelled(true);
            ev.setBuild(false);
        }
    }

    @EventHandler()
    public void handleBlockBreak(BlockBreakEvent ev) {
        if (ev.getPlayer().getGameMode() != GameMode.CREATIVE) {
            ev.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleEntityRegainHealthEvent(EntityRegainHealthEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleProjectileLand(ProjectileHitEvent ev) {
        final Projectile entity = ev.getEntity();
        if (!(entity instanceof Arrow arrow)) {
            return;
        }

        arrow.remove();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handlePlayerSwapEvent(PlayerSwapHandItemsEvent ev) {
        final GamePlayer gamePlayer = CF.getPlayer(ev.getPlayer());

        if (gamePlayer == null) {
            return;
        }

        ev.setCancelled(true);

        if (!gamePlayer.isAbleToUseAbilities()) {
            return;
        }

        // Ultimate is not ready
        if (!gamePlayer.isUltimateReady()) {
            gamePlayer.sendTitle("&4&l※", "&cYour ultimate isn't ready!", 5, 15, 5);
            gamePlayer.sendMessage("&4&l※ &cYour ultimate isn't ready!");
            return;
        }

        final Hero hero = gamePlayer.getHero();
        final UltimateTalent ultimate = hero.getUltimate();

        // Ultimate is on cooldown
        if (ultimate.hasCd(gamePlayer)) {
            if (gamePlayer.isSettingEnable(Settings.SHOW_COOLDOWN_MESSAGE)) {
                gamePlayer.sendMessage(
                        "&4&l※ &cYour ultimate is on cooldown for %s!",
                        CFUtils.decimalFormatTick(ultimate.getCdTimeLeft(gamePlayer))
                );
            }
            return;
        }

        // Predicate fails
        if (!hero.predicateUltimate(gamePlayer)) {
            gamePlayer.sendMessage(
                    "&4&l※ &cCannot use ultimate! %s",
                    hero.predicateMessage(gamePlayer)
            );
            return;
        }

        // Already using ultimate
        if (hero.isUsingUltimate(gamePlayer)) {
            gamePlayer.sendMessage("&4&l※ &cYou are already using ultimate!");
            return;
        }

        // Ultimate used
        hero.useUltimate0(gamePlayer);
        ultimate.startCd(gamePlayer);
        gamePlayer.setUltPoints(0);

        // Stats
        gamePlayer.getStats().addValue(StatType.ULTIMATE_USED, 1);

        if (hero.getUltimateDuration() > 0) {
            hero.setUsingUltimate(gamePlayer, true, hero.getUltimateDuration());
        }

        // Achievement
        Achievements.USE_ULTIMATES.complete(gamePlayer);

        for (final Player online : Bukkit.getOnlinePlayers()) {
            Chat.sendMessage(
                    online,
                    "&b&l※ &b%s used &l%s&7!".formatted((gamePlayer.is(online) ? "You" : gamePlayer.getName()), ultimate.getName())
            );
            PlayerLib.playSound(online, ultimate.getSound(), ultimate.getPitch());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleDamage0(EntityDamageEvent ev) {
        final Entity entity = ev.getEntity();
        final EntityDamageEvent.DamageCause cause = ev.getCause();

        Projectile finalProjectile = null;

        // Ignore non living entities and/or void damage
        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }

        if (ev.getCause() == EntityDamageEvent.DamageCause.VOID) {
            EntityData.die(livingEntity);
            return;
        }

        // Don't damage invulnerable entities
        if (livingEntity.isInvulnerable()) {
            ev.setDamage(0.0d);
            ev.setCancelled(true);
            return;
        }

        // This is what actually stores all the custom data
        // needed to handle custom damage/causes
        final LivingGameEntity gameEntity = CF.getEntity(livingEntity);

        if (!Manager.current().isGameInProgress()) {
            // Ignore lobby damage unless game entity
            // Don't cancel when falling on slime block for a slime glitch
            if (entity.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.SLIME_BLOCK) {
                ev.setDamage(0.0d);
                return;
            }

            processLobbyDamage(livingEntity, ev);
        }

        if (gameEntity == null) {
            return;
        }

        // Don't allow damaging non-alive entities!
        if (gameEntity.getState() != EntityState.ALIVE) {
            ev.setCancelled(true);
            return;
        }

        final double initialDamage = ev.getDamage();
        final DamageInstance instance = new DamageInstance(gameEntity, ev.getDamage());
        final EntityData data = gameEntity.getData();

        // REASSIGNMENT STATE
        // If an entity wasn't hit by using DamageHandler, we
        // need to store the data from this event into DamageData,
        // since it is now the 'real' data.

        // Reassign cause
        data.setLastDamageCauseIfNative(cause);
        instance.cause = data.getLastDamageCauseNonNull();

        // PRE-EVENTS TESTS, SUCH AS GAME EFFECT, ETC.
        // Test for fall damage resistance
        if (data.getLastDamageCauseNonNull() == EnumDamageCause.FALL && gameEntity.hasEffect(GameEffectType.FALL_DAMAGE_RESISTANCE)) {
            ev.setCancelled(true);
            gameEntity.removeEffect(GameEffectType.FALL_DAMAGE_RESISTANCE);
            return;
        }

        // Calculate base damage and find final damager
        if (ev instanceof EntityDamageByEntityEvent ev2) {
            final Entity damager = ev2.getDamager();

            // Ignore self-damage for the following
            if (damager != entity) {
                // Check for player damager
                if (damager instanceof Player player) {
                    // Remove vanilla critical hit
                    if (player.getFallDistance() > 0.0F
                            && !player.isOnGround()
                            && !player.hasPotionEffect(PotionEffectType.BLINDNESS)
                            && player.getVehicle() == null) {
                        instance.damage /= 1.5F;
                    }

                    data.setLastDamagerIfNative(CF.getPlayer(player));
                }
                // Check for projectile damage
                else if (damager instanceof Projectile projectile) {
                    // Scale it down according to a super cool formula for players
                    if (projectile.getShooter() instanceof Player player && projectile instanceof AbstractArrow arrow) {
                        final GamePlayer gamePlayer = CF.getPlayer(player);
                        if (gamePlayer != null) {
                            final double weaponDamage = gamePlayer.getHero().getWeapon().getDamage();
                            final double scaleFactor = 1 + (instance.damage / RANGE_SCALE);

                            instance.damage = weaponDamage * scaleFactor;

                            if (arrow.isCritical()) {
                                instance.damage *= 1.5d;
                            }
                        }
                    }

                    // Reassign damager to shooter
                    if (projectile.getShooter() instanceof LivingEntity living) {
                        data.setLastDamagerIfNative(CF.getEntity(living));
                    }

                    // Store projectile for further use
                    finalProjectile = projectile;
                }
                // Default to damager if they're living
                else if (damager instanceof LivingEntity living) {
                    data.setLastDamagerIfNative(CF.getEntity(living));
                }
            }

            final LivingGameEntity lastDamager = data.getLastDamagerAsLiving();
            instance.damager = lastDamager;

            if (lastDamager instanceof GamePlayer gamePlayer && gamePlayer.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                final boolean cancelDamage = gamePlayer.getHero().processInvisibilityDamage(gamePlayer, gameEntity, instance.damage);

                if (cancelDamage) {
                    ev.setDamage(0.0d);
                    ev.setCancelled(true);
                    return;
                }
            }

            final GameTeam entityTeam = gameEntity.getTeam();

            // Teammate check
            if (entityTeam != null && lastDamager != null && entityTeam.isEntry(Entry.of(lastDamager))) {
                lastDamager.sendMessage("&cCannot damage teammates!");
                ev.setCancelled(true);
                ev.setDamage(0.0d);
                return;
            }

            // Apply effects and modifiers for the damager
            if (lastDamager != null) {
                final PotionEffect effectStrength = lastDamager.getPotionEffect(PotionEffectType.INCREASE_DAMAGE);
                final PotionEffect effectWeakness = lastDamager.getPotionEffect(PotionEffectType.WEAKNESS);

                // Strength
                // The current formula is +50% damage per strength lvl
                if (effectStrength != null) {
                    final int amplifier = effectStrength.getAmplifier() + 1;

                    instance.damage -= (3.0d * amplifier); // Remove vanilla strength
                    instance.damage += (instance.damage * 0.5d * amplifier);
                }

                // Weakness
                // The current formula reduces damage by half
                if (effectWeakness != null) {
                    instance.damage -= (4.0d * effectWeakness.getAmplifier() + 1);
                    instance.damage /= 2.0d;
                    // FIXME (hapyl): 027, May 27, 2023: Player's cannot damage with weakness, convert to GameEffect or use something like UNLUCK
                }

                // Check for stun and nullify the damage
                if (lastDamager.hasEffect(GameEffectType.STUN)) {
                    instance.damage = 0.0d;
                }

                // Check for lockdown and cancel the event
                if (lastDamager.hasEffect(GameEffectType.LOCK_DOWN)) {
                    ev.setCancelled(true);
                    lastDamager.sendTitle("&c&lLOCKDOWN", "&cCannot deal damage.", 0, 20, 0);
                    return;
                }
            }

            // Apply modifiers for a victim
            final PotionEffect effectResistance = livingEntity.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE);

            // Reduce damage by 85%
            if (effectResistance != null) {
                instance.damage *= 0.15d;
            }

            // Player victim checks
            if (livingEntity instanceof Player player) {
                // Negate damage if blocking
                if (player.isBlocking()) {
                    instance.damage = 0.0;
                }
            }

            // Check for GameEffects
            // Remove stun once damaged
            if (gameEntity.hasEffect(GameEffectType.STUN)) {
                gameEntity.removeEffect(GameEffectType.STUN);
            }

            // Increase damage taken by 2 if vulnerable
            if (gameEntity.hasEffect(GameEffectType.VULNERABLE)) {
                instance.damage *= 2.0d;
            }
        }

        // CALCULATE DAMAGE USING ATTRIBUTES

        // Outgoing damage
        final LivingGameEntity lastDamager = data.getLastDamagerAsLiving();

        if (lastDamager != null) {
            final EntityAttributes attributes = lastDamager.getAttributes();
            final CriticalResponse criticalResponse = attributes.calculateOutgoingDamage(instance.damage, data.getLastDamageCause());

            instance.damage = criticalResponse.damage();
            instance.isCrit = criticalResponse.isCrit();
        }

        // Incoming damage
        final EntityAttributes attributes = gameEntity.getAttributes();
        instance.damage = attributes.calculateIncomingDamage(instance.damage);

        // Don't allow negative damage
        if (instance.damage < 0.0d) {
            instance.damage = 0.0d;
        }

        // Dodge
        if (instance.damage > 0 && attributes.calculateDodge()) {
            ev.setCancelled(true);
            gameEntity.playDodgeFx();
            return;
        }

        // Ferocity
        if (lastDamager != null
                && (instance.cause != null
                && instance.cause.isAllowedForFerocity())
                && !gameEntity.hasCooldown(Cooldown.FEROCITY)) {
            final EntityAttributes damagerAttributes = lastDamager.getAttributes();
            final int ferocityStrikes = damagerAttributes.getFerocityStrikes();

            if (ferocityStrikes > 0) {
                gameEntity.executeFerocity(instance.damage, lastDamager, ferocityStrikes);
            }
        }

        // PROCESS HERO EVENTS

        // As victim
        if (gameEntity instanceof GamePlayer player) {
            instance.fromOutput(player.getHero().processDamageAsVictim(instance.toInput()));
        }

        // As damager
        if (data.getLastDamager() instanceof GamePlayer player) {
            instance.fromOutput(player.getHero().processDamageAsDamager(instance.toInput()));
        }

        // As projectile
        if (data.getLastDamager() instanceof GamePlayer player && finalProjectile != null) { //  && Manager.current().isGameInProgress()
            instance.fromOutput(player.getHero().processDamageAsDamagerProjectile(instance.toInput(), finalProjectile));
        }

        // PROCESS GAME ENTITY DAMAGE
        instance.fromOutput(gameEntity.onDamageTaken0(instance.toInput()));

        if (lastDamager != null) {
            instance.fromOutput(lastDamager.onDamageDealt0(instance.toInput()));
        }

        // PROCESS MAP DAMAGE
        final GameMaps currentMap = Manager.current().getCurrentMap();

        instance.fromOutput(currentMap.getMap().onDamageTaken(instance.toInput()));
        instance.fromOutput(currentMap.getMap().onDamageDealt(instance.toInput()));

        if (instance.isCancel()) {
            ev.setCancelled(true);
            return;
        }

        // Don't damage anything, only visually
        ev.setDamage(0.0d);

        // Store data in DamageData
        data.setLastDamage(instance.damage);
        data.setCrit(instance.isCrit);

        // CALL DAMAGE EVENT
        final GameDamageEvent event = instance.toEvent();
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            ev.setCancelled(true);
            return;
        }

        // Process true damage
        if (instance.cause.isTrueDamage()) {
            instance.damage = initialDamage;
            data.setLastDamage(initialDamage);
        }

        // Keep damage in limit
        if (instance.damage > DAMAGE_LIMIT) {
            Debug.warn("%s dealt too much damage! (%.1f)", lastDamager == null ? "Server" : lastDamager.getName(), instance.damage);
            instance.damage = DAMAGE_LIMIT;
            data.setLastDamage(DAMAGE_LIMIT);
        }

        // Progress stats for damager
        if (lastDamager instanceof GamePlayer player) {
            player.getStats().addValue(StatType.DAMAGE_DEALT, instance.damage);

            if (Settings.SHOW_DAMAGE_IN_CHAT.isEnabled(player.getPlayer())) {
                data.notifyChatOutgoing(player);
            }
        }

        // Decrease entity's health
        gameEntity.decreaseHealth(instance);

        // Make sure not to kill players but instead
        // put them in spectator mode
        if (gameEntity instanceof GamePlayer player) {
            // Decrease health
            player.markCombatTag();

            // Progress stats for a victim
            player.getStats().addValue(StatType.DAMAGE_TAKEN, instance.damage);

            if (Settings.SHOW_DAMAGE_IN_CHAT.isEnabled(player.getPlayer())) {
                data.notifyChatIncoming(player);
            }

            // Fail-safe just to be sure player does
            // not die. If the player actually died in the game,
            // then the exception was thrown
            if (player.getHealth() <= 0.0d) {
                ev.setCancelled(true);
            }
        }
    }

    // A little wonky implementation, but it allows damaging endermen with arrows.
    @EventHandler()
    public void handleProjectileEndermenDamage(ProjectileHitEvent ev) {
        final Projectile projectile = ev.getEntity();
        final ProjectileSource shooter = projectile.getShooter();
        final Entity hitEntity = ev.getHitEntity();

        if (!(hitEntity instanceof Enderman enderman)
                || !(shooter instanceof Player player)
                || !(projectile instanceof AbstractArrow arrow)) {
            return;
        }

        final GamePlayer gamePlayer = CF.getPlayer(player);
        if (gamePlayer == null) {
            return;
        }

        double damage = gamePlayer.getHero().getWeapon().getDamage();
        if (arrow.isCritical()) {
            damage *= 1.5d;
        }

        final LivingGameEntity gameEntity = CF.getEntity(enderman);

        if (gameEntity != null) {
            ev.setCancelled(true);
            gameEntity.damage(damage, player, EnumDamageCause.PROJECTILE);
        }
    }

    // I... I don't know what this is...
    @EventHandler()
    public void handleArmorStandDeath(EntityDeathEvent ev) {
        if (ev.getEntity() instanceof ArmorStand) {
            ev.getDrops().clear();
            ev.setDroppedExp(0);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void handleInventoryClickEvent(InventoryClickEvent ev) {
        if (Manager.current().isGameInProgress()) {
            ev.setCancelled(true);
        }
    }

    /**
     * Handler for talent usage.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void handlePlayerClick(PlayerItemHeldEvent ev) {
        final Player player = ev.getPlayer();
        final PlayerProfile profile = PlayerProfile.getProfile(player);

        if (profile == null) {
            return;
        }

        final GamePlayer gamePlayer = profile.getGamePlayer();

        if (gamePlayer == null) {
            return;
        }

        // This means the game has not yet started, aka "pre-game"
        if (!Manager.current().isAbleToUse(player)) {
            gamePlayer.snapToWeapon();
            return;
        }

        gamePlayer.cancelInputTalent();

        final int newSlot = ev.getNewSlot();

        final HotbarLoadout hotbarLoadout = profile.getHotbarLoadout();
        final HotbarSlots hotbarSlot = hotbarLoadout.bySlot(newSlot);

        if (hotbarSlot == null) {
            return;
        }

        final boolean shouldCancel = hotbarSlot.get().handle(gamePlayer, newSlot);
        if (shouldCancel) {
            ev.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleInteraction(PlayerInteractEvent ev) {
        final Player player = ev.getPlayer();
        if (Manager.current().isGameInProgress() || player.getGameMode() != GameMode.CREATIVE) {
            final ItemStack item = ev.getItem();
            final Block clickedBlock = ev.getClickedBlock();

            if (ev.getAction() == Action.PHYSICAL) {
                return;
            }

            if (item != null) {
                // allow interacting with intractable items
                if (isIntractable(item)) {
                    //return;
                }
            }

            if (clickedBlock != null && clickedBlock.getType().isInteractable()) {
                final String blockName = clickedBlock.getType().name().toLowerCase(Locale.ROOT);

                if (blockName.contains("button") || blockName.contains("lever")) {
                    return;
                }

                // I think this should be used instead of cancel to not cancel bows, etc.
                ev.setUseInteractedBlock(Event.Result.DENY);
            }
        }
    }

    @EventHandler()
    public void handleMovement(PlayerMoveEvent ev) {
        final Player player = ev.getPlayer();
        final Location from = ev.getFrom();
        final Location to = ev.getTo();

        if (to == null) {
            return;
        }

        if (Manager.current().isGameInProgress()) {
            final GamePlayer gamePlayer = CF.getPlayer(player);
            if (gamePlayer == null) {
                return;
            }

            // AFK detection
            // Mark as moved even if the player can't move and only moved the mouse
            gamePlayer.markLastMoved();

            if (hasNotMoved(from, to)) {
                return;
            }

            // Handle no moving
            if (!gamePlayer.canMove()) {
                ev.setCancelled(true);
                return;
            }

            gamePlayer.getActiveEffects().values().forEach(effect -> effect.processEvent(ev));

            // Amnesia
            if (gamePlayer.hasEffect(GameEffectType.AMNESIA)) {
                final double pushSpeed = player.isSneaking() ? 0.05d : 0.1d;

                player.setVelocity(new Vector(
                        new Random().nextBoolean() ? pushSpeed : -pushSpeed,
                        -0.2723,
                        new Random().nextBoolean() ? pushSpeed : -pushSpeed
                ));
            }
        }
    }

    @EventHandler()
    public void handleSlotClick(InventoryClickEvent ev) {
        if (ev.getClick() == ClickType.DROP && ev.getWhoClicked() instanceof Player player && player.getGameMode() == GameMode.CREATIVE) {
            ev.setCancelled(true);
            Chat.sendMessage(player, "&aClicked %s slot.", String.valueOf(ev.getRawSlot()));
            PlayerLib.playSound(player, Sound.BLOCK_LEVER_CLICK, 2.0f);
        }
    }

    /**
     * Handles Input Talent for left and right clicks.
     */
    @EventHandler()
    public void handleInputTalent(PlayerInteractEvent ev) {
        final Action action = ev.getAction();
        final boolean isLeftClick = action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK;

        handleInputTalent(CF.getPlayer(ev.getPlayer()), isLeftClick);
    }

    /**
     * Handles Input Talent for left click. (Entity damage)
     */
    @EventHandler()
    public void handleEntityInteract(EntityDamageByEntityEvent ev) {
        final Entity damager = ev.getDamager();

        if (damager instanceof Player player) {
            handleInputTalent(CF.getPlayer(player), true);
        }
    }

    @EventHandler()
    public void handleResourcePack(PlayerResourcePackStatusEvent ev) {
        final Player player = ev.getPlayer();
        final PlayerResourcePackStatusEvent.Status status = ev.getStatus();
        final PlayerProfile profile = PlayerProfile.getProfile(player);

        if (profile == null) {
            return;
        }

        switch (status) {
            case ACCEPTED -> {
                Chat.sendMessage(player, "&aDownloading resource pack...");
            }
            case DECLINED -> {
                Chat.sendMessage(
                        player,
                        "&aNo worries! Though the resource pack will provide a better and more enjoyable experience, it is not required. Though if you change your mind, just use &e/resoucepack &acommand to prompt you again."
                );
                Chat.sendMessage(
                        player,
                        "&eYou might need to set &6'Server Resource Packs' &eto &6'Enabled' or &6'Prompt'&e in server info!"
                );
            }
            case FAILED_DOWNLOAD -> {
                Chat.sendMessage(
                        player,
                        "&4Failed to download resource pack! &cTry again using &e/resourcepack&c. If the issue continues, report this!"
                );
            }

            case SUCCESSFULLY_LOADED -> {
                Chat.sendMessage(player, "&aSuccessfully downloaded!");
                profile.setResourcePack();
            }
        }

    }

    private void processLobbyDamage(@Nonnull LivingEntity entity, @Nonnull EntityDamageEvent ev) {
        final EntityDamageEvent.DamageCause cause = ev.getCause();

        if (!(entity instanceof Player player)) {
            return;
        }

        ev.setCancelled(true);

        final ParkourManager parkourManager = EternaPlugin.getPlugin().getParkourManager();
        final Data data = parkourManager.getData(player);

        if (data == null) {
            return;
        }

        if (!(data.getParkour() instanceof CFParkour parkour)) {
            return;
        }

        parkour.onDamage(player, cause);
    }

    private void handleInputTalent(GamePlayer player, boolean isLeftClick) {
        if (player == null) {
            return;
        }

        final InputTalent talent = player.getInputTalent();

        if (talent == null || !checkTalent(player, talent)) {
            return;
        }

        final Response response = isLeftClick ? talent.onLeftClick(player) : talent.onRightClick(player);
        final String usage = talent.getUsage(isLeftClick);

        if (response.isError()) {
            player.setInputTalent(null);
            player.snapToWeapon();
        }

        if (!checkResponse(player, response)) {
            return;
        }

        // \/ Talent executed \/
        player.setInputTalent(null); // keep this above CD and slot changes!

        if (isLeftClick) {
            talent.startCdLeft(player);
        }
        else {
            talent.startCdRight(player);
        }

        talent.addPoint(player, isLeftClick);

        // Add 1 tick cooldown to a weapon to prevent accidental use
        final ItemStack item = player.getItem(HotbarSlots.WEAPON);

        if (item != null) {
            player.setCooldownIfNotAlreadyOnCooldown(item.getType(), 1);
        }

        player.snapToWeapon();
    }

    private boolean isIntractable(ItemStack stack) {
        final Material type = stack.getType();
        return switch (type) {
            case BOW, CROSSBOW, TRIDENT, FISHING_ROD -> true;
            default -> type.isInteractable();
        };
    }

    private boolean hasNotMoved(Location from, @Nullable Location to) {
        if (to == null) {
            return true;
        }
        return from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ();
    }

    /**
     * Perform talent checks and return if it's valid to be used.
     *
     * @param player - Player.
     * @param talent - Talent.
     * @return true if valid, false otherwise.
     */
    public static boolean checkTalent(@Nonnull GamePlayer player, @Nullable Talent talent) {
        // null check
        if (talent == null) {
            player.sendMessage("&4Talent is null! Report this.");
            return false;
        }

        // cooldown check
        if (talent.hasCd(player)) {
            if (player.isSettingEnable(Settings.SHOW_COOLDOWN_MESSAGE)) {
                player.sendMessage("&cTalent on cooldown for %s.", CFUtils.decimalFormatTick(talent.getCdTimeLeft(player)));
            }
            player.snapToWeapon();
            return false;
        }

        // charge check
        if (talent instanceof ChargedTalent chargedTalent) {
            if (chargedTalent.getChargesAvailable(player) <= 0) {
                player.sendMessage("&cOut of charges!");
                return false;
            }
        }

        return true;
    }

    /**
     * Perform response checks and return if it's valid to be used.
     *
     * @param player   - Player.
     * @param response - Response.
     * @return true if valid, false otherwise.
     */
    public static boolean checkResponse(GamePlayer player, Response response) {
        if (response.isError()) {
            response.sendError(player);
            return false;
        }

        // await stops the code here, basically OK but does not start cooldown nor remove charge if charged talent.
        // do not simplify so single line
        if (response.isAwait()) {
            return false;
        }

        return true;
    }


}

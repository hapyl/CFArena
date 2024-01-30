package me.hapyl.fight.event;

import com.google.common.collect.Maps;
import me.hapyl.fight.CF;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.event.custom.GameDamageEvent;
import me.hapyl.fight.event.custom.ProjectilePostLaunchEvent;
import me.hapyl.fight.game.*;
import me.hapyl.fight.game.achievement.Achievements;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.EntityData;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.entity.MoveType;
import me.hapyl.fight.game.entity.cooldown.Cooldown;
import me.hapyl.fight.game.entity.ping.PlayerPing;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.loadout.HotbarLoadout;
import me.hapyl.fight.game.loadout.HotbarSlots;
import me.hapyl.fight.game.parkour.CFParkour;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.setting.Settings;
import me.hapyl.fight.game.stats.StatType;
import me.hapyl.fight.game.talents.ChargedTalent;
import me.hapyl.fight.game.talents.InputTalent;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.game.task.GameTask;
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
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Handles all player related events.
 */
public class PlayerHandler implements Listener {

    public static final Map<PotionEffectType, AttributeType> disabledEffects = Maps.newHashMap();

    private static final double VELOCITY_MAX_Y = 4.821600093841552d;
    public final double RANGE_SCALE = 8.933d;
    public final double DAMAGE_LIMIT = Short.MAX_VALUE;
    private final Set<EntityDamageEvent.DamageCause> instantDeathCauses =
            Set.of(EntityDamageEvent.DamageCause.VOID);

    public PlayerHandler() {
        disabledEffects.put(PotionEffectType.WEAKNESS, AttributeType.DEFENSE);
        disabledEffects.put(PotionEffectType.INCREASE_DAMAGE, AttributeType.ATTACK);
        disabledEffects.put(PotionEffectType.DAMAGE_RESISTANCE, AttributeType.DEFENSE);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handlePlayerJoin(PlayerJoinEvent ev) {
        final Player player = ev.getPlayer();
        final Manager manager = Manager.current();

        manager.handlePlayer(player);

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

        LocalTeamManager.updateAll(player);
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
        final AttributeType attributeReplacement = disabledEffects.get(type);

        if (attributeReplacement == null) {
            return;
        }

        Chat.broadcastOp("&4&lADMIN&c A disabled effect is used, canceled!");
        Chat.broadcastOp("&4&lADMIN&c Do &nnot&c use &e&l%s&c!", Chat.capitalize(type.getKey().getKey()));
        Chat.broadcastOp("&4&lADMIN&c Replace it with &e&l%s&c attribute!", attributeReplacement.getName());

        ev.setCancelled(true);
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
        PlayerDatabase.uninstantiate(player.getUniqueId());

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
            if (gamePlayer.isSettingEnabled(Settings.SHOW_COOLDOWN_MESSAGE)) {
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

        // Check instant death
        if (instantDeathCauses.contains(cause)) {
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

        // No handle means not in a game
        if (gameEntity == null) {
            processLobbyDamage(livingEntity, ev);
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

        // FIXME:
        // For some reason this was missing?
        // If something breaks after this update, remove this ig
        instance.setLastDamager(data.getLastDamagerAsLiving());

        // PRE-EVENTS TESTS, SUCH AS GAME EFFECT, ETC.

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
            instance.setLastDamager(lastDamager);

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
            if (entityTeam != null && lastDamager != null && !gameEntity.equals(lastDamager) && entityTeam.isEntry(Entry.of(lastDamager))) {
                lastDamager.sendMessage("&cCannot damage teammates!");
                ev.setCancelled(true);
                ev.setDamage(0.0d);
                return;
            }

            // Player victim checks
            if (livingEntity instanceof Player player) {
                // Negate damage if blocking
                if (player.isBlocking()) {
                    instance.damage = 0.0;
                }
            }
        }

        // CALCULATE DAMAGE USING ATTRIBUTES

        // Calculate damage before calling events
        instance.calculateDamage();

        // CALL DAMAGE EVENT

        if (new GameDamageEvent(instance).callAndCheck()) {
            ev.setCancelled(true);
            return;
        }

        // PROCESS HERO EVENTS

        // As victim
        if (gameEntity instanceof GamePlayer player) {
            player.getHero().processDamageAsVictim(instance);
        }

        // As damager
        final LivingGameEntity lastDamager = instance.getDamager();

        if (lastDamager instanceof GamePlayer player) {
            player.getHero().processDamageAsDamager(instance);
        }

        // As projectile
        if (lastDamager instanceof GamePlayer player && finalProjectile != null) {
            player.getHero().processDamageAsDamagerProjectile(instance, finalProjectile);
        }

        // PROCESS GAME ENTITY DAMAGE
        gameEntity.onDamageTaken0(instance);

        if (lastDamager != null) {
            lastDamager.onDamageDealt0(instance);
        }

        if (instance.isCancelled()) {
            ev.setCancelled(true);
            return;
        }

        // Recalculate damage in case attributes changed
        final EntityAttributes entityAttributes = instance.getEntity().getAttributes();

        // Dodge
        if (instance.damage > 0 && entityAttributes.calculateDodge()) {
            ev.setCancelled(true);
            gameEntity.playDodgeFx();
            return;
        }

        // Ferocity
        if (lastDamager != null) {
            final EntityAttributes damagerAttributes = lastDamager.getAttributes();
            if ((instance.cause != null
                    && instance.cause.isAllowedForFerocity())
                    && !gameEntity.hasCooldown(Cooldown.FEROCITY)) {
                final int ferocityStrikes = damagerAttributes.getFerocityStrikes();

                if (ferocityStrikes > 0) {
                    gameEntity.executeFerocity(instance.damage, lastDamager, ferocityStrikes);
                }
            }
        }

        // Don't damage anything, only visually
        ev.setDamage(0.0d);

        // Store data in DamageData
        data.setLastDamage(instance.damage);
        data.setCrit(instance.isCrit);

        // Process true damage
        if (instance.cause.isTrueDamage()) {
            instance.damage = initialDamage;
            data.setLastDamage(initialDamage);
        }

        // Keep damage in limit
        if (instance.damage > DAMAGE_LIMIT) {
            instance.damage = DAMAGE_LIMIT;
            data.setLastDamage(DAMAGE_LIMIT);
        }

        // Progress stats for damager
        if (lastDamager instanceof GamePlayer player) {
            player.getStats().addValue(StatType.DAMAGE_DEALT, instance.getDamageWithinLimit());

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
            player.getStats().addValue(StatType.DAMAGE_TAKEN, instance.getDamageWithinLimit());

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
        final Player player = (Player) ev.getWhoClicked();
        final PlayerProfile profile = PlayerProfile.getProfile(player);

        if ((profile != null && profile.hasTrial()) || Manager.current().isGameInProgress()) {
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
        if (!Manager.current().isAbleToUse(profile)) {
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

        // Attempt to fix the jump boost bug
        final Vector velocity = player.getVelocity();

        if (velocity.getY() >= VELOCITY_MAX_Y) {
            ev.setCancelled(true);
            ev.setTo(from);
            return;
        }

        if (Manager.current().isGameInProgress()) {
            final GamePlayer gamePlayer = CF.getPlayer(player);
            if (gamePlayer == null) {
                return;
            }

            // AFK detection
            gamePlayer.markLastMoved(MoveType.MOUSE);

            if (hasNotMoved(from, to)) {
                return;
            }

            gamePlayer.markLastMoved(MoveType.KEYBOARD);

            // Handle no moving
            if (!gamePlayer.canMove()) {
                ev.setCancelled(true);
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

        // Bad!
        if (action == Action.PHYSICAL) {
            return;
        }

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

    /**
     * Because of spigots stupid thing where {@link ProjectileLaunchEvent} is actually a spawn event, I have to "re-wire" it to the custom event.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleProjectileLaunchRewire(final ProjectileLaunchEvent ev) {
        final Projectile entity = ev.getEntity();
        final ProjectileSource shooter = entity.getShooter();

        if (!(shooter instanceof LivingEntity living)) {
            return;
        }

        final LivingGameEntity livingGameEntity = CF.getEntity(living);

        if (livingGameEntity == null) {
            return;
        }

        // We need to make sure that entity has all the data
        // after spawning, so adding a 1 tick delay is fine here.
        // Yet I agree that this is hideous.
        new GameTask() {
            @Override
            public void run() {
                new ProjectilePostLaunchEvent(livingGameEntity, entity).call();
            }
        }.runTaskLater(1);
    }

    private void processLobbyDamage(@Nonnull LivingEntity entity, @Nonnull EntityDamageEvent ev) {
        final EntityDamageEvent.DamageCause cause = ev.getCause();

        if (entity.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.SLIME_BLOCK) {
            ev.setDamage(0.0d);
            return;
        }

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

        // Snap 1 tick later to prevent it from removing the weapon and to not use the ability accidentally
        player.schedule(player::snapToWeapon, 1);
        //player.snapToWeapon();
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
            if (player.isSettingEnabled(Settings.SHOW_COOLDOWN_MESSAGE)) {
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

    private record DisabledEffect(PotionEffectType type, AttributeType attribute) {
        public DisabledEffect(PotionEffectType type) {
            this(type, null);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            final DisabledEffect that = (DisabledEffect) o;
            return Objects.equals(type, that.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type);
        }
    }


}

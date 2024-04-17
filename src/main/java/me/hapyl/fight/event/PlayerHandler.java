package me.hapyl.fight.event;

import com.google.common.collect.Maps;
import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.event.custom.GameDamageEvent;
import me.hapyl.fight.event.custom.ProjectilePostLaunchEvent;
import me.hapyl.fight.game.*;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.effect.Effects;
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
import me.hapyl.fight.game.parkour.ParkourCourse;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.setting.Settings;
import me.hapyl.fight.game.stats.StatType;
import me.hapyl.fight.game.talents.ChargedTalent;
import me.hapyl.fight.game.talents.InputTalent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.techie.Talent;
import me.hapyl.fight.game.talents.witcher.Akciy;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.team.Entry;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.game.team.LocalTeamManager;
import me.hapyl.fight.game.weapons.BowWeapon;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.guesswho.GuessWho;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.ux.Notifier;
import me.hapyl.spigotutils.Eterna;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.parkour.Data;
import me.hapyl.spigotutils.module.parkour.ParkourRegistry;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
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
import org.bukkit.metadata.FixedMetadataValue;
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

    public static final double RANGE_KNOCKBACK_RESISTANCE = 0.7d;
    public static final double VELOCITY_MAX_Y = 4.821600093841552d;

    public final double[] bowScale = { 6.0d, 11.0d };

    public final double RANGE_SCALE = 6.28d;
    public final double DAMAGE_LIMIT = Short.MAX_VALUE;

    private final Set<EntityDamageEvent.DamageCause> instantDeathCauses
            = Set.of(EntityDamageEvent.DamageCause.VOID);

    private final Map<Projectile, DeflectedProjectile> deflectedProjectiles = Maps.newHashMap();

    public PlayerHandler() {
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handlePlayerJoin(PlayerJoinEvent ev) {
        final Player player = ev.getPlayer();
        final Manager manager = Manager.current();
        final PlayerProfile profile = manager.handlePlayer(player);

        if (manager.isGameInProgress()) {
            final GameInstance gameInstance = (GameInstance) manager.getCurrentGame();

            gameInstance.getMode().onJoin(gameInstance, player);
            ev.setJoinMessage(null);
        }
        else {
            if (!player.hasPlayedBefore()) {
                //new Tutorial(player);
            }
            // Only show the join message if the game is not in progress.
            // Game instance should modify and broadcast the join message.
            ev.setJoinMessage(profile.getJoinMessage());
        }

        if (profile.getRank().isStaff()) {
            Notifier.broadcastStaff("{} joined.", player.getName());
        }

        LocalTeamManager.updateAll();

        for (ParkourCourse value : ParkourCourse.values()) {
            value.getParkour().updateLeaderboardIfExists();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handlePlayerQuit(PlayerQuitEvent ev) {
        final Player player = ev.getPlayer();
        final Manager manager = Manager.current();
        final PlayerProfile profile = manager.getProfile(player);

        if (profile == null) {
            return; // Don't care
        }

        // GuessWho
        final GuessWho guessWhoGame = manager.getGuessWhoGame();

        if (guessWhoGame != null) {
            guessWhoGame.loseBecauseLeft(player);
        }

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
            ev.setQuitMessage(profile.getLeaveMessage());
        }

        if (profile.getRank().isStaff()) {
            Notifier.broadcastStaff("{} left.", player.getName());
        }

        // Save database
        manager.getOrCreateProfile(player).getDatabase().save();

        // Delete database instance
        PlayerDatabase.uninstantiate(player.getUniqueId());

        // Delete profile
        manager.removeProfile(player);
    }

    @EventHandler()
    public void handleBow(EntityShootBowEvent ev) {
        final LivingEntity entity = ev.getEntity();

        if (!Manager.current().isGameInProgress()) {
            return;
        }

        if (!(entity instanceof Player bukkitPlayer)) {
            return;
        }

        final GamePlayer player = CF.getPlayer(bukkitPlayer);
        final ItemStack bow = ev.getBow();

        if (bow == null || player == null) {
            return;
        }

        final Weapon weapon = player.getHero().getWeapon();
        final ItemStack weaponItem = weapon.getItem();

        if (!(weapon instanceof BowWeapon bowWeapon)) {
            return;
        }

        if (!weaponItem.isSimilar(bow)) {
            return;
        }

        final EntityAttributes attributes = player.getAttributes();
        final int cooldown = attributes.calculateRangeAttackSpeed(bowWeapon.getShotCooldown());

        player.setCooldownIgnoreModifier(weaponItem.getType(), cooldown);
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
        final GamePlayer player = CF.getPlayer(ev.getPlayer());

        if (player == null) {
            return;
        }

        ev.setCancelled(true);

        if (!player.isAbleToUseAbilities()) {
            return;
        }

        // Check for stun
        if (Talents.AKCIY.getTalent(Akciy.class).isStunned(player)) {
            player.sendMessage("&4&l※ &cCannot use ultimate while stunned!");
            return;
        }

        // Ultimate is not ready
        if (!player.isUltimateReady()) {
            player.sendTitle("&4&l※", "&cYour ultimate isn't ready!", 5, 15, 5);
            player.sendMessage("&4&l※ &cYour ultimate isn't ready!");
            return;
        }

        final Hero hero = player.getHero();
        final UltimateTalent ultimate = hero.getUltimate();

        ultimate.execute(player);
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
        DamageInstance instance = new DamageInstance(gameEntity, ev.getDamage());
        final EntityData data = gameEntity.getEntityData();

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
                            final double scale = arrow.isCritical() ? bowScale[1] : bowScale[0];
                            final double scaleFactor = instance.damage / scale;

                            final Weapon weapon = gamePlayer.getHero().getWeapon();
                            final double weaponDamage = weapon.getDamage();

                            instance.damage = weaponDamage * scaleFactor;
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

            if (lastDamager instanceof GamePlayer gamePlayer && gamePlayer.hasEffect(Effects.INVISIBILITY)) {
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
                boolean cancelDamage = true;

                if (lastDamager instanceof GamePlayer lastPlayerDamager) {
                    cancelDamage = lastPlayerDamager.getHero().processTeammateDamage(lastPlayerDamager, gameEntity, instance);
                }

                if (cancelDamage) {
                    gameEntity.onTeammateDamage(lastDamager);

                    ev.setCancelled(true);
                    ev.setDamage(0.0d);
                    return;
                }
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

        // A little hack to make fully charged arrows crit
        if (finalProjectile instanceof Arrow arrow) {
            if (arrow.isCritical()) {
                instance.setCrit(true);
            }
        }

        // Deflect
        if (finalProjectile != null) {
            final DeflectedProjectile deflectedProjectile = deflectedProjectiles.remove(finalProjectile);

            if (deflectedProjectile != null) {
                instance.setDamage(deflectedProjectile.damage);
                instance.setLastDamager(deflectedProjectile.damager);
            }
            else if (gameEntity instanceof GamePlayer gamePlayer) {
                if (gamePlayer.isDeflecting()) {
                    final Vector velocity = finalProjectile.getVelocity();
                    final double length = velocity.length() * 0.75d;

                    // FIXME (hapyl): 027, Mar 27: add dot check
                    
                    final Vector projectileVelocity = gamePlayer.getEyeDirection();
                    projectileVelocity.multiply(length);

                    final Projectile projectile = gamePlayer.getEntity().launchProjectile(finalProjectile.getClass(), projectileVelocity);

                    deflectedProjectiles.put(projectile, new DeflectedProjectile(projectile, gamePlayer, instance.getDamage()));

                    ev.setCancelled(true);
                    return;
                }
            }
        }

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

        // Yes, this is a hack before I love everyone
        if (finalProjectile != null && lastDamager != null) {
            final double kbResist = gameEntity.getAttributeValue(Attribute.GENERIC_KNOCKBACK_RESISTANCE);

            gameEntity.setAttributeValue(Attribute.GENERIC_KNOCKBACK_RESISTANCE, RANGE_KNOCKBACK_RESISTANCE);

            GameTask.runLater(() -> {
                gameEntity.setAttributeValue(Attribute.GENERIC_KNOCKBACK_RESISTANCE, kbResist);
            }, 1);
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

        if (Math.abs(velocity.getY()) >= VELOCITY_MAX_Y) {
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

            // FIXME (hapyl): 012, Feb 12:
            //  This sometimes does not pass the check because the
            //  first mouse movement is for whatever reason is on a slightly different
            //  coordinate than the previous one ¯\_(ツ)_/¯
            if (hasNotMoved(from, to)) {
                return;
            }

            gamePlayer.markLastMoved(MoveType.KEYBOARD);

            // Call Skin::onMove
            if (gamePlayer.isValidForCosmetics()) {
                gamePlayer.callSkinIfHas(skin -> skin.onMove(gamePlayer, to));
            }

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
            Chat.sendMessage(player, "&aClicked %s slot.".formatted(ev.getRawSlot()));
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

        if (!(shooter instanceof Player playerShooter)) {
            return;
        }

        final GamePlayer player = CF.getPlayer(playerShooter);

        if (player == null) {
            return;
        }

        // We need to make sure that entity has all the data
        // after spawning, so adding a 1 tick delay is fine here.
        // Yet I agree that this is hideous.
        new GameTask() {
            @Override
            public void run() {
                new ProjectilePostLaunchEvent(player, entity).call();
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

        final ParkourRegistry parkourRegistry = Eterna.getRegistry().parkourRegistry;
        final Data data = parkourRegistry.getData(player);

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

        talent.onUse(player);

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

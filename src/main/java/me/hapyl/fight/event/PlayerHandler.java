package me.hapyl.fight.event;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.game.*;
import me.hapyl.fight.game.achievement.Achievements;
import me.hapyl.fight.game.attribute.CriticalResponse;
import me.hapyl.fight.game.attribute.PlayerAttributes;
import me.hapyl.fight.game.damage.EntityData;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.parkour.CFParkour;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.setting.Setting;
import me.hapyl.fight.game.stats.StatType;
import me.hapyl.fight.game.talents.ChargedTalent;
import me.hapyl.fight.game.talents.InputTalent;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.game.tutorial.Tutorial;
import me.hapyl.fight.game.ui.display.DamageDisplay;
import me.hapyl.spigotutils.EternaPlugin;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.parkour.Data;
import me.hapyl.spigotutils.module.parkour.ParkourManager;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.BukkitUtils;
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
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Random;

/**
 * Handles all player related events.
 */
public class PlayerHandler implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handlePlayerJoin(PlayerJoinEvent ev) {
        final Player player = ev.getPlayer();
        final Manager manager = Manager.current();

        manager.handlePlayer(player);
        ScoreboardTeams.updateAll();

        if (manager.isGameInProgress()) {
            final GameInstance gameInstance = (GameInstance) manager.getCurrentGame();

            gameInstance.getMode().onJoin(gameInstance, player);
            //            gameInstance.populateScoreboard(player);
        }
        else {
            if (!player.hasPlayedBefore()) {
                new Tutorial(player);
            }
        }

        ev.setJoinMessage(Chat.format("&7[&a+&7] %s%s &ewants to fight!", player.isOp() ? "&c" : "", player.getName()));
    }

    // Prevent painting-breaking while the game is in progress
    @EventHandler()
    public void handlePaintingBreaking(HangingBreakEvent ev) {
        if (Manager.current().isGameInProgress()) {
            ev.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handlePlayerQuit(PlayerQuitEvent ev) {
        final Player player = ev.getPlayer();

        if (Manager.current().isGameInProgress()) {
            final IGameInstance game = Manager.current().getCurrentGame();
            final GamePlayer gamePlayer = GamePlayer.getExistingPlayer(player);

            if (gamePlayer != null) {
                game.getMode().onLeave((GameInstance) game, player);
            }
        }

        ev.setQuitMessage(Chat.format("&7[&c-&7] %s%s &ehas fallen!", player.isOp() ? "&c" : "", player.getName()));

        // save database
        Manager.current().getOrCreateProfile(player).getDatabase().save();

        // delete database instance
        PlayerDatabase.removeDatabase(player.getUniqueId());
        //        PlayerDatabase.dumpDatabaseInstanceInConsoleToConfirmThatThereIsNoMoreInstancesAfterRemoveIsCalledButThisIsTemporaryShouldRemoveOnProd();

        // Delete profile
        Manager.current().removeProfile(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleItemDropEntity(EntityDropItemEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleItemDropPlayer(PlayerDropItemEvent ev) {
        ev.setCancelled(true);
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
        ev.setCancelled(true);
        final Player player = ev.getPlayer();
        final Hero hero = Manager.current().getCurrentHero(player);

        if (!Manager.current().isAbleToUse(player)) {
            return;
        }

        final GamePlayer gamePlayer = getAlivePlayer(player);
        if (gamePlayer == null) {
            return;
        }

        if (gamePlayer.isUltimateReady()) {
            final UltimateTalent ultimate = hero.getUltimate();

            if (ultimate.hasCd(player)) {
                sendUltimateFailureMessage(player, "&cUltimate on cooldown for %ss.", BukkitUtils.roundTick(ultimate.getCdTimeLeft(player)));
                return;
            }

            if (!hero.predicateUltimate(player)) {
                sendUltimateFailureMessage(player, "&cUnable to use ultimate! " + hero.predicateMessage(player));
                return;
            }

            if (hero.isUsingUltimate(player)) {
                sendUltimateFailureMessage(player, "&cAlready using ultimate!");
                return;
            }

            //ultimate.execute0(player);
            hero.useUltimate(player);
            ultimate.startCd(player);
            gamePlayer.setUltPoints(0);

            // Stats
            gamePlayer.getStats().addValue(StatType.ULTIMATE_USED, 1);

            if (hero.getUltimateDuration() > 0) {
                hero.setUsingUltimate(player, true, hero.getUltimateDuration());
            }

            // Achievement
            Achievements.USE_ULTIMATES.complete(player);

            for (final Player online : Bukkit.getOnlinePlayers()) {
                Chat.sendMessage(
                        online,
                        "&b&l※ &b%s used &l%s&7!".formatted(online == player ? "You" : player.getName(), ultimate.getName())
                );
                PlayerLib.playSound(online, ultimate.getSound(), ultimate.getPitch());
            }
        }
        else if (!hero.isUsingUltimate(player)) {
            Chat.sendTitle(player, "&4&l※", "&cYour ultimate isn't ready!", 5, 15, 5);
            sendUltimateFailureMessage(player, "&cYour ultimate isn't ready!");
        }
        // ignore if using ultimate
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleDamage0(EntityDamageEvent ev) {
        final Entity entity = ev.getEntity();
        final EntityDamageEvent.DamageCause cause = ev.getCause();

        double damage = ev.getDamage();
        Projectile finalProjectile = null;

        // Ignore non living entities and/or void damage
        if (!(entity instanceof LivingEntity livingEntity) || cause == EntityDamageEvent.DamageCause.VOID) {
            return;
        }

        // Ignore lobby damage
        if (!Manager.current().isGameInProgress()) {
            // Don't cancel when falling on slime block for a slime glitch
            if (entity.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.SLIME_BLOCK) {
                ev.setDamage(0.0d);
                return;
            }

            processLobbyDamage(entity, cause);
            ev.setCancelled(true);
            return;
        }

        // This is what actually stores all the custom data
        // needed to handle custom damage/causes.
        final EntityData data = EntityData.getEntityData(livingEntity);

        // REASSIGNMENT STATE
        // If an entity wasn't hit by using DamageHandler, we
        // need to store the data from this event into DamageData,
        // since it is now the 'real' data.

        // Reassign cause
        data.setLastDamageCauseIfNative(cause);

        // Pre-events tests, such as GameEffect, etc.
        if (livingEntity instanceof Player player) {
            final IGamePlayer gamePlayer = GamePlayer.getPlayer(player);

            // Test for fall damage resistance
            if (data.getLastDamageCauseNonNull() == EnumDamageCause.FALL && gamePlayer.hasEffect(GameEffectType.FALL_DAMAGE_RESISTANCE)) {
                ev.setCancelled(true);
                gamePlayer.removeEffect(GameEffectType.FALL_DAMAGE_RESISTANCE);
                return;
            }
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
                        damage /= 1.5F;
                    }

                    data.setLastDamagerIfNative(player);
                }
                // Check for projectile damage
                else if (damager instanceof Projectile projectile) {
                    // Scale it down according to a super cool formula for players
                    if (projectile.getShooter() instanceof Player player && projectile instanceof AbstractArrow arrow) {
                        final double weaponDamage = GamePlayer.getPlayer(player).getHero().getWeapon().getDamage();
                        final double scaleFactor = 1 + (damage / 8.933d);

                        damage = weaponDamage * scaleFactor;

                        if (arrow.isCritical()) {
                            damage *= 1.5d;
                        }
                    }

                    // Reassign damager to shooter
                    if (projectile.getShooter() instanceof LivingEntity living) {
                        data.setLastDamagerIfNative(living);
                    }

                    // Store projectile for further use
                    finalProjectile = projectile;
                }
                // Default to damager if they're living
                else if (damager instanceof LivingEntity living) {
                    data.setLastDamagerIfNative(living);
                }
            }

            // Process invisibility
            final LivingEntity lastDamager = data.getLastDamager();

            if (lastDamager instanceof Player player && player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                final boolean cancelDamage = GamePlayer.getPlayer(player)
                        .getHero()
                        .processInvisibilityDamage(player, lastDamager, damage);

                if (cancelDamage) {
                    ev.setDamage(0.0d);
                    ev.setCancelled(true);
                    return;
                }
            }

            // Process player to player checks
            if (entity instanceof Player player && lastDamager instanceof Player playerDamager) {

                // Check for teammate
                if (GameTeam.isTeammate(player, playerDamager)) {
                    Chat.sendMessage(playerDamager, "&cCannot damage teammates!");

                    ev.setDamage(0.0d);
                    ev.setCancelled(true);
                    return;
                }
            }

            // Apply effects and modifiers for the damager
            if (lastDamager != null) {
                final PotionEffect effectStrength = lastDamager.getPotionEffect(PotionEffectType.INCREASE_DAMAGE);
                final PotionEffect effectWeakness = lastDamager.getPotionEffect(PotionEffectType.WEAKNESS);

                // Strength
                // The current formula is +50% damage per strength lvl
                if (effectStrength != null) {
                    final int amplifier = effectStrength.getAmplifier() + 1;

                    damage -= (3.0d * amplifier); // Remove vanilla strength
                    damage += (damage * 0.5d * amplifier);
                }

                // Weakness
                // The current formula reduces damage by half
                if (effectWeakness != null) {
                    damage -= (4.0d * effectWeakness.getAmplifier() + 1);
                    damage /= 2.0d;
                    // FIXME (hapyl): 027, May 27, 2023: Player's cannot damage with weakness, convert to GameEffect or use something like UNLUCK
                }

                // Check for GameEffect is it is player
                if (lastDamager instanceof Player player) {
                    final IGamePlayer gamePlayer = GamePlayer.getPlayer(player);

                    // Check for stun and nullity the damage
                    if (gamePlayer.hasEffect(GameEffectType.STUN)) {
                        damage = 0.0d;
                    }

                    // Check for lockdown and cancel the event
                    if (gamePlayer.hasEffect(GameEffectType.LOCK_DOWN)) {
                        ev.setCancelled(true);
                        gamePlayer.sendTitle("&c&lLOCKDOWN", "&cCannot deal damage.", 0, 20, 0);
                        return;
                    }
                }
            }

            // Apply modifiers for a victim
            final PotionEffect effectResistance = livingEntity.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE);

            // Reduce damage by 85%
            if (effectResistance != null) {
                damage *= 0.15d;
            }

            // Player victim checks
            if (livingEntity instanceof Player player) {
                // Negate damage if blocking
                if (player.isBlocking()) {
                    damage = 0.0;
                }

                // Check for GameEffects
                final IGamePlayer gamePlayer = GamePlayer.getPlayer(player);

                // Remove stun once damaged
                if (gamePlayer.hasEffect(GameEffectType.STUN)) {
                    gamePlayer.removeEffect(GameEffectType.STUN);
                }

                // Increase damage taken by 2 if vulnerable
                if (gamePlayer.hasEffect(GameEffectType.VULNERABLE)) {
                    damage *= 2.0d;
                }
            }
        }

        // PROCESS HERO EVENTS

        boolean cancelDamage = false;

        // As victim
        if (livingEntity instanceof Player player) {
            final DamageOutput output = getDamageOutput(player, data.getLastDamager(), damage, false);

            if (output != null) {
                damage = output.getDamage();
                cancelDamage = output.isCancelDamage();
            }
        }

        // As damager
        if (data.getLastDamager() instanceof Player player) {
            final DamageOutput output = getDamageOutput(player, livingEntity, damage, true);

            if (output != null) {
                damage = output.getDamage();

                // Don't 'uncancel' the event
                if (!cancelDamage) {
                    cancelDamage = output.isCancelDamage();
                }
            }
        }

        // As projectile
        if (data.getLastDamager() instanceof Player player && finalProjectile != null && Manager.current().isGameInProgress()) {
            final DamageOutput output = GamePlayer.getPlayer(player)
                    .getHero()
                    .processDamageAsDamagerProjectile(new DamageInput(player, livingEntity, damage), finalProjectile);

            if (output != null) {
                damage = output.getDamage();

                // Don't 'uncancel' the event
                if (!cancelDamage) {
                    cancelDamage = output.isCancelDamage();
                }
            }
        }

        // Don't damage players
        ev.setDamage(livingEntity instanceof Player ? 0.0d : damage);

        if (cancelDamage) {
            ev.setCancelled(true);
            return;
        }

        boolean isCrit = false;

        // CALCULATE DAMAGE USING ATTRIBUTES

        // Outgoing damage
        if (data.getLastDamager() instanceof Player player) {
            final GamePlayer gamePlayer = GamePlayer.getExistingPlayer(player);

            if (gamePlayer != null) {
                final PlayerAttributes attributes = gamePlayer.getAttributes();
                final CriticalResponse criticalResponse = attributes.calculateOutgoingDamage(damage, data.getLastDamageCause());

                damage = criticalResponse.damage();
                isCrit = criticalResponse.isCrit();
            }
        }

        // Incoming damage
        if (livingEntity instanceof Player player) {
            final GamePlayer gamePlayer = GamePlayer.getExistingPlayer(player);

            if (gamePlayer != null) {
                damage = gamePlayer.getAttributes().calculateIncomingDamage(damage);
            }
        }

        // Don't allow negative damage
        if (damage < 0.0d) {
            damage = 0.0d;
        }

        // Store data in DamageData
        data.setLastDamage(damage);
        data.setCrit(isCrit);

        // Show damage indicator if dealt more
        // than 1 damage to remove clutter
        if (damage >= 1.0d && !(entity instanceof ArmorStand)) {
            //            final DamageIndicator damageIndicator = new DamageIndicator(entity.getLocation(), damage, isCrit);
            //            damageIndicator.display(isCrit ? 30 : 20);

            new DamageDisplay(damage, isCrit).display(livingEntity.getEyeLocation());
        }

        // Progress stats for damager
        if (data.getLastDamager() instanceof Player player) {
            final GamePlayer gamePlayer = GamePlayer.getExistingPlayer(player);

            if (gamePlayer != null) {
                gamePlayer.getStats().addValue(StatType.DAMAGE_DEALT, damage);
            }

            if (Setting.SHOW_DAMAGE_IN_CHAT.isEnabled(player)) {
                data.notifyChatOutgoing(player);
            }
        }

        // Make sure not to kill players but instead
        // put them in spectator mode
        if (livingEntity instanceof Player player) {
            final GamePlayer gamePlayer = GamePlayer.getExistingPlayer(player);

            if (gamePlayer != null) {
                final double health = gamePlayer.getHealth();

                // Decrease health
                gamePlayer.decreaseHealth(damage, data.getLastDamager());

                // Progress stats for a victim
                gamePlayer.getStats().addValue(StatType.DAMAGE_TAKEN, damage);

                // Cancel the event if finishing blow
                if (damage >= health) {
                    ev.setCancelled(true);
                    return;
                }
            }

            if (Setting.SHOW_DAMAGE_IN_CHAT.isEnabled(player)) {
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

    @EventHandler()
    public void handleProjectileDamage(ProjectileLaunchEvent ev) {
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

        if (!Manager.current().isAbleToUse(player)) {
            return;
        }

        cancelInputTalent(player);

        // 1-2 -> Simple Abilities, 3-5 -> Complex Abilities (Extra)
        // 0 -> Weapon Slot

        final int newSlot = ev.getNewSlot();
        if (newSlot <= 0 || newSlot > 5) {
            return;
        }

        final Hero hero = Manager.current().getCurrentHero(player);
        final PlayerInventory inventory = player.getInventory();

        // don't care if talent is null, either not a talent or not complete
        // null or air item means this skill should be ignored for now (not active)
        final Talent talent = hero.getTalent(newSlot);
        final ItemStack itemOnNewSlot = inventory.getItem(newSlot);

        if (talent == null || !isValidItem(talent, itemOnNewSlot)) {
            return;
        }

        // Execute talent
        checkAndExecuteTalent(player, talent, newSlot);
        final IGamePlayer gamePlayer = GamePlayer.getPlayer(player);

        if (talent instanceof InputTalent inputTalent) {
            gamePlayer.setInputTalent(inputTalent);
        }
        else {
            ev.setCancelled(true);
            inventory.setHeldItemSlot(0);
            cancelInputTalent(player);
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

                // I think this should be used instead of cancel to not cancel bows etc.
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
            final IGamePlayer gp = GamePlayer.getPlayer(player);

            // AFK detection
            // Mark as moved even if the player can't move and only moved the mouse
            gp.markLastMoved();

            if (hasNotMoved(from, to)) {
                return;
            }

            // Handle no moving
            if (!gp.canMove()) {
                ev.setCancelled(true);
                return;
            }

            // Amnesia
            if (gp.hasEffect(GameEffectType.AMNESIA)) {
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
     * Handles Input Talent.
     */
    @EventHandler()
    public void handleInputTalent(PlayerInteractEvent ev) {
        final Player player = ev.getPlayer();
        final Action action = ev.getAction();
        final boolean isLeftClick = action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK;

        handleInputTalent(player, isLeftClick);
    }

    @EventHandler()
    public void handleEntityInteract(EntityDamageByEntityEvent ev) {
        final Entity damager = ev.getDamager();

        if (damager instanceof Player player) {
            handleInputTalent(player, true);
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

    private void processLobbyDamage(@Nonnull Entity entity, @Nonnull EntityDamageEvent.DamageCause cause) {
        if (!(entity instanceof Player player)) {
            return;
        }

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

    private void cancelInputTalent(Player player) {
        final IGamePlayer gamePlayer = GamePlayer.getPlayer(player);
        final InputTalent inputTalent = gamePlayer.getInputTalent();

        if (inputTalent != null) {
            inputTalent.onCancel(player);
            gamePlayer.setInputTalent(null);
        }
    }

    private void handleInputTalent(Player player, boolean isLeftClick) {
        final IGamePlayer gamePlayer = GamePlayer.getPlayer(player);
        final InputTalent talent = gamePlayer.getInputTalent();

        if (talent == null || !checkTalent(player, talent)) {
            return;
        }

        final Response response = isLeftClick ? talent.onLeftClick(player) : talent.onRightClick(player);
        final String usage = talent.getUsage(isLeftClick);

        if (!checkResponse(player, response)) {
            return;
        }

        // \/ Talent executed \/
        gamePlayer.setInputTalent(null); // keep this above CD and slot changes!

        if (isLeftClick) {
            talent.startCdLeft(player);
        }
        else {
            talent.startCdRight(player);
        }

        talent.addPoint(player, isLeftClick);

        // Add 1 tick cooldown to a weapon to prevent accidental use
        final PlayerInventory inventory = player.getInventory();
        final ItemStack item = inventory.getItem(0);

        if (item != null) {
            final Material type = item.getType();
            if (!player.hasCooldown(type)) {
                player.setCooldown(type, 1);
            }
        }

        inventory.setHeldItemSlot(0);
    }

    private GamePlayer getAlivePlayer(Player player) {
        final Manager manager = Manager.current();
        if (manager.isTrialExistsAndIsOwner(player)) {
            return manager.getTrial().getGamePlayer();
        }

        return GamePlayer.getExistingPlayer(player);
    }

    private void sendUltimateSuccessMessage(Player player, String str, Object... objects) {
        Chat.sendMessage(player, "&b&l※ &a" + Chat.format(str, objects));
    }

    private void sendUltimateFailureMessage(Player player, String str, Object... objects) {
        Chat.sendMessage(player, "&4&l※ &c" + Chat.format(str, objects));
        PlayerLib.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
    }

    private DamageOutput getDamageOutput(Player player, LivingEntity entity, double damage, boolean asDamager) {
        if (Manager.current().isPlayerInGame(player)) {
            final IGamePlayer gamePlayer = GamePlayer.getPlayer(player);
            final Hero hero = gamePlayer.getHero();

            final DamageInput input = new DamageInput(player, entity, gamePlayer.getLastDamageCause(), damage);
            return asDamager ? hero.processDamageAsDamager(input) : hero.processDamageAsVictim(input);
        }
        return null;
    }

    private boolean isValidItem(Talent talent, ItemStack stack) {
        return stack != null && !stack.getType().isAir();
        //return talent.getMaterial() == stack.getType();
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
    private boolean checkTalent(Player player, Talent talent) {
        // null check
        if (talent == null) {
            Chat.sendMessage(player, "&cNullPointerException: talent is null");
            return false;
        }

        // cooldown check
        if (talent.hasCd(player)) {
            Chat.sendMessage(player, "&cTalent on cooldown for %ss.", BukkitUtils.roundTick(talent.getCdTimeLeft(player)));
            player.getInventory().setHeldItemSlot(0); // work-around for InputTalent
            return false;
        }

        // charge check
        if (talent instanceof ChargedTalent chargedTalent) {
            if (chargedTalent.getChargedAvailable(player) <= 0) {
                Chat.sendMessage(player, "&cOut of charges!");
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
    private boolean checkResponse(Player player, Response response) {
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

    private void checkAndExecuteTalent(Player player, Talent talent, int slot) {
        if (!checkTalent(player, talent)) {
            return;
        }

        // Make sure the talent item is still in the slot
        final ItemStack itemInSlot = player.getInventory().getItem(slot);
        if (itemInSlot == null || itemInSlot.getType() != talent.getMaterial()) {
            return;
        }

        // Execute talent and get response
        final Response response = talent.execute0(player);
        final IGamePlayer gamePlayer = GamePlayer.getPlayer(player);

        // If not error, add to the queue
        // Yeah I know two of the 'same' checks, but I'll
        // make it look good later maybe or not or I don't care
        if (!response.isError()) {
            gamePlayer.getTalentQueue().add(talent);
        }

        if (!checkResponse(player, response)) {
            return;
        }

        // \/ Talent executed \/
        if (talent instanceof ChargedTalent chargedTalent) {
            chargedTalent.setLastKnownSlot(player, slot);
            chargedTalent.removeChargeAndStartCooldown(player);
        }

        final int point = talent.getPoint();

        if (point > 0) {
            gamePlayer.addUltimatePoints(point);
        }

        talent.startCd(player);
    }
}

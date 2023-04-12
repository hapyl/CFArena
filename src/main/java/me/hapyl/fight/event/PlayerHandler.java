package me.hapyl.fight.event;

import me.hapyl.fight.Main;
import me.hapyl.fight.Shortcuts;
import me.hapyl.fight.game.*;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.talents.ChargedTalent;
import me.hapyl.fight.game.talents.InputTalent;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.game.tutorial.Tutorial;
import me.hapyl.fight.game.ui.DamageIndicator;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Handles all player related events.
 */
public class PlayerHandler implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handlePlayerJoin(PlayerJoinEvent ev) {
        final Player player = ev.getPlayer();
        final Main plugin = Main.getPlugin();
        final Manager manager = Manager.current();

        if (manager.isGameInProgress()) {
            final GameInstance gameInstance = (GameInstance) manager.getCurrentGame();

            gameInstance.getMode().onJoin(gameInstance, player);
            gameInstance.populateScoreboard(player);
        }
        else {
            plugin.handlePlayer(player);

            if (!player.hasPlayedBefore()) {
                new Tutorial(player);
            }
        }

        ev.setJoinMessage(Chat.format("&7[&a+&7] %s%s &ewants to fight!", player.isOp() ? "&c" : "", player.getName()));
    }

    // Prevent painting breaking while the game is in progress
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

            if (gamePlayer == null) {
                return;
            }

            game.getMode().onLeave((GameInstance) game, player);
        }

        // save database
        Shortcuts.getDatabase(player).save();

        final GameTeam playerTeam = GameTeam.getPlayerTeam(player);
        if (playerTeam != null) {
            playerTeam.removeFromTeam(player);
        }

        ev.setQuitMessage(Chat.format("&7[&c-&7] %s%s &ehas fallen!", player.isOp() ? "&c" : "", player.getName()));

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
        // Auto-Generated
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
            gamePlayer.getStats().addValue(StatContainer.Type.ULTIMATE_USED, 1);

            if (hero.getUltimateDuration() > 0) {
                hero.setUsingUltimate(player, true, hero.getUltimateDuration());
            }

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

    /**
     * Handler for damage calculations.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleDamage(EntityDamageEvent ev) {
        final Entity entity = ev.getEntity();

        Projectile projectile = null;
        LivingEntity damagerFinal = null;
        double damage = ev.getDamage();

        // Ignore non living entity and void damage
        final EntityDamageEvent.DamageCause cause = ev.getCause();
        if (!(entity instanceof LivingEntity livingEntity) || cause == EntityDamageEvent.DamageCause.VOID) {
            return;
        }

        // Ignore trial or non instanced damage
        if (entity instanceof Player player && !Manager.current().isAbleToUse(player)) {
            ev.setCancelled(true);
            return;
        }

        // Pre events tests, such as GameEffect etc
        if (livingEntity instanceof Player player) {
            final IGamePlayer gamePlayer = GamePlayer.getPlayer(player);

            // Fall damage
            if (cause == EntityDamageEvent.DamageCause.FALL && gamePlayer.hasEffect(GameEffectType.FALL_DAMAGE_RESISTANCE)) {
                ev.setCancelled(true);
                gamePlayer.removeEffect(GameEffectType.FALL_DAMAGE_RESISTANCE);
                return;
            }
        }

        // Reassign damage cause
        if (livingEntity instanceof Player playerVictim) {
            if ((GamePlayer.getPlayer(playerVictim) instanceof GamePlayer player) && player.isNativeDamage()) {
                player.setLastDamageCause(EnumDamageCause.getFromCause(cause));
            }
        }

        // Calculate base damage
        if (ev instanceof EntityDamageByEntityEvent event) {
            final Entity damager = event.getDamager();

            // Ignore all this if self damage (fall damage, explosion etc.)
            if (damager != entity) {
                if (damager instanceof Player playerDamager) {
                    // Remove critical hit
                    if (playerDamager.getFallDistance() > 0.0F && !playerDamager.isOnGround() &&
                            !playerDamager.hasPotionEffect(PotionEffectType.BLINDNESS) && playerDamager.getVehicle() == null) {
                        damage /= 1.5F;
                    }

                    final Material type = playerDamager.getInventory().getItemInMainHand().getType();
                    // Decrease damage if hitting with a bow
                    //                    if (cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK && type == Material.BOW || type == Material.CROSSBOW) {
                    //                        Debugger.log("reduced damage");
                    //                        damage *= 0.4d;
                    //                    }

                    // Assign the damager
                    damagerFinal = playerDamager;
                }
                else if (damager instanceof Projectile proj && proj.getShooter() instanceof Player playerDamager) {
                    // Increase damage if fully charged shot
                    if (proj instanceof AbstractArrow arrow) {
                        final double weaponDamage = GamePlayer.getPlayer(playerDamager).getHero().getWeapon().getDamage();
                        final double scale = 1 + (damage / 8.933d);

                        damage = weaponDamage * scale;

                        if (arrow.isCritical()) {
                            damage *= 1.5d;
                        }
                    }

                    // Assign the damager
                    projectile = proj;
                    damagerFinal = playerDamager;
                }
                else if (damager instanceof LivingEntity living) {
                    damagerFinal = living;
                }
            }

            // Invisibility Check
            if (damagerFinal instanceof Player playerDamager && playerDamager.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                final boolean cancel = GamePlayer.getPlayer(playerDamager)
                        .getHero()
                        .processInvisibilityDamage(playerDamager, livingEntity, damage);

                if (cancel) {
                    ev.setDamage(0.0d);
                    ev.setCancelled(true);
                    return;
                }
            }

            // Player to Player tests
            if (damagerFinal instanceof Player playerDamager && entity instanceof Player playerVictim) {
                boolean cancelDamage = false;

                // Teammate check
                if ((GameTeam.isTeammate(playerDamager, playerVictim))) {
                    Chat.sendMessage(playerDamager, "&cCannot damage teammates!");
                    cancelDamage = true;
                }

                // Invisibility
                //if (!playerVictim.canSee(playerDamager)) {
                //    Chat.sendMessage(playerDamager, "&cCannot damage while invisible!");
                //    cancelDamage = true;
                //}

                if (cancelDamage) {
                    ev.setCancelled(true);
                    ev.setDamage(0.0d);
                    return;
                }
            }

            // Apply modifiers for damager
            if (damagerFinal != null) {
                final PotionEffect effectStrength = damagerFinal.getPotionEffect(PotionEffectType.INCREASE_DAMAGE);
                final PotionEffect effectWeakness = damagerFinal.getPotionEffect(PotionEffectType.WEAKNESS);

                // Add 20% of damage per strength level
                if (effectStrength != null) {
                    damage += ((damage * 0.2d) * (effectStrength.getAmplifier() + 1));
                }

                // Reduce damage by half is has weakness effect
                if (effectWeakness != null) {
                    damage /= 2;
                }

                // Apply GameEffect for damager
                if (damagerFinal instanceof Player player) {
                    final IGamePlayer gp = GamePlayer.getPlayer(player);
                    if (gp.hasEffect(GameEffectType.STUN)) {
                        damage = 0.0d;
                    }

                    // lockdown
                    if (gp.hasEffect(GameEffectType.LOCK_DOWN)) {
                        ev.setCancelled(true);
                        gp.sendTitle("&c&lLOCKDOWN", "&cUnable to deal damage.", 0, 20, 0);
                        return;
                    }
                }

            }

            // Apply modifiers for victim
            {
                final PotionEffect effectResistance = livingEntity.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                // reduce damage by 85% if we have resistance
                if (effectResistance != null) {
                    damage *= 0.15d;
                }

                // negate all damage is blocking
                if (livingEntity instanceof Player player) {
                    if (player.isBlocking()) {
                        damage = 0.0d;
                    }

                    // Apply GameEffect for victim */
                    final IGamePlayer gp = GamePlayer.getPlayer(player);
                    if (gp.hasEffect(GameEffectType.STUN)) {
                        gp.removeEffect(GameEffectType.STUN);
                    }

                    if (gp.hasEffect(GameEffectType.VULNERABLE)) {
                        damage *= 2.0d;
                    }
                }
            }
        }

        // Set damager if not custom hit
        if (livingEntity instanceof Player player) {
            final IGamePlayer gamePlayer = GamePlayer.getPlayer(player);
            if (gamePlayer instanceof GamePlayer gp && gp.isNativeDamage()) {
                gamePlayer.setLastDamager(damagerFinal);
            }
        }

        // Process damager and victims hero damage processors
        final List<String> extraStrings = new ArrayList<>();

        // Victim
        boolean cancelEvent = false;

        if (livingEntity instanceof Player player) {
            final DamageOutput output = getDamageOutput(player, damagerFinal, damage, false);
            if (output != null) {
                damage = output.getDamage();
                cancelEvent = output.isCancelDamage();
                //if (output.hasExtraDisplayStrings()) {
                //    extraStrings.addAll(Arrays.stream(output.getExtraDisplayStrings()).toList());
                //}
            }
        }

        // Damager
        if (damagerFinal instanceof Player player) {
            final DamageOutput output = getDamageOutput(player, livingEntity, damage, true);
            if (output != null) {
                damage = output.getDamage();
                if (!cancelEvent) {
                    cancelEvent = output.isCancelDamage();
                    //if (output.hasExtraDisplayStrings()) {
                    //    extraStrings.addAll(Arrays.stream(output.getExtraDisplayStrings()).toList());
                    //}
                }
            }
        }

        // Damager Projectile
        if (damagerFinal instanceof Player player && projectile != null && Manager.current().isGameInProgress()) {
            final DamageOutput output = GamePlayer.getPlayer(player)
                    .getHero()
                    .processDamageAsDamagerProjectile(new DamageInput(player, livingEntity, damage), projectile);

            if (output != null) {
                damage = output.getDamage();
                if (!cancelEvent) {
                    cancelEvent = output.isCancelDamage();
                    //if (output.hasExtraDisplayStrings()) {
                    //    extraStrings.addAll(Arrays.stream(output.getExtraDisplayStrings()).toList());
                    //}
                }
            }
        }

        // Only damage entities other that the player
        ev.setDamage(livingEntity instanceof Player ? 0.0d : damage);

        // Cancel even if necessary
        if (cancelEvent) {
            ev.setCancelled(true);
            return;
        }

        // Don't allow negative damage
        if (damage < 0) {
            damage = 0;
        }

        // Create damage indicator if dealt 1 or more damage
        // TODO (hapyl): 010, Apr 10, 2023: Create separate indicators.
        if (damage >= 1.0d && !(entity instanceof ArmorStand)) {
            final DamageIndicator damageIndicator = new DamageIndicator(entity.getLocation(), damage);
            damageIndicator.display(20);
        }

        if (damagerFinal instanceof Player player) {
            final GamePlayer gamePlayer = GamePlayer.getExistingPlayer(player);

            if (gamePlayer != null) {
                gamePlayer.getStats().addValue(StatContainer.Type.DAMAGE_DEALT, damage);
            }
        }

        // Make sure not to kill player but instead put them in spectator
        if (entity instanceof Player player) {
            final GamePlayer gamePlayer = GamePlayer.getExistingPlayer(player);

            // If game player is null means the game is not in progress
            if (gamePlayer != null) {
                final double health = gamePlayer.getHealth();
                gamePlayer.decreaseHealth(damage, damagerFinal);

                // Stats
                gamePlayer.getStats().addValue(StatContainer.Type.DAMAGE_TAKEN, damage);

                // Cancel even if player died so there is no real death
                if (damage >= health) {
                    ev.setCancelled(true);
                    return;
                }
            }

            // fail-safe for actual health
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
        final Talent talent = Manager.current().getTalent(hero, newSlot);
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
                // allow to interact with intractable items
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
            // Mark as moved even if player can't move and only moved the mouse
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
            Chat.sendMessage(player, "&aClicked %s slot.", ev.getRawSlot());
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

        gamePlayer.setInputTalent(null); // keep this above CD and slot changes!

        if (isLeftClick) {
            talent.startCdLeft(player);
        }
        else {
            talent.startCdRight(player);
        }

        talent.addPoint(player, isLeftClick);
        player.getInventory().setHeldItemSlot(0);
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
            GamePlayer.getPlayer(player).addUltimatePoints(point);
        }

        talent.startCd(player);
    }

}

package me.hapyl.fight.game.heroes.archive.juju;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.fight.Main;
import me.hapyl.fight.event.DamageInput;
import me.hapyl.fight.event.DamageOutput;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroEquipment;
import me.hapyl.fight.game.heroes.Role;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.archive.juju.ArrowShield;
import me.hapyl.fight.game.talents.archive.juju.Climb;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class JuJu extends Hero implements Listener {

    private final Set<Arrow> passiveArrows = Sets.newHashSet();
    private final Map<Player, Integer> ultimateArrows = Maps.newHashMap();

    private final int maxUltimateArrows = 5;
    private final int cdBetweenUltimateArrows = 10;

    private final int maxBounce = 2;

    public JuJu() {
        super("JuJu the Bandit");

        setRole(Role.RANGE);
        setMinimumLevel(5);

        setInfo("A bandit from the depths of the jungle. Highly skilled in range combat.");
        setItem("9dcff46588f394987979b7dd770adea94d8ee1fb1f7b8704e1baf91227f6a4d");

        final HeroEquipment equipment = getEquipment();
        equipment.setChestplate(62, 51, 40);
        equipment.setLeggings(62, 51, 40);
        equipment.setBoots(16, 13, 10);

        setWeapon(new Weapon(Material.BOW)
                .setName("Twisted")
                .setDescription("A bow made of anything you can find in the middle of the jungle.")
                .setDamage(4.0d));

        /**
         * Changes to ultimate:
         *
         * - Elusive Burst will shoot instantly.
         * - Can be used up be X times or Y seconds.
         */

        setUltimate(new UltimateTalent(
                "Tricks of the Jungle",
                "Remember all the tricks you learned in the jungle for {duration}.____During this time, you can shoot your bow instantly for up to &b%s&7 times, and arrows you shoot will be considered as &e%s&7 arrows.____&e%s&7 can also be used without restrictions.".formatted(
                        maxUltimateArrows,
                        getPassiveTalent().getName(),
                        getSecondTalent().getName()
                ),
                65
        ).setDurationSec(10).setCooldownSec(30).setItem(Material.OAK_SAPLING));

        final UltimateTalent ultimate = getUltimate();
        ultimate.addAttributeDescription("Maximum Arrows", maxUltimateArrows);
        ultimate.addAttributeDescription("Cooldown between Shots", BukkitUtils.roundTick(cdBetweenUltimateArrows) + "s");
    }

    @EventHandler()
    public void handleBowCharge(PlayerInteractEvent ev) {
        final Player player = ev.getPlayer();
        final Action action = ev.getAction();

        if (!validatePlayer(player)
                || !isUsingUltimate(player)
                || ev.getHand() == EquipmentSlot.OFF_HAND
                || (action == Action.LEFT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR)) {
            return;
        }

        final ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (itemInHand.getType() != Material.BOW) {
            return;
        }

        GamePlayer.getPlayer(player).interrupt();

        player.launchProjectile(Arrow.class);
        player.setCooldown(Material.BOW, cdBetweenUltimateArrows);

        ultimateArrows.compute(player, (p, i) -> i == null ? 1 : i - 1);

        // End if all arrows are used
        if (ultimateArrows.get(player) == 0) {
            ultimateArrows.remove(player);
            setUsingUltimate(player, false);
        }
    }

    //@EventHandler
    //public void onProjectileHit(ProjectileHitEvent event) {
    //    if (event.getEntityType() == EntityType.ARROW && event.getEntity().getShooter() instanceof Player player) {
    //        final Arrow arrow = (Arrow) event.getEntity();
    //        final BlockFace hitBlockFace = event.getHitBlockFace();
    //
    //        if (!validatePlayer(player, Heroes.JUJU) || !isUsingUltimate(player) || hitBlockFace == null) {
    //            return;
    //        }
    //
    //        if (getBounce(arrow) >= maxBounce) {
    //            return;
    //        }
    //
    //        bounceArrow(arrow, hitBlockFace);
    //    }
    //}

    public Arrow bounceArrow(Arrow arrow, BlockFace hitBlock) {
        // Calculate the angle of reflection for the arrow.
        final Vector arrowVelocity = arrow.getVelocity();
        final Vector surfaceNormal = hitBlock.getDirection();
        final Vector reflectedVelocity = arrowVelocity.subtract(surfaceNormal.multiply(1.5d * arrowVelocity.dot(surfaceNormal)));

        // Set the new velocity of the arrow to the reflected velocity.
        return Entities.ARROW.spawn(arrow.getLocation(), self -> {
            self.setShooter(arrow.getShooter());
            self.setDamage(arrow.getDamage() * 2);
            self.setColor(Color.GREEN);
            self.setCritical(arrow.isCritical());
            self.setVelocity(reflectedVelocity);
        });
    }

    private int getBounce(Arrow arrow) {
        final List<MetadataValue> bounce = arrow.getMetadata("bounce");
        return bounce.isEmpty() ? 0 : bounce.get(0).asInt();
    }

    private void setBounce(Arrow arrow, int value) {
        arrow.setMetadata("bounce", new FixedMetadataValue(Main.getPlugin(), value));
    }

    @Override
    public void useUltimate(Player player) {
        getSecondTalent().cancelTask(player);
        ultimateArrows.put(player, maxUltimateArrows);
    }

    @Override
    public void onDeath(Player player) {
        ultimateArrows.remove(player);
    }

    @Override
    public void onUltimateEnd(Player player) {
        ultimateArrows.remove(player);
    }

    @Override
    public void onStart(Player player) {
        player.getInventory().setItem(9, new ItemStack(Material.ARROW));
    }

    @Override
    public void onStart() {
        new GameTask() {
            @Override
            public void run() {
                // Draw particle for Elusive Burst
                passiveArrows.forEach(arrow -> PlayerLib.spawnParticle(arrow.getLocation(), Particle.TOTEM, 3, 0, 0, 0, 0));

                // Display the amount of arrows left.
                ultimateArrows.forEach((player, arrows) -> {
                    if (arrows <= 0) {
                        return;
                    }

                    Chat.sendTitle(player, "", "&a🏹".repeat(arrows) + "&7🏹".repeat(maxUltimateArrows - arrows), 0, 10, 0);
                });
            }
        }.runTaskTimer(0, 1);
    }

    @Override
    public void onStop() {
        passiveArrows.forEach(Entity::remove);
        passiveArrows.clear();
        ultimateArrows.clear();
    }

    @EventHandler()
    public void handleProjectileLaunch(ProjectileLaunchEvent ev) {
        if (ev.getEntity() instanceof Arrow arrow && arrow.getShooter() instanceof Player player) {
            if (!validatePlayer(player)) {
                return;
            }

            if (isUsingUltimate(player) || (player.isSneaking() && arrow.isCritical())) {
                passiveArrows.add(arrow);
            }
        }
    }

    @EventHandler()
    public void handleProjectileHit(ProjectileHitEvent ev) {
        if (ev.getEntity() instanceof Arrow arrow && arrow.getShooter() instanceof Player player && passiveArrows.contains(arrow)) {
            createExplosion(player, arrow.getLocation());
            passiveArrows.remove(arrow);
        }
    }

    private void createExplosion(Player player, Location location) {
        final double y = -1.5d;
        final double spread = 1.55d;
        location.add(0, 2, 0);
        spawnArrow(player, location, new Vector(-spread, y, 0));
        spawnArrow(player, location, new Vector(spread, y, 0));
        spawnArrow(player, location, new Vector(0, y, spread));
        spawnArrow(player, location, new Vector(0, y, -spread));
        spawnArrow(player, location, new Vector(spread, y, spread));
        spawnArrow(player, location, new Vector(spread, y, -spread));
        spawnArrow(player, location, new Vector(-spread, y, spread));
        spawnArrow(player, location, new Vector(-spread, y, -spread));
    }

    private void spawnArrow(Player player, Location location, Vector vector) {
        if (location.getWorld() == null || !location.getBlock().getType().isAir()) {
            return;
        }
        final Arrow arrow = location.getWorld().spawnArrow(location, vector, 1.5f, 0.25f);
        arrow.setDamage(this.getWeapon().getDamage());
        arrow.setShooter(player);
    }

    @Override
    public DamageOutput processDamageAsVictim(DamageInput input) {
        final ArrowShield shield = getFirstTalent();
        final Player player = input.getPlayer();
        if (shield.getCharges(player) > 0) {
            shield.removeCharge(player);

            return DamageOutput.CANCEL;
        }
        return null;
    }

    @Override
    public ArrowShield getFirstTalent() {
        return (ArrowShield) Talents.ARROW_SHIELD.getTalent();
    }

    @Override
    public Climb getSecondTalent() {
        return (Climb) Talents.CLIMB.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.ELUSIVE_BURST.getTalent();
    }
}

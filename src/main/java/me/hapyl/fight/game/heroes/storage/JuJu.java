package me.hapyl.fight.game.heroes.storage;

import me.hapyl.fight.event.DamageInput;
import me.hapyl.fight.event.DamageOutput;
import me.hapyl.fight.game.heroes.ClassEquipment;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.Role;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.storage.juju.ArrowShield;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class JuJu extends Hero implements Listener {

    private final Set<Arrow> arrows = new HashSet<>();

    public JuJu() {
        super("JuJu the Bandit");

        setRole(Role.RANGE);
        setMinimumLevel(5);

        setInfo("A bandit from the depths of the jungle. Highly skilled in range combat.");
        setItem("9dcff46588f394987979b7dd770adea94d8ee1fb1f7b8704e1baf91227f6a4d");

        final ClassEquipment equipment = getEquipment();
        equipment.setChestplate(62, 51, 40);
        equipment.setLeggings(62, 51, 40);
        equipment.setBoots(16, 13, 10);

        setWeapon(new Weapon(Material.BOW)
                .setName("Twisted")
                .setDescription("A bow made of anything you can find in the middle of the jungle.")
                .setDamage(4.0d));

        setUltimate(new UltimateTalent(
                "Trick Shot",
                "Your arrows will ricochet off walls and other surfaces for {duration}, giving you the ability to hit targets that are out of sight or hiding behind cover.__The damage is increased with each bounce and speed of ricochet arrows is based on your charge time.",
                65
        ).setDurationSec(8).setItem(Material.ARROW));
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntityType() == EntityType.ARROW && event.getEntity().getShooter() instanceof Player player) {
            final Arrow arrow = (Arrow) event.getEntity();
            final BlockFace hitBlockFace = event.getHitBlockFace();

            if (!validatePlayer(player, Heroes.JUJU) || !isUsingUltimate(player) || hitBlockFace == null) {
                return;
            }

            // Calculate the angle of reflection for the arrow.
            final Vector arrowVelocity = arrow.getVelocity();
            final Vector surfaceNormal = hitBlockFace.getDirection();
            final Vector reflectedVelocity = arrowVelocity.subtract(surfaceNormal.multiply(1.5d * arrowVelocity.dot(surfaceNormal)));

            // Set the new velocity of the arrow to the reflected velocity.
            Entities.ARROW.spawn(arrow.getLocation(), self -> {
                self.setShooter(player);
                self.setDamage(arrow.getDamage() * 2);
                self.setColor(Color.GREEN);
                self.setCritical(arrow.isCritical());
                self.setVelocity(reflectedVelocity);
            });
        }
    }

    @Override
    public void useUltimate(Player player) {
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
                if (arrows.isEmpty()) {
                    return;
                }

                arrows.forEach(arrow -> PlayerLib.spawnParticle(arrow.getLocation(), Particle.TOTEM, 3, 0, 0, 0, 0));
            }
        }.runTaskTimer(0, 1);
    }

    @Override
    public void onStop() {
        arrows.forEach(Entity::remove);
        arrows.clear();
    }

    @EventHandler()
    public void handleProjectileLaunch(ProjectileLaunchEvent ev) {
        if (ev.getEntity() instanceof Arrow arrow && arrow.getShooter() instanceof Player player) {
            if (validatePlayer(player, Heroes.JUJU) && player.isSneaking() && arrow.isCritical() && !isUsingUltimate(player)) {
                arrows.add(arrow);
            }
        }
    }

    @EventHandler()
    public void handleProjectileHit(ProjectileHitEvent ev) {
        if (ev.getEntity() instanceof Arrow arrow && arrow.getShooter() instanceof Player player && arrows.contains(arrow)) {
            createExplosion(player, arrow.getLocation());
            arrows.remove(arrow);
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
        final ArrowShield shield = (ArrowShield) getFirstTalent();
        final Player player = input.getPlayer();
        if (shield.getCharges(player) > 0) {
            shield.removeCharge(player);

            return DamageOutput.CANCEL;
        }
        return null;
    }

    @Override
    public Talent getFirstTalent() {
        return Talents.ARROW_SHIELD.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return Talents.CLIMB.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.ELUSIVE_BURST.getTalent();
    }
}

package me.hapyl.fight.game.heroes.archive.archer;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroEquipment;
import me.hapyl.fight.game.heroes.Role;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.ItemStacks;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.ThreadRandom;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class Archer extends Hero implements Listener {

    private final Set<Arrow> boomArrows = new HashSet<>();
    private final Weapon boomBow = new Weapon(Material.BOW).setDamage(1.0d).setName("&6&lBOOM BOW");

    private final double explosionRadius = 5.0d;
    private final double explosionDamage = 30.0d;
    private final int boomBowPerShotCd = 10;

    public Archer() {
        super("Archer");

        setRole(Role.RANGE);
        setArchetype(Archetype.RANGE);

        setDescription("One of the best archers joins the fight! Not alone though but with his &3custom-made &8&obow.");
        setItem("106c16817c73ff64a4a49b590d2cdb25bcfa52c630fe7281a177eabacdaa857b");

        setWeapon(Material.BOW, "Bow of Destiny", "A custom-made bow with some unique abilities!", 5.0d);

        final HeroAttributes attributes = getAttributes();
        attributes.setValue(AttributeType.HEALTH, 125.0d);
        attributes.setValue(AttributeType.SPEED, 0.225d);
        attributes.setValue(AttributeType.DEFENSE, 0.8d);

        final HeroEquipment equipment = getEquipment();
        equipment.setChestplate(86, 86, 87);
        equipment.setLeggings(75, 75, 87);
        equipment.setBoots(51, 51, 51);

        setUltimate(new UltimateTalent(
                "Boom Bow",
                "Equip a &6&lBOOM BOW &7for {duration} that fires explosive arrows that explode on impact dealing with massive damage.",
                50
        ).setItem(Material.BLAZE_POWDER)
                .setDuration(120)
                .setCooldownSec(20)
                .setSound(Sound.ITEM_CROSSBOW_SHOOT, 0.25f));

        getUltimate().addAttributeDescription("Explosion Radius", explosionRadius + " blocks");
        getUltimate().addAttributeDescription("Explosion Damage", explosionDamage);
    }

    @Override
    public void useUltimate(Player player) {
        final PlayerInventory inventory = player.getInventory();
        inventory.setItem(4, boomBow.getItem());
        inventory.setHeldItemSlot(4);

        GamePlayer.setCooldown(player, boomBow.getMaterial(), boomBowPerShotCd);

        GameTask.runLater(() -> {
            inventory.setItem(4, ItemStacks.AIR);
            inventory.setHeldItemSlot(0);
        }, getUltimateDuration());
    }

    @Override
    public void onStart() {
        new GameTask() {
            @Override
            public void run() {
                boomArrows.forEach(arrow -> {
                    if (arrow.isDead()) {
                        return;
                    }
                    PlayerLib.spawnParticle(arrow.getLocation(), Particle.FLAME, 2, 0, 0, 0, 0.015f);
                });
            }
        }.runTaskTimer(0, 2);
    }

    @Override
    public void onStop() {
        Utils.clearCollection(boomArrows);
    }

    @EventHandler()
    public void handleProjectileHitEvent(ProjectileHitEvent ev) {
        if (ev.getEntity() instanceof Arrow arrow && boomArrows.contains(arrow)) {
            final ProjectileSource shooter = arrow.getShooter();

            if (shooter instanceof Player player) {
                Utils.createExplosion(
                        arrow.getLocation(),
                        explosionRadius,
                        explosionDamage,
                        player,
                        EnumDamageCause.BOOM_BOW_ULTIMATE,
                        null
                );
            }
        }
    }

    @EventHandler()
    public void handleProjectileLaunchEvent(ProjectileLaunchEvent ev) {
        if (ev.getEntity() instanceof Arrow arrow && arrow.getShooter() instanceof Player player) {
            final int selectedSlot = player.getInventory().getHeldItemSlot();

            // Handle ultimate arrows
            if (isUsingUltimate(player) && selectedSlot == 4) {
                boomArrows.add(arrow);
                GamePlayer.setCooldown(player, boomBow.getMaterial(), boomBowPerShotCd);
                return;
            }

            // Handle hawkeye arrows
            if (validatePlayer(player) && selectedSlot == 0 && arrow.isCritical() && player.isSneaking()) {
                if (!ThreadRandom.nextFloatAndCheckBetween(0.75f, 1.0f)) {
                    return;
                }
                PlayerLib.playSound(player, Sound.ENCHANT_THORNS_HIT, 2.0f);

                new GameTask() {
                    @Override
                    public void run() {
                        if (arrow.isDead()) {
                            this.cancel();
                            return;
                        }

                        PlayerLib.spawnParticle(arrow.getLocation(), Particle.CRIT_MAGIC, 1, 0, 0, 0, 0);
                        final Entity target = findNearestTarget(player, arrow.getLocation());

                        if (target == null) {
                            return;
                        }

                        final Vector vector = target.getLocation()
                                .clone()
                                .add(0d, 0.5d, 0.0d)
                                .toVector()
                                .subtract(arrow.getLocation().toVector())
                                .normalize()
                                .multiply(0.7);
                        arrow.setVelocity(vector);
                    }
                }.runTaskTimer(0, 1);
            }
        }
    }

    private Entity findNearestTarget(Player shooter, Location location) {
        final GameEntity gameEntity = Collect.nearestEntity(location, 3.0d, shooter);
        return gameEntity == null ? null : gameEntity.getEntity();
    }

    @Override
    public Talent getFirstTalent() {
        return Talents.TRIPLE_SHOT.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return Talents.SHOCK_DARK.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.HAWKEYE_ARROW.getTalent();
    }

}

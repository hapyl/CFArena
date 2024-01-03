package me.hapyl.fight.game.heroes.archive.archer;

import me.hapyl.fight.CF;
import me.hapyl.fight.event.custom.ProjectilePostLaunchEvent;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.UltimateCallback;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.loadout.HotbarSlots;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Collect;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.ThreadRandom;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public class Archer extends Hero implements Listener {

    private final Set<Arrow> boomArrows = new HashSet<>();
    private final Weapon boomBow = new Weapon(Material.BOW).setDamage(1.0d).setName("&6&lBOOM BOW");

    private final double explosionRadius = 5.0d;
    private final double explosionDamage = 30.0d;
    private final int boomBowPerShotCd = 15;

    private final Color hawkeyeArrowColors = Color.fromRGB(19, 81, 143);

    public Archer() {
        super("Archer");

        setArchetype(Archetype.RANGE);

        setDescription("One of the best archers joins the fight! Not alone though but with his &3custom-made &8&obow.");
        setItem("106c16817c73ff64a4a49b590d2cdb25bcfa52c630fe7281a177eabacdaa857b");

        setWeapon(Material.BOW, "Bow of Destiny", "A custom-made bow with some unique abilities!", 5.0d);

        final HeroAttributes attributes = getAttributes();
        attributes.set(AttributeType.MAX_HEALTH, 100.0d);
        attributes.set(AttributeType.SPEED, 0.23d);
        attributes.set(AttributeType.DEFENSE, 0.8d);

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(86, 86, 87);
        equipment.setLeggings(75, 75, 87);
        equipment.setBoots(51, 51, 51);

        setUltimate(new UltimateTalent(
                "Boom Bow",
                "Equip a &6&lBOOM BOW &7for {duration} that fires explosive arrows that explode on impact dealing with massive &ftrue damage&7.",
                60
        ).setItem(Material.BLAZE_POWDER)
                .setDurationSec(6)
                .setCooldownSec(20)
                .setSound(Sound.ITEM_CROSSBOW_SHOOT, 0.25f));

        getUltimate().addAttributeDescription("Explosion Radius", explosionRadius + " blocks");
        getUltimate().addAttributeDescription("Explosion Damage", explosionDamage);
    }

    @Override
    public UltimateCallback useUltimate(@Nonnull GamePlayer player) {
        player.setItemAndSnap(HotbarSlots.HERO_ITEM, boomBow.getItem());
        player.setCooldown(boomBow.getMaterial(), boomBowPerShotCd);

        GameTask.runLater(() -> {
            player.setItem(HotbarSlots.HERO_ITEM, null);
            player.snapToWeapon();
        }, getUltimateDuration());

        return UltimateCallback.OK;
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

                    final Location location = arrow.getLocation();

                    PlayerLib.spawnParticle(location, Particle.FLAME, 2, 0, 0, 0, 0.015f);
                    PlayerLib.playSound(location, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 2.0f);
                });
            }
        }.runTaskTimer(0, 2);
    }

    @Override
    public void onStop() {
        CFUtils.clearCollection(boomArrows);
    }

    @EventHandler()
    public void handleProjectileHitEvent(ProjectileHitEvent ev) {
        if (ev.getEntity() instanceof Arrow arrow && boomArrows.contains(arrow)) {
            final ProjectileSource shooter = arrow.getShooter();

            if (shooter instanceof Player player) {
                final GamePlayer gamePlayer = CF.getPlayer(player);

                if (gamePlayer == null) {
                    return;
                }

                gamePlayer.createExplosion(arrow.getLocation(), explosionRadius, explosionDamage, EnumDamageCause.BOOM_BOW);
            }
        }
    }

    @EventHandler()
    public void handleProjectileLaunchEvent(ProjectilePostLaunchEvent ev) {
        if (ev.getProjectile() instanceof Arrow arrow && ev.getShooter() instanceof GamePlayer player) {
            // Handle ultimate arrows
            final Color color = arrow.getColor();

            if (isUsingUltimate(player) && color == null) {
                boomArrows.add(arrow);

                player.setCooldown(boomBow.getMaterial(), boomBowPerShotCd);
                return;
            }

            // Handle hawkeye arrows
            if (!validatePlayer(player) || !player.isHeldSlot(HotbarSlots.WEAPON) || !arrow.isCritical() || !player.isSneaking()) {
                return;
            }

            if (!ThreadRandom.nextFloatAndCheckBetween(0.75f, 1.0f)) {
                return;
            }

            arrow.setColor(hawkeyeArrowColors);

            new GameTask() {
                @Override
                public void run() {
                    if (arrow.isDead()) {
                        this.cancel();
                        return;
                    }

                    player.spawnWorldParticle(arrow.getLocation(), Particle.CRIT_MAGIC, 5, 0, 0, 0, 0);
                    final Entity target = findNearestTarget(player, arrow.getLocation());

                    if (target == null) {
                        return;
                    }

                    final Vector vector = target.getLocation()
                            .add(0.0d, 0.5d, 0.0d)
                            .toVector()
                            .subtract(arrow.getLocation().toVector())
                            .normalize()
                            .multiply(0.7d);
                    arrow.setVelocity(vector);
                }
            }.runTaskTimer(0, 1);

            // Fx
            player.playSound(Sound.ENCHANT_THORNS_HIT, 2.0f);
            player.playSound(Sound.ENTITY_ELDER_GUARDIAN_DEATH_LAND, 1.25f);
        }
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

    private Entity findNearestTarget(GamePlayer shooter, Location location) {
        final LivingGameEntity gameEntity = Collect.nearestEntity(location, 3.0d, shooter);
        return gameEntity == null ? null : gameEntity.getEntity();
    }

}

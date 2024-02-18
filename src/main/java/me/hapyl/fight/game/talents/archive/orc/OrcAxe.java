package me.hapyl.fight.game.talents.archive.orc;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.loadout.HotbarSlots;
import me.hapyl.fight.game.talents.InputTalent;
import me.hapyl.fight.game.task.GeometryTask;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Collect;
import me.hapyl.spigotutils.module.entity.Entities;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class OrcAxe extends InputTalent {

    public OrcAxe() {
        super("Axe");

        setDescription("Equip and prepare your axe for action.");

        leftData.setAction("Spin")
                .setDescription("""
                        Spin the axe, &cdamaging&7 and knocking enemies back.
                        """)
                .setDurationSec(3)
                .setCooldownSec(10);

        rightData.setAction("Dash")
                .setDescription("""
                        Dash forward, &cdamaging&7 the &nfirst&7 hit &cenemy.
                        """)
                .setCooldownSec(15);

        setItem(Material.NETHERITE_AXE);
    }

    @Nonnull
    @Override
    public Response onLeftClick(@Nonnull GamePlayer player) {
        final ItemStack item = player.getItem(HotbarSlots.WEAPON);

        // Don't allow spin if no axe
        if (item == null || item.getType() != Material.IRON_AXE) {
            return Response.error("The Axe is missing!");
        }

        final ArmorStand axe = Entities.ARMOR_STAND_MARKER.spawn(player.getLocation(), self -> {
            self.getLocation().setYaw(player.getLocation().getYaw()); // fix weird spawn yaw
            self.setVisible(false);
            self.setRightArmPose(new EulerAngle(Math.toRadians(170), Math.toRadians(180), Math.toRadians(90)));

            CFUtils.setEquipment(self, equipment -> {
                equipment.setItemInMainHand(new ItemStack(Material.IRON_AXE));
            });

            player.setItem(HotbarSlots.WEAPON, null);
        });

        new GeometryTask() {

            @Override
            public void onTaskStop() {
                axe.remove();
                Heroes.ORC.getHero().getWeapon().give(player);
            }

            @Override
            public void run(double theta) {
                final Location playerLocation = player.getLocation();

                offsetXZ(playerLocation, 0.5d, axe::teleport);

                offsetXZ(playerLocation, 3.0d, location -> {
                    CFUtils.lookAt(axe, location);
                    player.spawnWorldParticle(location.clone().add(0.0d, 0.75d, 0.0d), Particle.SWEEP_ATTACK, 1);

                    // Damage and KB
                    Collect.nearbyEntities(location, 1.0d, entity -> !player.isSelfOrTeammate(entity))
                            .forEach(entity -> {
                                entity.damage(15.0d, player, EnumDamageCause.CYCLING_AXE);

                                if (entity.hasEffectResistanceAndNotify(player)) {
                                    return;
                                }

                                entity.setVelocity(entity.getLocation().getDirection().normalize().multiply(-1.5d));
                            });
                });

            }
        }.properties()
                .max(leftData)
                .maxSpins(1)
                .cancelIfDead(player)
                .step(Math.PI / 16)
                .iterations(2)
                .task()
                .runTaskTimer(0, 1);

        // Fx
        final Location location = player.getLocation();
        player.playWorldSound(location, Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR, 0.75f);
        player.playWorldSound(location, Sound.ENTITY_EVOKER_CAST_SPELL, 1.25f);

        return Response.OK;
    }

    @Nonnull
    @Override
    public Response onRightClick(@Nonnull GamePlayer player) {
        final Location startLocation = player.getLocation();
        final Vector vector = startLocation.getDirection();

        vector.setY(0.0d);
        vector.normalize();

        new TickingGameTask() {

            @Override
            public void run(int tick) {
                final Location location = player.getLocation();
                final Location eyeLocation = player.getEyeLocation();

                if (tick >= 25 || startLocation.distance(location) >= 6.0d) {
                    executeHit(eyeLocation);
                    cancel();
                    return;
                }

                final LivingGameEntity hitEntity = Collect.nearestEntity(location, 1.0d, living -> !player.isSelfOrTeammate(living));

                if (hitEntity != null) {
                    executeHit(hitEntity.getEyeLocation());
                    cancel();
                    return;
                }

                // Travel
                player.setVelocity(player.getLocation().getDirection().normalize().setY(-1.0d).multiply(0.75d));

                // Fx
                if (tick % 5 == 0) {
                    player.spawnWorldParticle(eyeLocation, Particle.SWEEP_ATTACK, 1, 0.1d, 0.1d, 0.1d, 1);
                    player.spawnWorldParticle(eyeLocation, Particle.LAVA, 1, 0.1d, 0.1d, 0.1d, 1);
                }
            }

            private void executeHit(@Nonnull Location location) {
                CF.damageAoE(location, 2.5d, 10.0d, player, EnumDamageCause.ORC_DASH, living -> !living.equals(player));

                // Fx
                player.spawnWorldParticle(location, Particle.SWEEP_ATTACK, 1, 0.1d, 0.1d, 0.1d, 10);
                player.playWorldSound(location, Sound.BLOCK_ANVIL_HIT, 0.75f);
            }
        }.runTaskTimer(0, 1);

        // Fx
        player.playWorldSound(startLocation, Sound.ENTITY_CAMEL_DASH, 0.75f);
        player.playWorldSound(startLocation, Sound.ENTITY_CAMEL_DASH_READY, 0.0f);

        return Response.OK;
    }
}

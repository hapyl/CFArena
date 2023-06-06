package me.hapyl.fight.game.talents.archive.orc;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.damage.EntityData;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.talents.InputTalent;
import me.hapyl.fight.game.task.GeometryTask;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.ItemStacks;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class OrcAxe extends InputTalent {

    public OrcAxe() {
        super("Axe");

        setDescription("Equip and prepare your axe for action.");

        leftData.setAction("Spin")
                .setDescription("""
                        Summon the axe that spins around you for {duration}.
                        """)
                .setDurationSec(3)
                .setCooldownSec(10);

        rightData.setAction("Dash")
                .setDescription("""
                        Dash forward up to 6 blocks, damaging the first enemy hit.
                        """)
                .setCooldownSec(15);

        setItem(Material.NETHERITE_AXE);
    }

    @Nonnull
    @Override
    public Response onLeftClick(Player player) {
        // Don't allow spin if no axe
        final PlayerInventory inventory = player.getInventory();
        final ItemStack item = inventory.getItem(0);

        if (item == null || item.getType() != Material.IRON_AXE) {
            return Response.error("The Axe is missing!");
        }

        final ArmorStand axe = Entities.ARMOR_STAND_MARKER.spawn(player.getLocation(), self -> {
            Utils.setEquipment(self, equipment -> {
                self.setVisible(false);
                self.setRightArmPose(new EulerAngle(Math.toRadians(90), 0.0d, Math.toRadians(90)));

                equipment.setItemInMainHand(new ItemStack(Material.IRON_AXE));
            });

            inventory.setItem(0, ItemStacks.AIR);
        });

        new GeometryTask() {
            @Override
            public void run(double theta) {
                final Location playerLocation = player.getLocation();

                offsetXZ(playerLocation, 2.0d, location -> {
                    // Damage
                    Utils.getEntitiesInRange(location, 1.0d, living -> living != player)
                            .forEach(entity -> {
                                GamePlayer.damageEntity(entity, 5.0d, player, EnumDamageCause.CYCLING_AXE);
                            });

                    axe.teleport(location);
                });
            }

            @Override
            public void onStop() {
                axe.remove();
                Heroes.ORC.getHero().getWeapon().giveWeapon(player);
            }
        }.properties()
                .max(leftData)
                .step(Math.PI / 12)
                .cancelIfDead(player)
                .task()
                .runTaskTimer(0, 1);

        return Response.OK;
    }

    @Nonnull
    @Override
    public Response onRightClick(Player player) {
        final Location startLocation = player.getLocation();
        final Vector vector = startLocation.getDirection();

        vector.setY(0.0d);
        vector.normalize();

        new GameTask() {

            @Override
            public void run() {
                final Location location = player.getLocation();

                if (startLocation.distance(location) >= 6.0d) {
                    executeHit(location);
                    cancel();
                    return;
                }

                final LivingEntity hitEntity = Utils.getNearestLivingEntity(location, 0.5d, living -> living != player);

                if (hitEntity != null) {
                    executeHit(hitEntity.getLocation());
                    cancel();
                    return;
                }

                // Travel
                player.setVelocity(player.getLocation().getDirection().normalize().setY(-1.0d).multiply(0.75d));
            }

            private void executeHit(@Nonnull Location location) {
                EntityData.damageAoE(location, 1.0d, 10.0d, player, EnumDamageCause.ORC_DASH, living -> living != player);

                // Fx
                PlayerLib.spawnParticle(location, Particle.SWEEP_ATTACK, 1, 0.1d, 0.1d, 0.1d, 10);
                PlayerLib.playSound(location, Sound.BLOCK_ANVIL_HIT, 0.75f);
            }
        }.runTaskTimer(0, 1);

        return Response.OK;
    }
}

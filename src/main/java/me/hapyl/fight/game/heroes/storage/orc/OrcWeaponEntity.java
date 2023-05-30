package me.hapyl.fight.game.heroes.storage.orc;

import me.hapyl.fight.game.Callback;
import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.math.Tick;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public abstract class OrcWeaponEntity extends GameTask implements Callback<OrcWeaponEntity> {

    private static final int MAX_ALIVE_TICKS = Tick.fromSecond(10);

    private final Player player;
    private final ArmorStand entity;
    private final Vector vector;
    private int aliveTicks;

    public OrcWeaponEntity(Player player) {
        final Location startLocation = player.getLocation();

        vector = startLocation.getDirection();
        vector.normalize().multiply(0.75d);

        this.player = player;
        this.entity = Entities.ARMOR_STAND_MARKER.spawn(startLocation, self -> {
            self.setGravity(false);
            Utils.setEquipment(self, equipment -> {
                self.setRightArmPose(new EulerAngle(Math.toRadians(-85d), 0.0d, 0.0d));
                equipment.setItemInMainHand(new ItemStack(Material.IRON_AXE));
            });
        });

        runTaskTimer(0, 1);
    }

    private boolean next() {
        // Check for entity or block hit
        final Location location = entity.getLocation();
        final Location nextLocation = location.add(vector);
        final Location hitLocation = nextLocation.clone().add(0.0d, 1.5d, 0.0d);

        // Check for hit block
        if (!hitLocation.getBlock().getType().isAir()) {
            // Stay in block for 1 second and return back
            cancel();
            Debug.info("hit block");
            GameTask.runLater(this::returnToSender, 20);
            return true;
        }

        // Check for entity hit
        final LivingEntity nearestEntity = Utils.getNearestLivingEntity(hitLocation, 1.0d, predicate -> predicate != player);

        if (nearestEntity != null) {
            Debug.info("hit " + nearestEntity);
            cancel();
            returnToSender();
            return true;
        }

        // Travel
        entity.teleport(nextLocation);
        entity.setRightArmPose(entity.getRightArmPose().add(Math.toRadians(15.0d), 0.0d, 0.0d));
        return false;
    }

    @Override
    public void run() {
        if (aliveTicks++ >= MAX_ALIVE_TICKS) {
            cancel();
            returnToSender();
            Debug.info("max alive ticks reached");
            return;
        }

        // I know it's ugly, but whatever
        for (int i = 0; i < 2; i++) {
            if (next()) {
                break;
            }
        }
    }

    public void returnToSender() {
        Debug.info("returning");

        entity.setRightArmPose(new EulerAngle(Math.toRadians(18.0d), Math.toRadians(174.0d), 0.0d));

        new GameTask() {
            @Override
            public void run() {
                final Location playerLocation = player.getLocation();
                Utils.lookAt(entity, playerLocation);

                final Location location = entity.getLocation();
                final Vector vector = location.getDirection().normalize().multiply(0.75d);

                // Check for distance to player
                if (location.distance(playerLocation) <= 1.0d) {
                    callback(OrcWeaponEntity.this);
                    cancel();
                    return;
                }

                entity.teleport(location.add(vector));
            }
        }.runTaskTimer(0, 1);
    }

    public void remove() {
        entity.remove();
        aliveTicks = -1;
    }

}

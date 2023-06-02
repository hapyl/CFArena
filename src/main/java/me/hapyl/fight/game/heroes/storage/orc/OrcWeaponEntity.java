package me.hapyl.fight.game.heroes.storage.orc;

import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.locaiton.LocationHelper;
import me.hapyl.spigotutils.module.math.Tick;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public abstract class OrcWeaponEntity extends GameTask {

    private static final int MAX_ALIVE_TICKS = Tick.fromSecond(10);

    private final double FLIGHT_SPEED = 0.75d;
    private final BlockData[] BLOCK_DATA = {
            Material.WHITE_CONCRETE_POWDER.createBlockData(),
            Material.LIGHT_BLUE_CONCRETE_POWDER.createBlockData()
    };

    private final Player player;
    private final ArmorStand entity;
    private final Vector vector;
    private int aliveTicks;

    private LivingEntity hitEntity;

    public OrcWeaponEntity(Player player) {
        final Location startLocation = LocationHelper.getToTheLeft(player.getLocation(), 0.25d);

        vector = startLocation.getDirection();
        vector.normalize().multiply(FLIGHT_SPEED);

        this.player = player;
        this.entity = Entities.ARMOR_STAND_MARKER.spawn(startLocation, self -> {
            self.setGravity(false);
            self.setInvisible(true);
            self.setSilent(true);

            Utils.setEquipment(self, equipment -> {
                self.setRightArmPose(new EulerAngle(Math.toRadians(-85d), 0.0d, 0.0d));
                equipment.setItemInMainHand(new ItemStack(Material.IRON_AXE));
            });
        });

        runTaskTimer(0, 1);
    }

    public void onHit(@Nonnull LivingEntity entity) {
    }

    public void onHit(@Nonnull Block block) {
    }

    public void onReturn(@Nonnull Player player) {
    }

    @Override
    public void run() {
        // Stuck in entity for 30 ticks
        final Location entityLocation = entity.getLocation();

        if (hitEntity != null) {
            if (aliveTicks++ < 30) {
                final Location location = hitEntity.getLocation();

                // Preserve axe's yaw and pitch
                location.setYaw(entityLocation.getYaw());
                location.setPitch(entityLocation.getPitch());

                entity.teleport(location);
            }
            else {
                cancel();
                returnToSender();
            }
            return;
        }

        if (aliveTicks++ >= MAX_ALIVE_TICKS) {
            cancel();
            returnToSender();
            return;
        }

        // I know it's ugly, but whatever
        for (int i = 0; i < 2; i++) {
            if (next()) {
                return;
            }
        }

        // Fx
        if (aliveTicks % 12 == 0) {
            PlayerLib.playSound(entityLocation, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.8f);
        }

        final Location axeLocation = getAxeLocation();
        final World world = entity.getWorld();

        // Shift then location of the particle depending on the current value of the arm
        final double armPosX = entity.getRightArmPose().getX();

        if (armPosX < Math.PI) {
            axeLocation.subtract(0.0d, 0.15d, 0.0d);
        }

        world.spawnParticle(Particle.FALLING_DUST, axeLocation, 1, 0.1d, 0.1d, 0.1d, 1, BLOCK_DATA[0]);
        world.spawnParticle(Particle.FALLING_DUST, axeLocation, 1, 0.1d, 0.1d, 0.1d, 1, BLOCK_DATA[1]);
    }

    public Location getAxeLocation() {
        return LocationHelper.getToTheRight(entity.getLocation(), 0.25d).add(0.0d, 1.5d, 0.0d);
    }

    public void returnToSender(int delay) {
        GameTask.runLater(this::returnToSender, delay);
    }

    private boolean next() {
        // Check for entity or block hit
        final Location location = entity.getLocation();
        final Location nextLocation = location.add(vector);
        final Location hitLocation = LocationHelper.getToTheRight(location, 0.25d).add(0.0d, 1.5d, 0.0d);

        // Check for hit block
        final Block hitBlock = hitLocation.getBlock();

        // Stay in block for 1 second and return back
        if (hitBlock.getType().isOccluding()) {
            cancel();
            onHit(hitBlock);
            returnToSender(60);
            return true;
        }

        // Check for entity hit
        final LivingEntity nearestEntity = Utils.getNearestLivingEntity(hitLocation, 0.1d, predicate -> predicate != player);

        if (nearestEntity != null) {
            hitEntity = nearestEntity;
            aliveTicks = 0;

            onHit(nearestEntity);
            return true;
        }

        // Travel
        entity.teleport(nextLocation);
        entity.setRightArmPose(entity.getRightArmPose().add(Math.toRadians(15.0d), 0.0d, 0.0d));
        return false;
    }

    public void returnToSender() {
        entity.setRightArmPose(new EulerAngle(Math.toRadians(18.0d), Math.toRadians(174.0d), 0.0d));

        new GameTask() {
            @Override
            public void run() {
                final Location playerLocation = player.getLocation();
                Utils.lookAt(entity, playerLocation);

                final Location location = entity.getLocation();
                final Vector vector = location.getDirection().normalize().multiply(FLIGHT_SPEED * 2);

                // Check for distance to player
                if (location.distance(playerLocation) <= 0.75d) {
                    remove();
                    cancel();
                    onReturn(player);
                    return;
                }

                entity.teleport(location.add(vector));
            }
        }.runTaskTimer(0, 1);
    }

    public void remove() {
        cancelIfActive();
        entity.remove();
        aliveTicks = -1;
    }

}

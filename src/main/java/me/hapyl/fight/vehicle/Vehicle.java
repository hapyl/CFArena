package me.hapyl.fight.vehicle;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.eterna.module.util.Vectors;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.Event;
import me.hapyl.fight.game.talents.Removable;
import me.hapyl.fight.util.ImmutableList;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class Vehicle implements Removable {

    protected final Player passenger;
    protected final Entity vehicle;

    protected double speed;
    protected double smoothness;
    protected double maxHeight;

    protected Vehicle(@Nonnull Player passenger) {
        this.passenger = passenger;
        this.vehicle = Entities.ARMOR_STAND.spawn(passenger.getLocation(), self -> {
            self.setInvisible(true);
            self.setSilent(true);
            self.setSmall(true);
            self.setInvulnerable(true);
        });

        // Make sure to ride
        this.vehicle.addPassenger(passenger);

        // Properties
        this.speed = 1.0d;
        this.smoothness = 0.5d;
        this.maxHeight = 255.0d;
    }

    @Event
    public void onMove() {
    }

    public void move(@Nonnull ImmutableList<VehicleDirection> directions) {
        // Make sure the player is still riding the vehicle
        if (!vehicle.getPassengers().contains(passenger)) {
            vehicle.addPassenger(passenger);
        }

        final Vector direction = passenger.getLocation().getDirection();
        direction.setY(0.0d).normalize();

        final Vector rightVector = direction.clone().crossProduct(Vectors.UP).normalize();
        final Vector vector = new Vector(0, 0, 0);

        directions.apply(vector)
                .when(VehicleDirection.FORWARD, vec -> vector.add(direction))
                .when(VehicleDirection.BACKWARDS, v -> v.subtract(direction))

                .when(VehicleDirection.LEFT, v -> v.subtract(rightVector))
                .when(VehicleDirection.RIGHT, v -> v.add(rightVector))

                .when(VehicleDirection.UP, v -> v.setY(1))
                .when(VehicleDirection.DOWN, v -> v.setY(-1));

        if (vector.lengthSquared() > 0) {
            vector.normalize().multiply(speed);
        }

        // Height
        final Location location = vehicle.getLocation();

        double distanceToGround = 0.0d;
        Block block = location.getBlock();

        while (block.isEmpty() && block.getLocation().getY() > location.getWorld().getMinHeight()) {
            block = block.getRelative(BlockFace.DOWN);
            distanceToGround++;
        }

        if (distanceToGround >= maxHeight) {
            vector.setY(-BukkitUtils.GRAVITY);
        }

        final Vector velocity = vehicle.getVelocity();
        final Vector interpolatedVelocity = velocity.clone().add(vector.subtract(velocity).multiply(smoothness));

        vehicle.setVelocity(interpolatedVelocity);
        onMove();
    }

    @Nonnull
    public Player getPassenger() {
        return passenger;
    }

    public void dismount() {
        CF.getVehicleManager().stopRiding(getPassenger());
    }

    @Override
    public void remove() {
        // Remove the entity
        vehicle.remove();
    }
}

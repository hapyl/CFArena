package me.hapyl.fight.vehicle.vehicles;

import me.hapyl.fight.vehicle.Vehicle;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class TestVehicle extends Vehicle {
    public TestVehicle(@Nonnull Player passenger) {
        super(passenger);

        this.speed.set(0.5d);
        this.smoothness.set(0.2d);
        this.maxHeight.set(10.0d);
    }
}

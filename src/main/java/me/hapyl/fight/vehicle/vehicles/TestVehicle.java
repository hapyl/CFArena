package me.hapyl.fight.vehicle.vehicles;

import me.hapyl.fight.vehicle.Vehicle;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class TestVehicle extends Vehicle {
    public TestVehicle(@Nonnull Player passenger) {
        super(passenger);

        this.speed = 0.5d;
        this.smoothness = 0.2d;
        this.maxHeight = 10.0d;
    }
}

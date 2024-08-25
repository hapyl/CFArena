package me.hapyl.fight.game.heroes.aurora;

import me.hapyl.fight.vehicle.Vehicle;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class AuroraVehicle extends Vehicle {
    protected AuroraVehicle(@Nonnull Player passenger) {
        super(passenger);

        this.maxHeight.set(12.0d);
        this.speed.set(0.75d);
        this.smoothness.set(0.4d);
    }
}

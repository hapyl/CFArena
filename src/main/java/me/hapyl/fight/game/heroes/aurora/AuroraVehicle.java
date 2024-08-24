package me.hapyl.fight.game.heroes.aurora;

import me.hapyl.fight.vehicle.Vehicle;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class AuroraVehicle extends Vehicle {
    protected AuroraVehicle(@Nonnull Player passenger) {
        super(passenger);

        this.maxHeight = 12d;
        this.speed = 0.75d;
        this.smoothness = 0.4d;
    }
}

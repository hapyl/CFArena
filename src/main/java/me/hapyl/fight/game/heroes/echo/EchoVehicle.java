package me.hapyl.fight.game.heroes.echo;

import me.hapyl.fight.vehicle.Vehicle;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class EchoVehicle extends Vehicle {
    protected EchoVehicle(@Nonnull Player passenger) {
        super(passenger);

        this.speed.set(0.5d);
        this.maxHeight.set(10.0d);
        this.smoothness.set(0.4d);
    }
}

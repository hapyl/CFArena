package me.hapyl.fight.vehicle;

import me.hapyl.fight.vehicle.vehicles.TestVehicle;
import org.bukkit.entity.Player;

import java.util.function.Function;

public enum VehicleType {

    TEST(TestVehicle::new),
    ;

    public final Function<Player, Vehicle> function;

    VehicleType(Function<Player, Vehicle> function) {
        this.function = function;
    }
}

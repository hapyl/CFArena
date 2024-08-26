package me.hapyl.fight.vehicle;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.util.DependencyInjector;
import me.hapyl.fight.Main;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Function;

public class VehicleManager extends DependencyInjector<Main> {

    private final Map<Player, Vehicle> playerVehicle;

    public VehicleManager(Main plugin) {
        super(plugin);

        this.playerVehicle = Maps.newHashMap();
    }

    @Nonnull
    public <V extends Vehicle> V startRiding(@Nonnull Player player, @Nonnull Function<Player, V> function) {
        stopRiding(player);

        final V vehicle = function.apply(player);

        playerVehicle.put(player, vehicle);
        return vehicle;
    }

    public boolean stopRiding(@Nonnull Player player) {
        final Vehicle vehicle = playerVehicle.remove(player);

        if (vehicle != null) {
            vehicle.remove();
            return true;
        }

        return false;
    }

    public boolean stopRiding(@Nonnull Player player, @Nonnull Vehicle vehicle) {
        if (playerVehicle.get(player) != vehicle) {
            return false;
        }

        return stopRiding(player);
    }

    @Nullable
    public Vehicle getVehicle(@Nonnull Player player) {
        return playerVehicle.get(player);
    }

    public boolean isRiding(@Nonnull Player player) {
        return playerVehicle.containsKey(player);
    }
}

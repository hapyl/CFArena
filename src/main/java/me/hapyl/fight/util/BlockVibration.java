package me.hapyl.fight.util;

import me.hapyl.eterna.module.util.BukkitUtils;
import org.bukkit.Vibration;

public class BlockVibration extends Vibration {
    // FIXME (Sun, Sep 1 2024 @xanyjl):
    public BlockVibration(
            double originX, double originY, double originZ,
            double destinationX, double destinationY, double destinationZ,
            int arrivalTime) {
        super(
                BukkitUtils.defLocation(originX, originY, originZ),
                new Destination.BlockDestination(BukkitUtils.defLocation(destinationX, destinationY, destinationZ)),
                arrivalTime
        );
    }
}

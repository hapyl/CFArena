package me.hapyl.fight.util;

import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Vibration;

public class BlockVibration extends Vibration {
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

package me.hapyl.fight.npc;

import me.hapyl.eterna.module.util.BukkitUtils;
import org.bukkit.Location;

import javax.annotation.Nonnull;

public class FlippedPersistentNPC extends PersistentNPC {
    public FlippedPersistentNPC(@Nonnull Location location) {
        super(location, "", "", fn -> "Dinnerbone");

        init();
    }

    public FlippedPersistentNPC(double x, double y, double z) {
        this(BukkitUtils.defLocation(x, y, z));
    }
}

package me.hapyl.fight.game.maps.maps.moon;

import me.hapyl.spigotutils.module.math.Cuboid;
import org.bukkit.Chunk;
import org.bukkit.Location;

import javax.annotation.Nonnull;

public class MoonRoomBB extends Cuboid {
    public MoonRoomBB(double x, double y, double z, double x2, double y2, double z2) {
        super(x, y, z, x2, y2, z2);
    }

    @Override
    public void cloneBlocksTo(@Nonnull Location location, boolean skipAir) {
        final Chunk chunk = location.getChunk();

        if (!chunk.isLoaded()) {
            chunk.load();
            chunk.setForceLoaded(true);
        }

        super.cloneBlocksTo(location, skipAir);
    }
}

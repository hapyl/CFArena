package me.hapyl.fight.game.maps.maps.moon;

import me.hapyl.eterna.module.math.Cuboid;
import me.hapyl.eterna.module.util.BukkitUtils;
import org.bukkit.Location;

public class MoonRoomExit extends MoonRoom {

    private final Cuboid bbGlassOpen = new MoonRoomBB(5469, 63, 26, 5480, 65, 26);
    private final Cuboid bbGlassClosed = new MoonRoomBB(5469, 67, 26, 5480, 69, 26);

    private final Cuboid bbDoorOpen = new MoonRoomBB(5473, 61, 32, 5477, 65, 32);
    private final Cuboid bbDoorClosed = new MoonRoomBB(5479, 61, 32, 5483, 65, 32);

    private final Location glassLocation = BukkitUtils.defLocation(5493, 71, 20);
    private final Location doorLocation = BukkitUtils.defLocation(5507, 62, 20);

    public MoonRoomExit() {
        super(MoonBase.GATE_EXIT_ROOM);
    }

    @Override
    public void open() {
        bbDoorOpen.cloneBlocksTo(doorLocation, false);
        bbGlassOpen.cloneBlocksTo(glassLocation, false);
    }

    @Override
    public void close() {
        bbDoorClosed.cloneBlocksTo(doorLocation, false);
        bbGlassClosed.cloneBlocksTo(glassLocation, false);
    }

}

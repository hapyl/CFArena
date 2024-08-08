package me.hapyl.fight.game.maps.maps.moon;

import me.hapyl.eterna.module.math.Cuboid;
import me.hapyl.eterna.module.util.BukkitUtils;
import org.bukkit.Location;

public class MoonRoomWater extends MoonRoom {

    private final Cuboid bbDoorOpen = new MoonRoomBB(5479, 67, 18, 5479, 71, 22);
    private final Cuboid bbDoorClosed = new MoonRoomBB(5479, 61, 18, 5479, 65, 22);

    private final Location doorLocation = BukkitUtils.defLocation(5480, 62, -13);

    public MoonRoomWater() {
        super(MoonBase.GATE_WATER_ROOM);
    }

    @Override
    public void open() {
        bbDoorOpen.cloneBlocksTo(doorLocation, false);
    }

    @Override
    public void close() {
        bbDoorClosed.cloneBlocksTo(doorLocation, false);
    }
}

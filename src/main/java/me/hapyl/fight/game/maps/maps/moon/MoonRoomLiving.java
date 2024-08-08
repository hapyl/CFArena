package me.hapyl.fight.game.maps.maps.moon;

import me.hapyl.eterna.module.math.Cuboid;
import me.hapyl.eterna.module.util.BukkitUtils;
import org.bukkit.Location;

public class MoonRoomLiving extends MoonRoom {

    private final Cuboid bbDoorOpen = new MoonRoomBB(5470, 61, 38, 5474, 65, 38);
    private final Cuboid bbDoorClosed = new MoonRoomBB(5476, 61, 38, 5480, 65, 38);

    private final Location doorLocation = BukkitUtils.defLocation(5500, 62, -20);

    public MoonRoomLiving() {
        super(MoonBase.GATE_LIVING_ROOM);
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

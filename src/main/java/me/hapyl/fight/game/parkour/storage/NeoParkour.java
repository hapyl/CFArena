package me.hapyl.fight.game.parkour.storage;

import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.fight.game.parkour.CFParkour;
import me.hapyl.fight.game.parkour.ParkourLeaderboard;

public class NeoParkour extends CFParkour {
    public NeoParkour() {
        super("Neo Parkour", 26, 65, -9, -180.0f, 0.0f, 21, 65, -19);

        addCheckpoint(26, 65, -12, 90, 0);
        addCheckpoint(22, 65, -13, 90, 0);
        addCheckpoint(18, 65, -13, 90, 0);

        setLeaderboard(new ParkourLeaderboard(this, 22.5, 66, -7.5));
        setQuitLocation(BukkitUtils.defLocation(25, 65, -6, -180, 0));
    }
}

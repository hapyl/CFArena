package me.hapyl.fight.game.parkour.storage;

import me.hapyl.fight.game.parkour.CFParkour;
import me.hapyl.fight.game.parkour.ParkourLeaderboard;
import me.hapyl.spigotutils.module.util.BukkitUtils;

public class SlimeParkour extends CFParkour {
    public SlimeParkour() {
        super("Slime Parkour",
                0, 63, 28, 0.0f, 0.0f,
                0, 65, 40
        );

        setLeaderboard(new ParkourLeaderboard(this, -2.5, 62.0, 22.5));
        setQuitLocation(BukkitUtils.defLocation(0.5, 62.0, 22.5));
    }
}

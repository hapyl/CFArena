package me.hapyl.fight.game.parkour.storage;

import me.hapyl.fight.game.parkour.CFParkour;
import me.hapyl.fight.game.parkour.ParkourLeaderboard;

public class TestParkour extends CFParkour {

    public TestParkour() {
        super("test", -3, 63, -11, 4, 63, -11);

        setLeaderboard(new ParkourLeaderboard(this, 1, 62, -13));
        addCheckpoint(0, 62, -8, 0, 0);
    }
}

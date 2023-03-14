package me.hapyl.fight.game.parkour;

import me.hapyl.fight.game.parkour.storage.LobbyParkour;

public enum ParkourCourse {

    LOBBY_PARKOUR(new LobbyParkour()),
    //TEST_PARKOUR(new TestParkour()),
    ;

    private final CFParkour parkour;

    ParkourCourse(CFParkour parkour) {
        this.parkour = parkour;
    }

    public CFParkour getParkour() {
        return parkour;
    }
}

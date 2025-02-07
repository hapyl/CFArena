package me.hapyl.fight.game.parkour;

import me.hapyl.fight.game.parkour.snake.SnakeParkour;
import me.hapyl.fight.game.parkour.storage.LobbyParkour;
import me.hapyl.fight.game.parkour.storage.SlimeParkour;

public enum ParkourCourse {

    LOBBY_PARKOUR(new LobbyParkour()),
    SLIME_PARKOUR(new SlimeParkour()),
    SNAKE_PARKOUR(new SnakeParkour()),

    //NEO_PARKOUR(new NeoParkour()),
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

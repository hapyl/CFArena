package me.hapyl.fight.game.event;

public enum ServerEvents {

    APRIL_FOOLS(null),
    ;

    private final ServerEvent event;

    ServerEvents(ServerEvent event) {
        this.event = event;
    }
}

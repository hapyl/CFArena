package me.hapyl.fight.game;

import javax.annotation.Nonnull;

public enum RestartReason {

    EMERGENCY_UPDATE("Game Update", 5),
    GAME_UPDATE("Game Update", 30),
    SCHEDULED_REBOOT("Scheduled Reboot", 60);

    private final String message;
    private final int secsBeforeShutdown;

    RestartReason(String message, int secsBeforeShutdown) {
        this.message = message;
        this.secsBeforeShutdown = secsBeforeShutdown;
    }

    @Nonnull
    public String getMessage() {
        return message;
    }

    public int getSecsBeforeShutdown() {
        return secsBeforeShutdown;
    }
}
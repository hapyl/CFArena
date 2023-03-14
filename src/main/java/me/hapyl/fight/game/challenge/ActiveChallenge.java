package me.hapyl.fight.game.challenge;

import java.util.UUID;

public class ActiveChallenge {

    private final UUID uuid;
    private final Challenge challenge;
    private int progress;

    public ActiveChallenge(UUID uuid, Challenge challenge) {
        this.uuid = uuid;
        this.challenge = challenge;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public int getProgress() {
        return progress;
    }
}

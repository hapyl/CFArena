package me.hapyl.fight.game.talents;

/**
 * Represents an interface with duration.
 */
public interface Timed {

    int getDuration();

    Timed setDuration(int duration);

    default Timed setDurationSec(int durationSec) {
        return setDuration(durationSec * 20);
    }

}

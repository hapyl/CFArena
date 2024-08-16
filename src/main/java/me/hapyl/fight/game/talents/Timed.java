package me.hapyl.fight.game.talents;

/**
 * Represents an interface with duration.
 */
public interface Timed {

    int getDuration();

    Timed setDuration(int duration);

    default Timed setDurationSec(float durationSec) {
        return setDuration((int) (durationSec * 20));
    }

}

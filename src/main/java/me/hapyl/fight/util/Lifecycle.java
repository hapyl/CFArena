package me.hapyl.fight.util;

/**
 * Represents a component with a 'life' cycle that can be 'born' {@link #onStart()} and 'born' {@link #onStop()}.
 */
public interface Lifecycle {

    /**
     * The 'born' behaviour.
     * <br>
     * Called upon the start of the life cycle.
     */
    void onStart();

    /**
     * The 'die' behaviour.
     * <br>
     * Called upon the end of the life cycle.
     */
    void onStop();

}

package me.hapyl.fight.game;

/**
 * Indicates that this class a game element that process game actions.
 */
public interface GameElement {

    /**
     * Called once whenever game starts.
     */
    void onStart();

    /**
     * Called once whenever game stops.
     */
    void onStop();

    /**
     * Called once whenever players are revealed.
     */
    default void onPlayersReveal() {
    }

}

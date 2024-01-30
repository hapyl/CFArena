package me.hapyl.fight.game;

/**
 * Indicates that this class a game element that process game actions.
 * <p>
 * In truth, this system SUCKS BALLS because every element has to be called manually.
 */
public interface GameElement {

    /**
     * Called once whenever the game starts.
     */
    void onStart();

    /**
     * Called once whenever the game stops.
     */
    void onStop();

    /**
     * Called once whenever players are revealed.
     */
    default void onPlayersRevealed() {
    }

}

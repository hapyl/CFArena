package me.hapyl.fight.game;

import me.hapyl.fight.game.entity.GamePlayer;

import javax.annotation.Nonnull;

/**
 * Indicates that this class a game element that process player actions.
 * <p>
 * I'M HAVING SO MUCH FUN CALLING EACH PLAYERELEMENT MANUALLY MMMM
 */
public interface PlayerElement {

    interface Caller {
        void callOnStart();

        void callOnStop();

        void callOnDeath();

        void callOnPlayersRevealed();
    }

    /**
     * Called once for each player whenever game stars.
     *
     * @param player - Player.
     */
    default void onStart(@Nonnull GamePlayer player) {
    }

    /**
     * Called once for each player whenever the game stops.
     *
     * @param player - Player.
     */
    default void onStop(@Nonnull GamePlayer player) {
    }

    /**
     * Called once for each player whenever they die.
     *
     * @param player - Player.
     */
    default void onDeath(@Nonnull GamePlayer player) {
    }

    /**
     * Called once for each player whenever players are revealed.
     *
     * @param player - Player.
     */
    default void onPlayersRevealed(@Nonnull GamePlayer player) {
    }

}

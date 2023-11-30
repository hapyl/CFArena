package me.hapyl.fight.game;

import me.hapyl.fight.game.entity.GamePlayer;

import javax.annotation.Nonnull;

/**
 * Indicates that this class a game element that process player actions.
 */
public interface PlayerElement {

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
    default void onPlayersReveal(@Nonnull GamePlayer player) {
    }

}

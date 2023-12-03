package me.hapyl.fight.game;

import org.bukkit.entity.Player;

/**
 * Indicates that this class a game element that process player actions.
 */
public interface PlayerElement {

    /**
     * Called once for each player whenever game stars.
     *
     * @param player - Player.
     */
    default void onStart(Player player) {
    }

    /**
     * Called once for each player whenever game stops.
     *
     * @param player - Player.
     */
    default void onStop(Player player) {
    }

    /**
     * Called once for each player whenever they die.
     *
     * @param player - Player.
     */
    default void onDeath(Player player) {
    }

}

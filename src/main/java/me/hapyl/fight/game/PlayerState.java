package me.hapyl.fight.game;

public enum PlayerState {

    /**
     * The player is alive.
     */
    ALIVE,
    /**
     * The player has died.
     */
    DEAD,
    /**
     * The player is spectating.
     * This is different from dead, because the spectator tag
     * is only given to spectators at the start of the game.
     */
    SPECTATOR,
    /**
     * The player is currently spectating.
     */
    RESPAWNING

}

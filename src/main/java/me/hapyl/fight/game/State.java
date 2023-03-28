package me.hapyl.fight.game;

public enum State {

    /**
     * Pre game, the game has started but players are not revealed yet.
     */
    PRE_GAME,
    /**
     * In game.
     */
    IN_GAME,
    /**
     * Post game, the game has ended and winner is announced.
     */
    POST_GAME,
    /**
     * The game has finished completely and players teleported to the lobby.
     */
    FINISHED

}

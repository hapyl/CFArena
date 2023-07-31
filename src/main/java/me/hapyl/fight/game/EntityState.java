package me.hapyl.fight.game;

public enum EntityState {

    /**
     * The player is alive.
     */
    ALIVE("&aAlive"),
    /**
     * The player has died.
     */
    DEAD("&cDead"),
    /**
     * The player is spectating.
     * This is different from dead, because the spectator tag
     * is only given to spectators at the start of the game.
     */
    SPECTATOR("&7Spectator"),
    /**
     * The player is currently spectating.
     */
    RESPAWNING("&eRespawning");

    public final String string;

    EntityState(String string) {
        this.string = string;
    }
}

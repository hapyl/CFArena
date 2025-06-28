package me.hapyl.fight.game;

public enum EntityState {
    
    /**
     * The entity is alive.
     */
    ALIVE("&aAlive"),
    
    /**
     * The entity has died.
     * <p>This flag is not effective for non-players entities, since they're removed on death.</p>
     */
    DEAD("&cDead"),
    
    /**
     * The entity is spectating.
     * <p>This flag is only applicable to players.</p>
     */
    SPECTATOR("&7Spectator"),
    
    /**
     * The entity is currently respawning.
     * <p>This flag is only applicable to players.</p>
     */
    RESPAWNING("&eRespawning");
    
    public final String string;
    
    EntityState(String string) {
        this.string = string;
    }
}

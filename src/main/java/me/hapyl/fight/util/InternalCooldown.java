package me.hapyl.fight.util;

/**
 * Stores internal cooldowns (icd) as millis.
 */
public class InternalCooldown {

    private final long cooldown;
    private long lastUse;

    /**
     * Creates a new instance with a static cooldown in <b>millis</b>.
     *
     * @param cooldown - Cooldown in <b>millis</b>.
     */
    public InternalCooldown(long cooldown) {
        this.cooldown = cooldown;
        this.lastUse = 0L;
    }

    /**
     * Starts the cooldown.
     */
    public void startCooldown() {
        this.lastUse = System.currentTimeMillis();
    }

    /**
     * Returns true if the cooldown was started and is still active.
     *
     * @return true if the cooldown was started and is still active.
     */
    public boolean isOnCooldown() {
        return this.lastUse != 0L && System.currentTimeMillis() - this.lastUse < this.cooldown;
    }

    /**
     * Gets the time since last use in millis.
     *
     * @return time since last use in millis.
     */
    public long timeSinceLastUse() {
        return this.lastUse != 0L ? System.currentTimeMillis() - this.lastUse : this.lastUse;
    }

    /**
     * Gets the time since last use in ticks.
     *
     * @return time since last use in ticks.
     */
    public int timeSinceLastUseInTicks() {
        return (int) timeSinceLastUse() / 50;
    }

}

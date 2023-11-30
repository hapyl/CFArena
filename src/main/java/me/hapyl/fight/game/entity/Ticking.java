package me.hapyl.fight.game.entity;

import me.hapyl.fight.game.task.GameTask;

import javax.annotation.Nonnull;

public interface Ticking {

    void tick();

    /**
     * Starts ticking this interface with the given delay and period.
     * The ticking is using the {@link GameTask} and is automatically canceled stop.
     * This waits one tick before each tick.
     */
    @Nonnull
    default GameTask startTicking() {
        return startTicking(1, 1);
    }

    /**
     * Starts ticking this interface with the given delay and period.
     * The ticking is using the {@link GameTask} and is automatically canceled stop.
     * This waits one tick before the first tick.
     *
     * @param period - Tick period.
     */
    @Nonnull
    default GameTask startTicking(int period) {
        return startTicking(1, period);
    }

    /**
     * Starts ticking this interface with the given delay and period.
     * The ticking is using the {@link GameTask} and is automatically canceled stop.
     *
     * @param delay  - Tick delay.
     * @param period - Tick period.
     */
    @Nonnull
    default GameTask startTicking(int delay, int period) {
        return new GameTask() {
            @Override
            public void run() {
                Ticking.this.tick();
            }
        }.runTaskTimer(delay, period);
    }
}

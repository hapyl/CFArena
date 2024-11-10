package me.hapyl.fight.game.cosmetic.win;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.ShutdownAction;
import org.bukkit.Location;

import javax.annotation.Nonnull;

public abstract class WinCosmetic extends Cosmetic {

    private int delay;
    private int maxTimes;
    private int step;

    public WinCosmetic(@Nonnull Key key, @Nonnull String name) {
        super(key, name, Type.WIN);

        this.delay = 10;
        this.maxTimes = 1;
        this.step = 0;
    }

    public void setMaxTimes(int maxTimes) {
        this.maxTimes = maxTimes;
    }

    public void setStep(int step) {
        this.step = step;
    }

    /**
     * Called when the cosmetic is displayed.
     *
     * @param display - Display object.
     */
    public abstract void onStart(@Nonnull Display display);

    /**
     * Called when the cosmetic is stopped, {@link WinCosmetic#delay} ticks before calling the manager stop.
     *
     * @param display - Display object.
     */
    public abstract void onStop(@Nonnull Display display);

    /**
     * Called every {@link WinCosmetic#step} ticks.
     *
     * @param display - Display object.
     * @param tick    - Ticks left. 1-{@link WinCosmetic#maxTimes}, on 0 {@link WinCosmetic#onStop(Display)} is called.
     */
    public abstract void tickTask(@Nonnull Display display, int tick);

    @Override
    public final void onDisplay(@Nonnull Display display) {
        onStart(display);
        GameTask.runTaskTimerTimes((task, tick) -> {
            if (tick == 0) {
                WinCosmetic.this.onStop(display);
                return;
            }

            WinCosmetic.this.tickTask(display, tick);
        }, step, step, maxTimes).setShutdownAction(ShutdownAction.IGNORE);
    }

    public int getMaxTimes() {
        return maxTimes;
    }

    public int getStep() {
        return step;
    }

    public void setAdditionalDelay(int delay) {
        this.delay = delay;
    }

    public int getDelay() {
        return (maxTimes * step) + delay;
    }

    /**
     * Called after the delay, just before calling the manager stop.
     *
     * @param location - Location of execution.
     */
    public void onStop(Location location) {
    }
}

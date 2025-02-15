package me.hapyl.fight.game.cosmetic.win;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.game.task.ShutdownAction;
import me.hapyl.fight.game.task.TickingGameTask;
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
     * @param display - The display.
     * @param tick    - The current tick, from {@code 0} to {@code maxTimes - 1}.
     *                On {@code tick == maxTimes}, {@link #onStop(Display)} is called.
     */
    public abstract void onTick(@Nonnull Display display, int tick);

    @Override
    public final void onDisplay(@Nonnull Display display) {
        onStart(display);

        new TickingGameTask() {
            @Override
            public void run(int tick) {
                if (tick >= maxTimes) {
                    onStop(display);
                    return;
                }

                onTick(display, tick);
            }
        }.shutdownAction(ShutdownAction.IGNORE)
         .runTaskTimer(step, step);
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

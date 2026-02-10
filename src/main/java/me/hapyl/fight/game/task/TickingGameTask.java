package me.hapyl.fight.game.task;

import me.hapyl.eterna.module.annotate.EventLike;

import javax.annotation.OverridingMethodsMustInvokeSuper;

// A game task with a designated tick variable
public abstract class TickingGameTask extends GameTask {

    /*package-private*/ int tick;
    private int increment = 1;
    private boolean ticked;

    public TickingGameTask(final int initTick) {
        this.tick = initTick;
        this.ticked = false;
    }

    public TickingGameTask() {
        this(0);
    }

    public abstract void run(final int tick);

    public TickingGameTask setIncrement(int increment) {
        this.increment = increment;
        return this;
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void run() {
        if (!ticked) {
            ticked = true;
            onFirstTick();
        }

        run(tick);
        tick += increment;
    }

    @EventLike
    public void onFirstTick() {
    }

    public int getTick() {
        return tick;
    }

    /**
     * Returns true if the current tick value is greater than 0, and it's modulo is equals the given value.
     *
     * @param mod - modulo operator.
     * @return true, if the current tick value is greater than 0, and it's modulo is equals the given value.
     */
    protected boolean modulo(int mod) {
        return tick > 0 && tick % mod == 0;
    }

}

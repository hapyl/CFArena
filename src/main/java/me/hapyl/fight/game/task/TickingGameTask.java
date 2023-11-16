package me.hapyl.fight.game.task;

import me.hapyl.fight.game.Event;

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
    public void run() {
        if (!ticked) {
            ticked = true;
            onTickOnce();
        }

        run(tick);
        tick += increment;
    }

    @Event
    public void onTickOnce() {
    }

    public int getTick() {
        return tick;
    }
}

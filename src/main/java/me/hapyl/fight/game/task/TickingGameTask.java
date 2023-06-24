package me.hapyl.fight.game.task;

// A game task with a designated tick variable
public abstract class TickingGameTask extends GameTask {

    private int increment = 1;
    private int tick;

    public TickingGameTask(final int initTick) {
        this.tick = initTick;
    }

    public TickingGameTask() {
        this(0);
    }

    public abstract void run(final int tick);

    public void setIncrement(int increment) {
        this.increment = increment;
    }

    @Override
    public final void run() {
        run(tick);

        tick += increment;
    }

}

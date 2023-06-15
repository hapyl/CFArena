package me.hapyl.fight.game.task;

// A game task with a designated tick variable
public abstract class TickingGameTask extends GameTask {

    private int increment = 1;
    private int tick;

    public abstract void run(final int tick);

    public void setIncrement(int increment) {
        this.increment = Math.max(1, increment);
    }

    @Override
    public final void run() {
        run(tick);

        tick += increment;
    }
}

package me.hapyl.fight.game.task;

// A game task with designated tick variable
public abstract class TickingGameTask extends GameTask {

    protected int increment = 1;
    private int tick;

    public abstract void run(final int tick);

    @Override
    public final void run() {
        run(tick);

        tick += increment;
    }
}

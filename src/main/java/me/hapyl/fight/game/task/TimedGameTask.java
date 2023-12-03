package me.hapyl.fight.game.task;

import me.hapyl.fight.game.Event;
import me.hapyl.fight.game.talents.Timed;

// Timed tasks are the same as ticking game task, but with a max tick value
// that will automatically cancel the task upon reaching the limit.
public abstract class TimedGameTask extends TickingGameTask {

    protected final int maxTick;

    public TimedGameTask(Timed timed) {
        this(timed.getDuration());
    }

    public TimedGameTask(int maxTick) {
        this(maxTick, 0);
    }

    public TimedGameTask(int maxTick, int initTick) {
        super(initTick);
        this.maxTick = maxTick;
    }

    public int getMaxTick() {
        return maxTick;
    }

    @Event
    public void onLastTick() {
    }

    @Override
    public void run() {
        if (tick >= maxTick) {
            cancel();
            onLastTick();
            return;
        }

        super.run();
    }
}

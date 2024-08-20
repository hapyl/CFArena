package me.hapyl.fight.game.task;

import me.hapyl.fight.util.ParameterPrecondition;

public abstract class TickingStepGameTask extends TickingGameTask {

    private final int steps;

    protected TickingStepGameTask(int steps) {
        this.steps = ParameterPrecondition.of(steps, i -> i >= 1 && i <= 100);
    }

    /**
     * Returns true to cancel the task after the iteration; false to ignore.
     *
     * @param tick - Current tick.
     * @return true to cancel the task; false to ignore.
     */
    public abstract boolean tick(int tick);

    public void onActualTick(int tick) {
    }

    @Override
    public final void run(int tick) {
        for (int i = 0; i < steps; i++) {
            if (tick(tick)) {
                cancel();
                return;
            }
        }

        onActualTick(tick);
    }

}

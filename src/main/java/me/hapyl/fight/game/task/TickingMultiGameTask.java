package me.hapyl.fight.game.task;

import me.hapyl.fight.util.ParameterPrecondition;

public abstract class TickingMultiGameTask extends TickingGameTask {

    private final int speed;

    protected TickingMultiGameTask(int speed) {
        this.speed = ParameterPrecondition.of(speed, i -> i >= 1 && i <= 100);
    }

    /**
     * Returns true to cancel the task after the iteration; false to ignore.
     *
     * @param tick - Current tick.
     * @return true to cancel the task; false to ignore.
     */
    public abstract boolean tick(int tick);

    @Override
    public final void run(int tick) {
        if (speed == 1) {
            if (tick(tick)) {
                cancel();
            }
        }
        else {
            for (int i = 0; i < speed; i++) {
                if (tick(tick)) {
                    cancel();
                    return;
                }
            }
        }
    }

}

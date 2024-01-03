package me.hapyl.fight.game.attribute.temper;

import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.CFUtils;

public abstract class AttributeTemper implements Runnable {

    public final double value;
    public final int duration;

    private GameTask task;

    public AttributeTemper(double value) {
        this(value, -1);
    }

    public AttributeTemper(double value, int duration) {
        this.value = value;
        this.duration = duration;

        if (duration > 0) {
            task = GameTask.runLater(this, duration);
        }
    }

    @Override
    public String toString() {
        return duration == -1 ? "indefinitely" : CFUtils.decimalFormatTick(duration);
    }

    protected void cancel() {
        if (task != null) {
            task.cancel();
        }
    }

}

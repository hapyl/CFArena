package me.hapyl.fight.game.attribute.temper;

import me.hapyl.fight.game.task.GameTask;

public abstract class AttributeTemper implements Runnable {

    protected final double value;
    protected final int duration;

    private final GameTask task;

    public AttributeTemper(double value, int duration) {
        this.value = value;
        this.duration = duration;

        task = GameTask.runLater(this, duration);
    }

    protected void cancel() {
        task.cancelIfActive();
    }

}

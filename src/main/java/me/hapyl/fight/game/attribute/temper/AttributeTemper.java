package me.hapyl.fight.game.attribute.temper;

import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.CFUtils;

import javax.annotation.Nullable;

public abstract class AttributeTemper implements Runnable {

    public final double value;
    public final int duration;
    public final boolean isBuff;
    @Nullable
    public final LivingGameEntity applier;
    public final long appliedAt;

    private GameTask task;

    public AttributeTemper(double value, int duration, boolean isBuff, @Nullable LivingGameEntity applier) {
        this.value = value;
        this.duration = duration;
        this.isBuff = isBuff;
        this.applier = applier;
        this.appliedAt = System.currentTimeMillis();

        if (duration > 0) {
            task = GameTask.runLater(this, duration);
        }
    }

    @Override
    public String toString() {
        return duration == -1 ? "indefinitely" : CFUtils.formatTick(duration);
    }

    protected void cancel() {
        if (task != null) {
            task.cancel();
        }
    }

}

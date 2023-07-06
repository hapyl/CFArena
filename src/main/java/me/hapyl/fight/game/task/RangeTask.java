package me.hapyl.fight.game.task;

import com.google.common.collect.Maps;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Consumer;

public abstract class RangeTask extends TickingGameTask {

    protected final Map<TickRange, Consumer<RangeTask>> rangeMap;
    protected int max;

    public RangeTask() {
        this.rangeMap = Maps.newLinkedHashMap();
        this.max = -1;
    }

    public int getRange(int index) {
        int i = 0;
        for (TickRange range : rangeMap.keySet()) {
            if (i == index++) {
                return range.max;
            }
        }

        return 0;
    }

    public int getMax() {
        return max;
    }

    public RangeTask at(int min, int max, @Nonnull Consumer<RangeTask> consumer) {
        rangeMap.put(new TickRange(min, max), consumer);
        if (this.max == -1 || max > this.max) {
            this.max = max;
        }

        return this;
    }

    public RangeTask at(int value, @Nonnull Consumer<RangeTask> consumer) {
        return at(value, value, consumer);
    }

    public abstract void tick(int tick);

    @Override
    public final void run(int tick) {
        if (this.max != -1 && tick > this.max) {
            cancel();
            return;
        }

        tick(tick);

        if (rangeMap.isEmpty()) {
            return;
        }

        rangeMap.forEach((range, consumer) -> {
            if (range.isWithinRange(tick)) {
                consumer.accept(this);
            }
        });
    }
}

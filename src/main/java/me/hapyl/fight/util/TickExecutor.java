package me.hapyl.fight.util;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.task.TickingGameTask;

import javax.annotation.Nonnull;
import java.util.Map;

public class TickExecutor extends TickingGameTask {

    private final Map<Integer, Runnable> runnableMap;
    private int maxTick;

    public TickExecutor() {
        this.runnableMap = Maps.newHashMap();
    }

    public TickExecutor at(int tick, @Nonnull Runnable runnable) {
        this.runnableMap.put(tick, runnable);
        this.maxTick = Math.max(maxTick, tick);

        return this;
    }

    @Override
    public final void run(int tick) {
        if (tick > maxTick) {
            this.cancel();
            return;
        }

        final Runnable runnable = runnableMap.get(tick);

        if (runnable != null) {
            runnable.run();
        }
    }
}

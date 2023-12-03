package me.hapyl.fight.game.task;

import com.google.common.collect.Sets;

import java.util.Set;
import java.util.function.Consumer;

/**
 * This implementation automatically handles delayed
 * tasks that are accessible and cancellable early.
 * <p>
 * See {@link #cancelAll()}
 */
public final class CachedDelayedGameTaskList {

    private final Set<GameTask> tasks = Sets.newConcurrentHashSet();

    public GameTask schedule(Consumer<GameTask> toRun, int delay) {
        final GameTask task = new GameTask() {
            @Override
            public void run() {
                toRun.accept(this);
                tasks.remove(this);
            }
        }.runTaskLater(delay);

        tasks.add(task);
        return task;
    }

    public void cancelAll() {
        for (GameTask task : tasks) {
            task.cancelIfActive();
        }

        tasks.clear();
    }

}

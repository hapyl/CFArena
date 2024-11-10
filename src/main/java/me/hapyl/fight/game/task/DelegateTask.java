package me.hapyl.fight.game.task;

import com.google.common.collect.Maps;
import me.hapyl.fight.util.Delegate;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;
import java.util.Map;

public final class DelegateTask {

    private static final Map<Delegate, BukkitTask> DELEGATE;

    static {
        DELEGATE = Maps.newIdentityHashMap();
    }

    @Nonnull
    public static BukkitTask of(@Nonnull Delegate delegate) {
        final BukkitTask task = DELEGATE.get(delegate);

        if (task != null) {
            return task;
        }

        throw new IllegalStateException("No delegate!");
    }

    public static void delegate(@Nonnull Delegate delegate, @Nonnull BukkitTask task) {
        if (DELEGATE.containsKey(delegate)) {
            throw new IllegalStateException("Duplicate task delegate");
        }

        DELEGATE.put(delegate, task);
    }

    public static void cancel(@Nonnull Delegate delegate) {
        final BukkitTask task = DELEGATE.remove(delegate);

        if (task != null) {
            task.cancel();
        }
    }

}

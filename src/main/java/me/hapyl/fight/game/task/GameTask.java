package me.hapyl.fight.game.task;

import me.hapyl.eterna.module.annotate.EventLike;
import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.talents.Timed;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Represents a task automatically canceled when the game ends.
 */
public abstract class GameTask implements Runnable, BukkitTask {

    private ShutdownAction shutdownAction;
    private BukkitTask bukkitTask;
    private BukkitRunnable atCancel;

    public GameTask() {
        this.shutdownAction = ShutdownAction.CANCEL;
    }

    public ShutdownAction getShutdownAction() {
        return shutdownAction;
    }

    public void setShutdownAction(ShutdownAction shutdownAction) {
        this.shutdownAction = shutdownAction;
    }

    public GameTask addCancelEvent(BukkitRunnable task) {
        this.atCancel = task;
        return this;
    }

    public GameTask addCancelEvent(Runnable runnable) {
        return this.addCancelEvent(new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        });
    }

    public synchronized GameTask runTaskAtCancel() {
        this.addCancelEvent(new BukkitRunnable() {
            @Override
            public void run() {
                GameTask.this.run();
            }
        });
        return this;
    }

    public synchronized GameTask runTaskLater(long later) {
        if (!canExecute()) {
            return this;
        }

        validateDoesNotExists();
        return setupTask(Bukkit.getScheduler().runTaskLater(Main.getPlugin(), this, later));
    }

    public synchronized GameTask runTaskTimer(long delay, long period) {
        if (!canExecute()) {
            return this;
        }
        this.validateDoesNotExists();
        return this.setupTask(Bukkit.getScheduler().runTaskTimer(Main.getPlugin(), this, delay, period));
    }

    public GameTask runTaskTimerAsync(long delay, long period) {
        if (!canExecute()) {
            return this;
        }

        this.validateDoesNotExists();
        return this.setupTask(Bukkit.getScheduler().runTaskTimerAsynchronously(CF.getPlugin(), this, delay, period));
    }

    public synchronized GameTask runTask() {
        if (!canExecute()) {
            return this;
        }
        this.validateDoesNotExists();
        return this.setupTask(Bukkit.getScheduler().runTask(CF.getPlugin(), this));
    }

    public synchronized void deepCancel() {
        if (atCancel != null) {
            atCancel.runTask(CF.getPlugin());
        }
        this.cancel();
    }

    public synchronized int getId() {
        return bukkitTask == null ? -1 : bukkitTask.getTaskId();
    }

    @EventLike
    public void onTaskStart() {
    }

    @EventLike
    public void onTaskStop() {
    }

    public synchronized void cancel() {
        if (bukkitTask == null || bukkitTask.isCancelled()) {
            return;
        }

        onTaskStop();
        cancel0();
    }

    public synchronized boolean isCancelled() {
        if (bukkitTask == null) {
            return false;
        }

        return bukkitTask.isCancelled();
    }

    /**
     * @deprecated Do not call unless you know what you're doing, use {@link GameTask#cancel} instead.
     * @see GameTask#cancel()
     */
    @Deprecated
    public void cancel0() {
        if (bukkitTask == null) {
            Debug.warn("Tried to cancel an inactive task!");
            return;
        }

        Bukkit.getScheduler().cancelTask(getId());
    }

    @Override
    public final int getTaskId() {
        return bukkitTask.getTaskId();
    }

    @Override
    @Nonnull
    public final Plugin getOwner() {
        return CF.getPlugin();
    }

    @Override
    public final boolean isSync() {
        return bukkitTask.isSync();
    }

    public GameTask shutdownAction(@Nonnull ShutdownAction shutdownAction) {
        this.shutdownAction = shutdownAction;
        return this;
    }

    private void validateDoesNotExists() {
        if (bukkitTask != null) {
            throw Debug.exception(IllegalStateException.class, "Tried to register an already registered task with id %s!".formatted(getId()));
        }
    }

    private synchronized GameTask setupTask(BukkitTask task) {
        final Main plugin = CF.getPlugin();

        if (!plugin.isEnabled()) {
            Debug.severe("Cannot schedule a task while the plugin is disabled!");
            return this;
        }

        this.bukkitTask = task;
        onTaskStart();
        plugin.getTaskList().register(this);
        return this;
    }

    private boolean canExecute() {
        return CF.getPlugin().isEnabled();
    }

    /**
     * Runs the task for the duration of the timed.
     *
     * @param timed    - Timed.
     * @param runnable - BiConsumer of task runner and remaining tick.
     * @param delay    - Delay before starting.
     * @param period   - Period after each execution.
     * @return Running task.
     */
    public static GameTask runDuration(Timed timed, BiConsumer<GameTask, Integer> runnable, int delay, int period) {
        final int duration = timed.getDuration();

        return new GameTask() {
            private int tick = duration;

            @Override
            public void run() {
                if (tick < 0) {
                    this.cancel();
                    return;
                }

                runnable.accept(this, tick);
                tick -= period;
            }
        }.runTaskTimer(delay, period);
    }

    public static void runDuration(Timed talent, Consumer<Integer> runnable, int period) {
        runDuration(talent, (task, i) -> runnable.accept(i), 0, period);
    }

    public static void runDuration(Timed talent, Consumer<Integer> runnable, int delay, int period) {
        runDuration(talent, (task, i) -> runnable.accept(i), delay, period);
    }

    public static GameTask runTaskTimerTimes(Consumer<GameTask> runnable, int delay, int period, int maxTimes) {
        return runTaskTimerTimes((a, b) -> runnable.accept(a), delay, period, maxTimes);
    }

    public static GameTask runTaskTimer(Consumer<GameTask> runnable, int delay, int period) {
        return runTaskTimerTimes((a, b) -> runnable.accept(a), delay, period, Integer.MAX_VALUE);
    }

    public static GameTask runTaskTimerTimes(Consumer<GameTask> runnable, int period, int maxTimes) {
        return runTaskTimerTimes((a, b) -> runnable.accept(a), 0, period, maxTimes);
    }

    public static GameTask scheduleCancelTask(Runnable runnable) {
        return new GameTask() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskAtCancel().runTaskLater(Long.MAX_VALUE);
    }

    public static GameTask runTaskTimerTimes(BiConsumer<GameTask, Integer> runnable, int delay, int period, int maxTimes) {
        return new GameTask() {
            private int tick = Math.max(1, maxTimes);

            @Override
            public void run() {
                if (tick-- <= 0) {
                    this.cancel();
                    return;
                }

                runnable.accept(this, tick);
            }
        }.runTaskTimer(delay, period);
    }

    public static GameTask runTaskTimerTimes(BiConsumer<GameTask, Integer> runnable, int delayBetween, int maxTimes) {
        return runTaskTimerTimes(runnable, 0, delayBetween, maxTimes);
    }

    public static GameTask runLater(Runnable runnable, int later) {
        return runLater(runnable, later, false);
    }

    public static GameTask runLater(Runnable runnable, int later, boolean runAtCancel) {
        final GameTask task = new GameTask() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskLater(later);
        if (runAtCancel) {
            task.runTaskAtCancel();
        }
        return task;
    }

    public static void runWhileReversed(double start, double condition, double decrement, BiConsumer<Double, GameTask> task) {
        if (start <= condition) {
            runWhile(start, condition, decrement, task);
            return;
        }

        new GameTask() {
            private double d = start;

            @Override
            public void run() {
                if (d <= condition) {
                    this.cancel();
                    return;
                }

                task.accept(d, this);

                d -= decrement;
            }
        }.runTaskTimer(0, 1);
    }

    public static void runWhile(double start, double condition, double increment, BiConsumer<Double, GameTask> task) {
        if (start >= condition) {
            runWhileReversed(start, condition, increment, task);
            return;
        }

        new GameTask() {
            private double d = start;

            @Override
            public void run() {
                if (d >= condition) {
                    this.cancel();
                    return;
                }

                task.accept(d, this);

                d += increment;
            }
        }.runTaskTimer(0, 1);
    }

}

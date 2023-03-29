package me.hapyl.fight.game.task;

import me.hapyl.fight.Main;
import me.hapyl.fight.game.talents.Talent;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class GameTask implements Runnable {

    private ShutdownAction shutdownAction;
    private BukkitTask task;
    private BukkitRunnable atCancel;

    public GameTask() {
        this.shutdownAction = ShutdownAction.CANCEL;
    }

    public static GameTask runDuration(Talent talent, BiConsumer<GameTask, Integer> runnable, int delay, int period) {
        final int duration = talent.getDuration();

        return new GameTask() {
            private int tick = duration;

            @Override
            public void run() {
                if (tick <= 0) {
                    this.cancel();
                    return;
                }

                runnable.accept(this, tick);
                tick -= period;
            }
        }.runTaskTimer(delay, period);
    }

    public static void runDuration(Talent talent, Consumer<Integer> runnable, int period) {
        runDuration(talent, (task, i) -> runnable.accept(i), 0, period);
    }

    public static void runDuration(Talent talent, Consumer<Integer> runnable, int delay, int period) {
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

    public void setShutdownAction(ShutdownAction shutdownAction) {
        this.shutdownAction = shutdownAction;
    }

    public ShutdownAction getShutdownAction() {
        return shutdownAction;
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
        this.validateDoesNotExists();
        return this.setupTask(Bukkit.getScheduler().runTaskLater(Main.getPlugin(), this, later));
    }

    public synchronized GameTask runTaskTimer(long delay, long period) {
        if (!canExecute()) {
            return this;
        }
        this.validateDoesNotExists();
        return this.setupTask(Bukkit.getScheduler().runTaskTimer(Main.getPlugin(), this, delay, period));
    }

    public synchronized GameTask runTask() {
        if (!canExecute()) {
            return this;
        }
        this.validateDoesNotExists();
        return this.setupTask(Bukkit.getScheduler().runTask(Main.getPlugin(), this));
    }

    public synchronized void deepCancel() {
        if (atCancel != null) {
            atCancel.runTask(Main.getPlugin());
        }
        this.cancel();
    }

    public synchronized int getId() {
        this.validateExists();
        return this.task.getTaskId();
    }

    // Called when the task is cancelled
    public void onCancel() {
    }

    public synchronized void cancel() {
        this.validateExists();

        onCancel();
        Bukkit.getScheduler().cancelTask(this.task.getTaskId());
    }

    public synchronized boolean isCancelled() {
        this.validateExists();
        return this.task.isCancelled();
    }

    private void validateExists() {
        if (this.task == null) {
            throw new IllegalStateException("Could not validate task being active");
        }
    }

    private void validateDoesNotExists() {
        if (this.task != null) {
            throw new IllegalStateException(String.format("Cannot run task since it's already running as %s!", this.getId()));
        }
    }

    public synchronized GameTask setupTask(BukkitTask task) {
        if (!Main.getPlugin().isEnabled()) {
            return this;
        }
        this.task = task;
        Main.getPlugin().getTaskList().register(this);
        return this;
    }

    private boolean canExecute() {
        return Main.getPlugin().isEnabled();
    }

}

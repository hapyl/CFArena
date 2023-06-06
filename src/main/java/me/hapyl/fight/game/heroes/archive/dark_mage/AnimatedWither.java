package me.hapyl.fight.game.heroes.archive.dark_mage;

import me.hapyl.fight.Main;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.util.Runnables;
import org.bukkit.Location;
import org.bukkit.entity.Wither;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;

public abstract class AnimatedWither {

    public final Wither wither;

    private BukkitTask task;
    private int invul;

    public AnimatedWither(@Nonnull Location location) {
        this(location, 0);
    }

    public AnimatedWither(@Nonnull Location location, int initInvul) {
        wither = Entities.WITHER.spawn(location, self -> {
            self.setAI(false);
            Utils.setWitherInvul(self, initInvul);
            onInit(self);
        });
    }

    public void onInit(@Nonnull Wither wither) {
    }

    public abstract void onStart();

    public abstract void onStop();

    public abstract void onTick(int tick);

    public final int getInvul() {
        return invul;
    }

    public final void stopAnimation() {
        if (task != null && !task.isCancelled()) {
            task.cancel();
            onStop();
            task = null;
        }
    }

    public final void flip() {
        final String name = wither.getCustomName();
        if (name == null || !name.equalsIgnoreCase("Dinnerbone")) {
            wither.setCustomName("Dinnerbone");
        }
        else {
            wither.setCustomName(null);
        }

        wither.setCustomNameVisible(false);
    }

    public final AnimatedWither startAnimation(int from, int to, float speed) {
        if (from == to) {
            throw new IllegalArgumentException("'from' cannot be the same as 'to'");
        }

        if (speed < 0) {
            throw new IllegalArgumentException("'speed' cannot be negative");
        }

        final boolean increase = from < to;

        stopAnimation();
        onStart();

        task = new BukkitRunnable() {

            private float tick = from;

            @Override
            public void run() {
                if ((increase && (tick += speed) > to) || (!increase && (tick -= speed) < to)) {
                    onStop();
                    cancel();
                    return;
                }

                setInvul((int) tick);
                onTick((int) tick);
            }
        }.runTaskTimer(Main.getPlugin(), 0, 1);

        return this;
    }

    public final void setInvul(int i) {
        invul = i;
        Utils.setWitherInvul(wither, i);
        //wither.setInvulnerabilityTicks(i); - I don't know why it doesn't work, updated with BuildTools
    }

    public void remove() {
        if (wither != null) {
            wither.remove();
        }
    }

    protected final synchronized void doLater(Runnable runnable, long later) {
        Runnables.runLater(runnable, later);
    }

}

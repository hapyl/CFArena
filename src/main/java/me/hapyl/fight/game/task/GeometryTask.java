package me.hapyl.fight.game.task;

import me.hapyl.fight.game.Callback;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.talents.Timed;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

/**
 * A game task that has built-in geometry functions like: sin, cos, tan etc.
 */
public abstract class GeometryTask extends GameTask {

    private int tick;
    private double theta;

    private final Properties properties;

    public GeometryTask() {
        this.properties = new Properties(this);
    }

    @Nonnull
    public Properties properties() {
        return properties;
    }

    public double sin(double distance) {
        return Math.sin(theta) * distance;
    }

    public double cos(double distance) {
        return Math.cos(theta) * distance;
    }

    public double tan(double distance) {
        return Math.tan(theta) * distance;
    }

    public double[] sin2cos(double distance) {
        return new double[] { sin(distance), cos(distance) };
    }

    public double[] cos2sin(double distance) {
        return new double[] { cos(distance), sin(distance) };
    }

    public void offsetXZ(@Nonnull Location location, double distance, @Nonnull Callback<Location> callback) {
        final double[] sin2cos = sin2cos(distance);

        location.add(sin2cos[0], 0, sin2cos[1]);
        callback.callback(location);
        location.subtract(sin2cos[0], 0, sin2cos[1]);
    }

    public void offsetXY(@Nonnull Location location, double distance, @Nonnull Callback<Location> callback) {
        final double[] sin2cos = sin2cos(distance);

        location.add(sin2cos[0], sin2cos[1], 0);
        callback.callback(location);
        location.subtract(sin2cos[0], sin2cos[1], 0);
    }

    public void offsetZY(@Nonnull Location location, double distance, @Nonnull Callback<Location> callback) {
        final double[] sin2cos = sin2cos(distance);

        location.add(0, sin2cos[1], sin2cos[0]);
        callback.callback(location);
        location.subtract(0, sin2cos[1], sin2cos[0]);
    }

    public abstract void run(double theta);

    public void onStop() {
    }

    public void onStart() {
    }

    public int getTick() {
        return tick;
    }

    private boolean shouldCancel() {
        if (tick > properties.maxTick) {
            return true;
        }

        final LivingEntity deathEntity = properties.deathEntity;

        if (deathEntity == null) {
            return false;
        }

        if (deathEntity instanceof Player player) {
            return GamePlayer.getPlayer(player).isDead();
        }

        return deathEntity.isDead();
    }

    @Override
    public final void run() {
        onStart();

        if (shouldCancel()) {
            onStop();
            cancelIfActive();
            return;
        }

        tick += properties.tickMod;

        run(theta);
        theta = theta >= Math.PI * 2 ? 0 : theta + properties.step;
    }

    public static final class Properties {

        private final GeometryTask task;

        private int tickMod = 1;
        private int maxTick;
        private double step;
        private LivingEntity deathEntity;

        public Properties(GeometryTask task) {
            this.task = task;
        }

        public GeometryTask task() {
            return task;
        }

        public Properties cancelIfDead(LivingEntity entity) {
            deathEntity = entity;
            return this;
        }

        public Properties mod(int tickMod) {
            this.tickMod = Math.max(tickMod, 0);
            return this;
        }

        public Properties max(Timed timed) {
            return max(timed.getDuration());
        }

        public Properties max(int max) {
            this.maxTick = Math.max(max, 1);
            return this;
        }

        public Properties step(double step) {
            this.step = Math.min(step, Math.PI * 2);
            return this;
        }
    }
}

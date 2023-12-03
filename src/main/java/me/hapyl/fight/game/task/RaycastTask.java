package me.hapyl.fight.game.task;

import me.hapyl.spigotutils.module.math.Numbers;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public abstract class RaycastTask extends GameTask {

    private final Location location;
    private Vector vector;

    private int iterations;
    private double tick;
    private double max;
    private double step;

    public RaycastTask(@Nonnull Location location) {
        this.location = BukkitUtils.newLocation(location);
        this.vector = location.getDirection().normalize();
        this.iterations = 1;
        this.step = 1;
        this.max = 20;
    }

    public double getMax() {
        return max;
    }

    public RaycastTask setMax(double max) {
        this.max = max / step;
        return this;
    }

    public double getStep() {
        return step;
    }

    public RaycastTask setStep(double step) {
        this.step = step;
        setMax(max);
        return this;
    }

    public Vector getVector() {
        return vector;
    }

    public void setVector(Vector vector) {
        this.vector = vector;
    }

    public RaycastTask setIterations(int iterations) {
        this.iterations = Numbers.clamp(iterations, 1, 100);
        return this;
    }

    public boolean predicate(@Nonnull Location location) {
        return true;
    }

    public abstract boolean step(@Nonnull Location location);

    @Override
    public final void run() {
        for (int i = 0; i < iterations; i++) {
            if (next()) {
                cancel();
                return;
            }
        }
    }

    private boolean next() {
        if (tick >= max) {
            return true;
        }

        final double x = tick * vector.getX();
        final double y = tick * vector.getY();
        final double z = tick * vector.getZ();

        location.add(x, y, z);

        if (!predicate(location) || step(location)) {
            return true;
        }

        location.subtract(x, y, z);
        tick += step;

        return false;
    }

}

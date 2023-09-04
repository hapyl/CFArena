package me.hapyl.fight.fx;

import me.hapyl.fight.game.task.GameTask;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public abstract class SwiftTeleportAnimation extends GameTask {

    private final Location from;
    private final Location to;
    private Vector vector;

    private double speed;
    private double slope;
    private double d;
    private double distance;
    private Location location;
    private double step;

    public SwiftTeleportAnimation(@Nonnull Location from, @Nonnull Location to) {
        this.from = from;
        this.to = to;
        this.speed = 1.0d;
        this.slope = Math.PI;
    }

    public double getSpeed() {
        return speed;
    }

    public SwiftTeleportAnimation setSpeed(double speed) {
        this.speed = speed;
        return this;
    }

    public double getSlope() {
        return slope;
    }

    public SwiftTeleportAnimation setSlope(double slope) {
        this.slope = slope;
        return this;
    }

    public void onAnimationStop() {
    }

    public abstract void onAnimationStep(Location location);

    @Override
    public final void onTaskStop() {
    }

    @Override
    public final void run() {
        if (d > distance) {
            onAnimationStop(); // have to call it here since cancel() throws a cnc error
            cancel();
            return;
        }

        final double y = Math.sin(d / distance * Math.PI) * slope;

        location.add(vector);
        location.add(0, y, 0);

        onAnimationStep(location);

        location.subtract(0, y, 0);
        d += step;
    }

    public void start(int delay, int period) {
        this.distance = from.distance(to);
        this.step = distance / 30 * speed;

        this.vector = to.clone().subtract(from).toVector().normalize().multiply(step);
        this.location = from.clone();

        runTaskTimer(delay, period);
    }

}

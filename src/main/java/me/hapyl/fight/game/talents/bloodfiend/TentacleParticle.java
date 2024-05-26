package me.hapyl.fight.game.talents.bloodfiend;

import me.hapyl.fight.game.task.TickingMultiGameTask;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public abstract class TentacleParticle extends TickingMultiGameTask {

    private final Location location;
    private final Vector vector;
    private final double distance;
    private final double step;

    private double d;

    public TentacleParticle(int speed, Location from, Location to) {
        super(speed);

        this.location = from.clone();
        this.distance = from.distance(to);
        this.step = distance / 30 * 2;

        this.vector = to.clone().subtract(from).toVector().normalize().multiply(step);
    }

    public abstract void draw(@Nonnull Location location);

    public void onDrawFinish(@Nonnull Location location) {
    }

    public double slope() {
        return 0.75d;
    }

    @Override
    public boolean tick(int tick) {
        if (d >= distance) {
            onDrawFinish(location);
            return true;
        }

        final double y = Math.sin(d / distance * Math.PI * 3) * slope();

        location.add(vector);
        location.add(0, y, 0);

        draw(location);

        location.subtract(0, y, 0);
        d += step;
        return false;
    }

}

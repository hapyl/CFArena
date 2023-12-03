package me.hapyl.fight.fx;

import me.hapyl.fight.game.task.TickingGameTask;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import javax.annotation.Nonnull;

public class HoverEntityAnimation extends TickingGameTask {

    private final Entity entity;
    private final Location initialLocation;
    private double speed;
    private double height;
    private int duration;

    public HoverEntityAnimation(@Nonnull Entity entity) {
        this.entity = entity;
        this.initialLocation = entity.getLocation();
        this.speed = 2;
        this.height = 2;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void start(int duration) {
        this.duration = duration;
        runTaskTimer(0, 1);
    }

    @Override
    public final void run(int tick) {
        if (tick >= duration) {
            cancel();
            return;
        }

        final Location location = entity.getLocation();

        final double radians = Math.toRadians(tick * speed);
        final double y = Math.sin(radians) / height;

        location.setY(initialLocation.getY() + y);
        entity.teleport(location);

        onTick();
    }

    @Override
    public final void onTaskStop() {
        onAnimationEnd();
    }

    @Override
    public final void onTaskStart() {
        onAnimationStart();
    }

    public void onAnimationEnd() {
    }

    public void onAnimationStart() {
    }

    public void onTick() {
    }
}

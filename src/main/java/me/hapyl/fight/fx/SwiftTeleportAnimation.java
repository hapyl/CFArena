package me.hapyl.fight.fx;

import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.util.Validate;
import me.hapyl.fight.game.task.TickingGameTask;
import org.bukkit.Location;

import javax.annotation.Nonnull;

public abstract class SwiftTeleportAnimation extends TickingGameTask {
    
    private final Location location;
    private final double[] from;
    private final double[] to;
    private final int duration;
    
    private double height;
    
    public SwiftTeleportAnimation(@Nonnull Location from, @Nonnull Location to, final int duration) {
        Validate.isTrue(from.getWorld().equals(to.getWorld()), "Cannot animate in different worlds!");
        
        this.location = new Location(from.getWorld(), 0, 0, 0);
        this.from = LocationHelper.toCoordinates(from);
        this.to = LocationHelper.toCoordinates(to);
        this.duration = duration;
        this.height = 1.5d;
    }
    
    public double height() {
        return height;
    }
    
    public void height(double height) {
        this.height = height;
    }
    
    public abstract void onStep(@Nonnull Location location);
    
    public abstract void onStop(@Nonnull Location location);
    
    public void start(int delay) {
        runTaskTimer(delay, 1);
    }
    
    @Override
    public final void run(int tick) {
        if (tick > duration) {
            onStop(location);
            cancel();
            return;
        }
        
        final double progress = (double) tick / duration;
        final double x = from[0] + (to[0] - from[0]) * progress;
        final double y = from[1] + (to[1] - from[1]) * progress + Math.sin(progress * Math.PI) * height;
        final double z = from[2] + (to[2] - from[2]) * progress;
        
        location.set(x, y, z);
        onStep(location);
    }
}

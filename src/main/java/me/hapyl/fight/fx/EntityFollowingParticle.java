package me.hapyl.fight.fx;

import me.hapyl.fight.annotate.ForceCloned;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.task.TickingStepGameTask;
import me.hapyl.fight.util.CFUtils;
import org.bukkit.Location;

import javax.annotation.Nonnull;

public abstract class EntityFollowingParticle extends TickingStepGameTask {

    private static final int MAX_TICK = 600;

    public final Location location;
    public final LivingGameEntity target;

    public EntityFollowingParticle(int speed, @ForceCloned Location location, @Nonnull LivingGameEntity target) {
        super(speed);

        this.location = location.clone();
        this.target = target;
    }

    public abstract void draw(int tick, @Nonnull Location location);

    public void onHit(@Nonnull Location location) {
    }

    public double distanceSquared() {
        return 0.8660254037844386d; // 0.75d
    }

    public double mlFactor() {
        return 0.25f;
    }

    @Nonnull
    public Location entityLocation() {
        return target.getLocation();
    }

    @Override
    public boolean tick(int tick, int step) {
        if (tick > MAX_TICK || target.isDeadOrRespawning()) {
            cancel();
            return true;
        }

        final Location entityLocation = entityLocation();

        if (CFUtils.distanceSquared(location, entityLocation) < distanceSquared()) {
            cancel();
            onHit(location);
            return true;
        }

        location.add(entityLocation.subtract(location).toVector().multiply(mlFactor()));
        draw(tick, location);
        return false;
    }


}

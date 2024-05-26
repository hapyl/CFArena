package me.hapyl.fight.fx;

import me.hapyl.fight.annotate.ForceCloned;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.task.TickingMultiGameTask;
import me.hapyl.fight.util.CFUtils;
import org.bukkit.Location;

import javax.annotation.Nonnull;

public abstract class EntityFollowingParticle extends TickingMultiGameTask {

    private static final int MAX_TICK = 600;
    public final Location location;
    public final LivingGameEntity entity;

    public EntityFollowingParticle(int speed, @ForceCloned Location location, @Nonnull LivingGameEntity entity) {
        super(speed);

        this.location = location.clone();
        this.entity = entity;
    }

    public abstract void draw(int tick, @Nonnull Location location);

    public void onHit(@Nonnull Location location) {
    }

    public double distance() {
        return 0.75d;
    }

    public double factor() {
        return 0.25f;
    }

    @Override
    public boolean tick(int tick) {
        if (tick > MAX_TICK || entity.isDeadOrRespawning()) {
            cancel();
            return true;
        }

        final Location entityLocation = entity.getLocation();

        if (CFUtils.distance(entityLocation, location) <= distance()) {
            cancel();
            onHit(location);
            return true;
        }

        location.add(entityLocation.subtract(location).toVector().multiply(factor()));
        draw(tick, location);
        return false;
    }


}

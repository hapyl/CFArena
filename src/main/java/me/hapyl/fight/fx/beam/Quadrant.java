package me.hapyl.fight.fx.beam;

import com.google.common.collect.Lists;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.entity.cooldown.Cooldown;
import me.hapyl.fight.game.talents.Removable;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.Collect;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class Quadrant extends TickingGameTask implements Removable {

    private static final double PI_2 = Math.PI * 2;

    private final Location location;
    private final List<QuadrantBeam> beams;

    private int height = 3;
    private double heightOffset = 0.7d;
    private double distance = 8.0d;

    private double theta = 0.0d;
    private double speed = Math.PI / 128;
    private long cooldown = 500; // in millis

    public Quadrant(Location location) {
        this.location = location;
        this.beams = Lists.newArrayList();
    }

    public void setCooldown(long cooldownMillis) {
        this.cooldown = cooldownMillis;
    }

    public void teleport(Location location) {
        this.location.setWorld(location.getWorld());
        this.location.setX(location.getX());
        this.location.setY(location.getY());
        this.location.setZ(location.getZ());
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setHeightOffset(double heightOffset) {
        this.heightOffset = heightOffset;
    }

    @Override
    public final void onTaskStart() {
        for (int i = 0; i < height; i++) {
            final double y = heightOffset * i + 1;

            location.add(0, y, 0);
            beams.add(new QuadrantBeam(location));
            location.subtract(0, y, 0);
        }
    }

    @Override
    public final void run(int tick) {
        onTick();

        for (int i = 0; i < height; i++) {
            final QuadrantBeam beam = beams.get(i);
            final double y = (i + 1) * heightOffset;
            final double offset = Math.PI / 2;

            for (int j = 0, index = 0; j < 2; j++, index += 2) {
                double x = Math.sin(theta + (offset * j)) * distance;
                double z = Math.cos(theta + (offset * j)) * distance;

                location.add(0, y, 0);

                // Offset guardian
                location.add(x, 0, z);
                final Location rayCastFrom = location.clone();

                beam.setGuardianLocation(index, location);
                location.subtract(x, 0, z);

                // Offset squid
                location.subtract(x, 0, z);
                final Location rayCastTo = location.clone();
                beam.setSquidLocation(index, location);
                location.add(x, 0, z);

                location.subtract(0, y, 0);

                rayCast(rayCastFrom, rayCastTo);
            }
        }

        theta += speed;
        if (theta >= PI_2) {
            theta = 0.0d;
        }
    }

    public abstract void onTouch(@Nonnull LivingGameEntity entity);

    public void onTick() {
    }

    @Override
    public void remove() {
        beams.forEach(QuadrantBeam::remove);
    }

    private void rayCast(Location from, Location to) {
        final Location location = from.clone();

        final double distance = from.distance(to);
        final Vector vector = to.toVector().subtract(from.toVector()).normalize().multiply(1.0d);

        for (double i = 0; i < distance; i += 1.0d) {
            location.add(vector);
            Collect.nearbyEntities(location, 0.5d).forEach(this::onTouch0);
        }
    }

    private void onTouch0(LivingGameEntity entity) {
        if (entity == null) {
            return;
        }

        if (entity.hasCooldown(Cooldown.BEAM_TOUCH)) {
            return;
        }

        onTouch(entity);
        entity.startCooldown(Cooldown.BEAM_TOUCH, cooldown);
    }
}

package me.hapyl.fight.game.maps.features;

import me.hapyl.fight.util.BoundingBoxCollector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class Turbine {

    public static final double MAGNITUDE = -0.1d;

    private final BoundingBoxCollector boundingBox;
    private final BoundingBoxCollector killTrigger;

    private Vector vector;
    private Location fxLocation;
    private Direction direction;

    public Turbine(@Nonnull BoundingBoxCollector boundingBox, @Nonnull BoundingBoxCollector killTrigger) {
        this.boundingBox = boundingBox;
        this.killTrigger = killTrigger;

        direction = Direction.NONE;
    }

    public Vector getVector() {
        if (vector == null) {
            vector = direction.createVector();
        }

        return vector;
    }

    @Nonnull
    public Location getFxLocation() {
        if (fxLocation == null) {
            fxLocation = boundingBox.getCenter().toLocation(Bukkit.getWorlds().get(0));
        }

        return fxLocation;
    }

    @Nonnull
    public BoundingBoxCollector getBoundingBox() {
        return boundingBox;
    }

    @Nonnull
    public BoundingBoxCollector getKillTrigger() {
        return killTrigger;
    }

    public Direction getDirection() {
        return direction;
    }

    public Turbine setDirections(Direction direction) {
        this.direction = direction;
        return this;
    }

    public double getRadius() {
        return switch (direction) {
            case POSITIVE_X, NEGATIVE_X -> (boundingBox.getMaxX() - boundingBox.getMinX()) / 4.0d;
            case POSITIVE_Y, NEGATIVE_Y -> (boundingBox.getMaxY() - boundingBox.getMinY()) / 4.0d;
            case POSITIVE_Z, NEGATIVE_Z -> (boundingBox.getMaxZ() - boundingBox.getMinZ()) / 4.0d;
            default -> 0.0d;
        };
    }

}

package me.hapyl.fight.game.maps.features;

import me.hapyl.fight.util.BoundingBoxCollector;
import me.hapyl.fight.util.Direction;
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
            case EAST, WEST -> (boundingBox.getMaxX() - boundingBox.getMinX()) / Math.PI; // 4.0
            case UP, DOWN -> (boundingBox.getMaxY() - boundingBox.getMinY()) / Math.PI;
            case SOUTH, NORTH -> (boundingBox.getMaxZ() - boundingBox.getMinZ()) / Math.PI;
            default -> 0.0d;
        };
    }

}

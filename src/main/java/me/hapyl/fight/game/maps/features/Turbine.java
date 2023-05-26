package me.hapyl.fight.game.maps.features;

import me.hapyl.fight.util.BoundingBoxCollector;
import me.hapyl.spigotutils.module.math.Numbers;
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

    public enum Direction {

        POSITIVE_X(1, 0, 0),
        NEGATIVE_X(-1, 0, 0),

        POSITIVE_Y(0, 1, 0),
        NEGATIVE_Y(0, -1, 0),

        POSITIVE_Z(0, 0, 1),
        NEGATIVE_Z(0, 0, -1),

        NONE(0, 0, 0);

        private final int[] values;

        Direction(int x, int y, int z) {
            this.values = new int[] { x, y, z };
        }

        public Vector createVector() {
            return new Vector(get(0), get(1), get(2));
        }

        public double get(int i) {
            i = Numbers.clamp(i, 0, values.length - 1);

            final int value = values[i];
            return value == 0 ? 0.0d : MAGNITUDE * value;
        }

        public double getValue(int index, double value, double def) {
            final int val = values[index];

            if (val == 0) {
                return def;
            }

            return value;
        }

        public boolean isAxisX() {
            return this == POSITIVE_X || this == NEGATIVE_X;
        }

        public boolean isAxisY() {
            return this == POSITIVE_Y || this == NEGATIVE_Y;
        }

        public boolean isAxisZ() {
            return this == POSITIVE_Z || this == NEGATIVE_Z;
        }

    }
}

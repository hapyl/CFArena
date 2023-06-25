package me.hapyl.fight.util;

import me.hapyl.fight.game.maps.features.Turbine;
import me.hapyl.spigotutils.module.math.Numbers;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Consumer;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nonnull;

// An 'extension' to BlockFace.
public enum Direction {

    NORTH(0, 0, -1),
    SOUTH(0, 0, 1),
    EAST(1, 0, 0),
    WEST(-1, 0, 0),
    UP(0, 1, 0),
    DOWN(0, -1, 0),
    NONE(0, 0, 0);

    private final int[] values;

    Direction(int x, int y, int z) {
        this.values = new int[] { x, y, z };
    }

    public Vector createVector() {
        return new Vector(getMagnitude(0), getMagnitude(1), getMagnitude(2));
    }

    public double getMagnitude(int i) {
        i = Numbers.clamp(i, 0, values.length - 1);

        final int value = values[i];
        return value == 0 ? 0.0d : Turbine.MAGNITUDE * value;
    }

    public double getValue(int index, double value, double def) {
        final int val = values[index];

        if (val == 0) {
            return def;
        }

        return value;
    }

    public void modifyLocation(Location location, double distance, Consumer<Location> consumer) {
        final double x = values[0] * distance;
        final double y = values[1] * distance;
        final double z = values[2] * distance;

        location.add(x, y, z);
        consumer.accept(location);
        location.subtract(x, y, z);
    }

    public int[] getValues() {
        return values;
    }

    @Nonnull
    public Direction getOpposite() {
        return switch (this) {
            case NORTH -> SOUTH;
            case SOUTH -> NORTH;
            case WEST -> EAST;
            case EAST -> WEST;
            case UP -> DOWN;
            case DOWN -> UP;
            default -> NONE;
        };
    }

    @Nonnull
    public BlockFace getOppositeBlockFace() {
        return getBlockFace().getOppositeFace();
    }

    @Nonnull
    public BlockFace getBlockFace() {
        return switch (this) {
            case NORTH -> BlockFace.NORTH;
            case SOUTH -> BlockFace.SOUTH;
            case WEST -> BlockFace.WEST;
            case EAST -> BlockFace.EAST;
            case UP -> BlockFace.UP;
            case DOWN -> BlockFace.DOWN;
            default -> BlockFace.SELF;
        };
    }

    public boolean isNorth() {
        return this == NORTH;
    }

    public boolean isSouth() {
        return this == SOUTH;
    }

    public boolean isWest() {
        return this == WEST;
    }

    public boolean isEast() {
        return this == EAST;
    }

    public boolean isUp() {
        return this == UP;
    }

    public boolean isDown() {
        return this == DOWN;
    }

    public boolean isAxisX() {
        return this == EAST || this == WEST;
    }

    public boolean isAxisY() {
        return this == UP || this == DOWN;
    }

    public boolean isAxisZ() {
        return this == SOUTH || this == NORTH;
    }

    /**
     * Gets the EulerAngle for this direction.
     * <p>
     * <b>This assumes that the armor stand is looking at south (default spawn direction)</b>
     *
     * @return the EulerAngle for this direction.
     */
    @Nonnull
    public EulerAngle toEulerAngle() {
        return switch (this) {
            case UP -> new EulerAngle(Math.toRadians(90), 0, 0);
            case DOWN -> new EulerAngle(Math.toRadians(-90), 0, 0);
            case SOUTH -> new EulerAngle(0, Math.toRadians(180), 0);
            case WEST -> new EulerAngle(0, Math.toRadians(90), 0);
            case EAST -> new EulerAngle(0, Math.toRadians(-90), 0);
            default -> new EulerAngle(0, 0, 0);
        };
    }

    public boolean isEastOrWest() {
        return isEast() || isWest();
    }

    public boolean isNorthOrSouth() {
        return isNorth() || isSouth();
    }

    public boolean isUpOrDown() {
        return this == UP || this == DOWN;
    }

    public Transformation toTransformation() {
        return new Transformation(
                new Vector3f(0, 0, 0),
                new Quaternionf(),
                new Vector3f(1, 1, 1),
                new Quaternionf()
        );
    }

    @Nonnull
    public static Direction POSITIVE_X() {
        return EAST;
    }

    @Nonnull
    public static Direction NEGATIVE_X() {
        return WEST;
    }

    @Nonnull
    public static Direction POSITIVE_Y() {
        return UP;
    }

    @Nonnull
    public static Direction NEGATIVE_Y() {
        return DOWN;
    }

    @Nonnull
    public static Direction POSITIVE_Z() {
        return SOUTH;
    }

    @Nonnull
    public static Direction NEGATIVE_Z() {
        return NORTH;
    }

    @Nonnull
    public static Direction getDirection(@Nonnull Location location) {
        float yaw = location.getYaw();
        yaw = yaw < 0 ? yaw + 360 : yaw;

        if (yaw >= 315 || yaw < 45) {
            return SOUTH;
        }
        else if (yaw < 135) {
            return WEST;
        }
        else if (yaw < 225) {
            return NORTH;
        }
        else if (yaw < 315) {
            return EAST;
        }
        return NORTH;
    }

}

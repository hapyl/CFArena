package me.hapyl.fight.util;

import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.Random;

public interface Vectors {

    /**
     * Gets a vector with all vectors being zero.
     */
    Vector ZERO = new Vector();

    /**
     * Gets a vector that points down.
     */
    Vector DOWN = new Vector(0, -1, 0);

    /**
     * Gets a vector that points up.
     */
    Vector UP = new Vector(0, 1, 0);

    /**
     * Gets a vector with gravity velocity.
     */
    Vector GRAVITY = new Vector(0, -BukkitUtils.GRAVITY, 0);

    /**
     * Gets a relative cuboid offset.
     */
    double[][] RELATIVE = {
            { -1.0d, 0.0d }, // -x, z
            { 1.0d, 0.0d },  // +x, z
            { 0.0d, -1.0d }, // x, -z
            { 0.0d, 1.0d },  // x, +z
            { 1.0d, 1.0d },  // +x, +z
            { -1.0d, 1.0d }, // -x, +z
            { 1.0d, -1.0d }, // +x, -z
            { -1.0d, -1.0d } // -x, -z
    };

    @Nonnull
    static Vector rotateX(@Nonnull Vector vector, double cos, double sin) {
        double y = vector.getY() * cos - vector.getZ() * sin;
        double z = vector.getY() * sin + vector.getZ() * cos;
        return vector.setY(y).setZ(z);
    }

    @Nonnull
    static Vector rotateY(@Nonnull Vector vector, double cos, double sin) {
        double x = vector.getX() * cos + vector.getZ() * sin;
        double z = vector.getX() * -sin + vector.getZ() * cos;
        return vector.setX(x).setZ(z);
    }

    @Nonnull
    static Vector rotateZ(@Nonnull Vector vector, double cos, double sin) {
        double x = vector.getX() * cos - vector.getY() * sin;
        double y = vector.getX() * sin + vector.getY() * cos;
        return vector.setX(x).setY(y);
    }

    /**
     * Gets a random {@link Vector} between <code>0.0-1.0</code>.
     *
     * @return a random vector.
     */
    @Nonnull
    static Vector random() {
        return random(1, 1);
    }

    /**
     * Gets a random {@link Vector} between <code>0.0-1.0</code>.
     *
     * @param xz - X and Z factor.
     * @param y  - Y factor.
     * @return a random vector.
     */
    @Nonnull
    static Vector random(double xz, double y) {
        return random(xz, y, xz);
    }

    /**
     * Gets a random {@link Vector} between <code>0.0-1.0</code>.
     *
     * @param x - X factor.
     * @param y - Y factor.
     * @param z - Z factor.
     * @return a random vector.
     */
    @Nonnull
    static Vector random(double x, double y, double z) {
        final Random random = new Random();

        return new Vector(random.nextDouble() * x, random.nextDouble() * y, random.nextDouble() * z);
    }

    /**
     * Gets a vector towards the left of the origin.
     *
     * @param origin    - Origin.
     * @param magnitude - Magnitude.
     * @return a vector towards the left of the origin.
     */
    @Nonnull
    static Vector left(@Nonnull Location origin, double magnitude) {
        return origin.getDirection().normalize().setY(0).rotateAroundY(Math.PI / 2).multiply(magnitude);
    }

    /**
     * Gets a vector towards the right of the origin.
     *
     * @param origin    - Origin.
     * @param magnitude - Magnitude.
     * @return a vector towards the right of the origin.
     */
    @Nonnull
    static Vector right(@Nonnull Location origin, double magnitude) {
        return origin.getDirection().normalize().setY(0).rotateAroundY(-Math.PI / 2).multiply(magnitude);
    }

}

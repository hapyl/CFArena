package me.hapyl.fight.util;

import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

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

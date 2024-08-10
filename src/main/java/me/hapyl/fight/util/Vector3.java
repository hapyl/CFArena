package me.hapyl.fight.util;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

/**
 * An immutable vector that holds three {@link Double} coordinates.
 *
 * @param x - X.
 * @param y - Y.
 * @param z - Z.
 */
public record Vector3(double x, double y, double z) {

    @Nonnull
    public static Vector3 of(double x, double y, double z) {
        return new Vector3(x, y, z);
    }

    @Nonnull
    public static Vector3 of(@Nonnull Location location) {
        return new Vector3(location.getX(), location.getY(), location.getZ());
    }

    @Nonnull
    public static Vector3 of(@Nonnull Vector vector) {
        return new Vector3(vector.getX(), vector.getY(), vector.getZ());
    }

}

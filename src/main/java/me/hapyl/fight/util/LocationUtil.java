package me.hapyl.fight.util;

import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * <a href="https://paulbourke.net/miscellaneous/interpolation/">Interpolation methods by Paul Bourke</a>
 */
public final class LocationUtil {

    /**
     * Calculates a new location with a linear interpolation.
     *
     * @param from - Start location.
     * @param to   - End location.
     * @param mu   - The "strength" of the interpolations.
     * @return a new interpolated location.
     */
    @Nonnull
    public static Location lerp(@Nonnull Location from, @Nonnull Location to, double mu) {
        final double x = lerp(from.getX(), to.getX(), mu);
        final double y = lerp(from.getY(), to.getY(), mu);
        final double z = lerp(from.getZ(), to.getZ(), mu);

        return new Location(from.getWorld(), x, y, z);
    }

    /**
     * Calculates a new location with a cosine interpolation.
     * <br>
     * Cosine interpolation is slower but much smoother than the linear interpolation.
     *
     * @param from - Start location.
     * @param to   - End location.
     * @param mu   - The "strength" of the interpolations.
     * @return a new interpolated location.
     */
    @Nonnull
    public static Location clerp(@Nonnull Location from, @Nonnull Location to, double mu) {
        mu = (1 - Math.cos(mu * 2)) / 2;

        return lerp(from, to, mu);
    }

    /**
     * Lerps the number between <code>min</code> and <code>max</code> with the given <code>mu</code>.
     *
     * @param min - Min.
     * @param max - Max.
     * @param mu  - Strength.
     * @return an interpolated number.
     */
    public static double lerp(double min, double max, double mu) {
        return min + mu * (max - min);
    }

    public static void modify(@Nonnull Location location, double x, double y, double z, @Nonnull Consumer<Location> consumer) {
        location.add(x, y, z);

        consumer.accept(location);

        location.subtract(x, y, z);
    }

    @Nonnull
    public static Location addAsNew(@Nonnull Location location, double x, double y, double z) {
        return new Location(
                location.getWorld(),
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch()
        ).add(x, y, z);
    }
}

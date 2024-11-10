package me.hapyl.fight.util;

import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.eterna.module.util.Validate;
import org.bukkit.Axis;
import org.bukkit.Location;
import org.jetbrains.annotations.Range;

import javax.annotation.Nonnull;

public final class LocationHelper2 {

    private LocationHelper2() {
    }

    public static double distanceSquared(@Nonnull Location from, @Nonnull Location to, @Nonnull @Range(from = 1, to = 3) Axis... axis) {
        Validate.isTrue(axis.length > 0, "There must be at least one axis!");

        double distance = 0.0d;

        if (CollectionUtils.contains(axis, Axis.X)) {
            distance += square(from.getX() - to.getX());
        }

        if (CollectionUtils.contains(axis, Axis.Y)) {
            distance += square(from.getY() - to.getY());
        }

        if (CollectionUtils.contains(axis, Axis.Z)) {
            distance += square(from.getZ() - to.getZ());
        }

        return distance;
    }

    private static double square(double a) {
        return a * a;
    }

}

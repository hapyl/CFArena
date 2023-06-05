package me.hapyl.fight.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

/**
 * Immutable location impl.
 */
public final class ImmutableLocation extends Location {

    public ImmutableLocation(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    public ImmutableLocation(World world, double x, double y, double z, float yaw, float pitch) {
        super(world, x, y, z, yaw, pitch);
    }

    public static ImmutableLocation fromLocation(Location location) {
        return new ImmutableLocation(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    @Nonnull
    @Deprecated
    @Override
    public Location multiply(double m) {
        return this;
    }

    @Nonnull
    @Deprecated
    @Override
    public Location subtract(@Nonnull Location vec) {
        return this;
    }

    @Nonnull
    @Deprecated
    @Override
    public Location subtract(@Nonnull Vector vec) {
        return this;
    }

    @Nonnull
    @Deprecated
    @Override
    public Location subtract(double x, double y, double z) {
        return this;
    }

    @Nonnull
    @Deprecated
    @Override
    public Location add(@Nonnull Location vec) {
        return this;
    }

    @Nonnull
    @Deprecated
    @Override
    public Location add(@Nonnull Vector vec) {
        return this;
    }

    @Nonnull
    @Deprecated
    @Override
    public Location add(double x, double y, double z) {
        return this;
    }

    @Deprecated
    @Override
    public void setWorld(World world) {
    }

    @Deprecated
    @Override
    public void setX(double x) {
    }

    @Deprecated
    @Override
    public void setY(double y) {
    }

    @Deprecated
    @Override
    public void setZ(double z) {
    }

    @Deprecated
    @Override
    public void setYaw(float yaw) {
    }

    @Deprecated
    @Override
    public void setPitch(float pitch) {
    }

    @Nonnull
    @Deprecated
    @Override
    public Location setDirection(@Nonnull Vector vector) {
        return this;
    }
}

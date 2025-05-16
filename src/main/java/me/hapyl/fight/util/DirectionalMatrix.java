package me.hapyl.fight.util;

import me.hapyl.fight.game.entity.GameEntity;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import org.joml.Matrix3f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * {@link Matrix3f} implementation with {@link Double} and bukkit {@link Vector} support.
 * <p>
 * <b>This class casts doubles to floats.</b>
 * <p>
 * <b>This automatically uses the +Z, negating and inventing the matrix</b>
 */
public class DirectionalMatrix extends Matrix3f {
    
    /**
     * The Minecraft UP direction.
     */
    public static final Vector3f UP = new Vector3f(0, 1, 0);
    
    /**
     * Creates an empty {@link DirectionalMatrix}.
     */
    public DirectionalMatrix() {
    }
    
    public DirectionalMatrix(@Nonnull Vector vector) {
        setLookAlong(vector);
    }
    
    /**
     * Creates an empty {@link DirectionalMatrix} and sets the {@link #setLookAlong(Vector)} the given entity's direction.
     *
     * @param entity - Entity.
     */
    public DirectionalMatrix(@Nonnull LivingEntity entity) {
        setLookAlong(entity.getEyeLocation().getDirection());
    }
    
    /**
     * Creates an empty {@link DirectionalMatrix} and sets the {@link #setLookAlong(Vector)} the given entity's direction.
     *
     * @param entity - Entity.
     */
    public DirectionalMatrix(@Nonnull GameEntity entity) {
        setLookAlong(entity.getEyeLocation().getDirection());
    }
    
    /**
     * Transform pitch, yaw and roll into a bukkit {@link Vector}.
     *
     * @param x - Pitch.
     * @param y - Yaw.
     * @param z - Roll.
     * @return a bukkit vector with transformed values.
     */
    @Nonnull
    public Vector transform(double x, double y, double z) {
        final Vector3f vector3f = transform(new Vector3f((float) x, (float) y, (float) z));
        
        return new Vector(vector3f.x, vector3f.y, vector3f.z);
    }
    
    /**
     * Transform a {@link Vector} into a bukkit {@link Vector}.
     *
     * @param vector - Vector.
     * @return a bukkit vector with transformed values.
     */
    @Nonnull
    public Vector transform(@Nonnull Vector vector) {
        return transform(vector.getX(), vector.getY(), vector.getZ());
    }
    
    /**
     * Performs a transformation on the given {@link Vector}, modifies location and accepts the {@link Consumer}.
     *
     * @param location - Location to transform.
     * @param vector   - Vector.
     * @param consumer - Consumer.
     */
    public void transformLocation(@Nonnull Location location, @Nonnull Vector vector, @Nonnull Consumer<Location> consumer) {
        final Vector transform = transform(vector);
        
        location.add(transform);
        consumer.accept(location);
        location.subtract(transform);
    }
    
    /**
     * Performs a transformation on the given pitch, yaw and roll, modifies location and accepts the {@link Consumer}.
     *
     * @param location - Location to transform.
     * @param x        - Pitch.
     * @param y        - Yaw.
     * @param z        - Roll.
     * @param consumer - Consumer.
     */
    public void transformLocation(@Nonnull Location location, double x, double y, double z, @Nonnull Consumer<Location> consumer) {
        transformLocation(location, new Vector(x, y, z), consumer);
    }
    
    public void transformLocation(@Nonnull Location location, double x, double y, double z, @Nonnull Runnable runnable) {
        transformLocation(location, x, y, z, l -> runnable.run());
    }
    
    /**
     * Sets the {@link Matrix3f#setLookAlong(Vector3fc, Vector3fc)} along the given pitch, yaw and roll.
     *
     * @param x - Pitch.
     * @param y - Yaw.
     * @param z - Roll.
     * @return the same matrix.
     */
    @Nonnull
    public Matrix3f setLookAlong(double x, double y, double z) {
        return setLookAlong(new Vector(x, y, z));
    }
    
    /**
     * Sets the {@link Matrix3f#setLookAlong(Vector3fc, Vector3fc)} along the given {@link Vector}.
     *
     * @param vector - The vector.
     * @return the same matrix.
     */
    @Nonnull
    public Matrix3f setLookAlong(@Nonnull Vector vector) {
        setLookAlong(new Vector3f((float) vector.getX(), (float) vector.getY(), (float) vector.getZ()).negate(), UP);
        return this.invert();
    }
    
    @Nonnull
    public Location transformLocationAsNew(@Nonnull Location location, double x, double y, double z) {
        return new Location(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch()).add(transform(x, y, z));
    }
}

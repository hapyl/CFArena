package me.hapyl.fight.game.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * A simple ray-cast utility class.
 */
public class Raycast {

    private final World world;
    private final Location location;
    private final Vector vector;
    private final Set<LivingEntity> excludeSet;

    private double maxDistance;
    private double shift;
    private double entitySearchRadius;
    private boolean requireLos;

    private RaycastResult result;

    public Raycast(@Nonnull Location location) {
        this.location = location;
        this.world = Objects.requireNonNull(location.getWorld(), "cannot ray cast in unloaded world.");
        this.vector = location.getDirection().normalize();
        this.excludeSet = Sets.newHashSet();

        this.maxDistance = 20.0d;
        this.shift = 0.5d;
        this.entitySearchRadius = 1.0d;
        this.requireLos = false;
    }

    /**
     * Creates a new ray-cast instance from {@link GamePlayer}'s eyes.
     * <p>
     * This will also <b>exclude</b> the player.
     *
     * @param player - Player.
     * @see #exclude(LivingEntity)
     */
    public Raycast(@Nonnull GamePlayer player) {
        this(player.getEyeLocation());

        exclude(player);
    }

    /**
     * Excludes a given entity from entity search.
     *
     * @param entity - Entity to exclude.
     */
    public Raycast exclude(@Nonnull LivingEntity entity) {
        excludeSet.add(entity);
        return this;
    }

    /**
     * Excludes a given entity from entity search.
     *
     * @param entity - Entity to exclude.
     */
    public Raycast exclude(@Nonnull LivingGameEntity entity) {
        return exclude(entity.getEntity());
    }

    /**
     * Sets the maximum distance this ray cast goes for.
     *
     * @param maxDistance - Max distance.
     */
    public Raycast setMaxDistance(double maxDistance) {
        this.maxDistance = Math.max(1, maxDistance);
        return this;
    }

    /**
     * Sets the shift of this ray cast.
     *
     * @param shift - Shift.
     */
    public Raycast setShift(double shift) {
        this.shift = Math.max(0.1d, shift);
        return this;
    }

    /**
     * Sets the entity search radius.
     *
     * @param radius - Radius.
     */
    public Raycast setEntitySearchRadius(double radius) {
        this.entitySearchRadius = radius;
        return this;
    }

    /**
     * Sets if this ray cast requires entity to have line of sight.
     *
     * @param requireLos - Require line of sight.
     */
    public Raycast setRequireLos(boolean requireLos) {
        this.requireLos = requireLos;
        return this;
    }

    /**
     * Gets the result of this ray cast.
     * <p>
     * This will force the ray cast if not already cast.
     *
     * @return the ray cast result.
     */
    @Nonnull
    public RaycastResult getResult() {
        if (result == null) {
            cast();
        }

        return result;
    }

    /**
     * A predicate method to validate a block.
     *
     * @param block - Block to predicate.
     * @return true to allow this block; false otherwise.
     */
    public boolean predicateBlock(@Nonnull Block block) {
        return !block.isEmpty();
    }

    /**
     * A predicate method to validate an entity.
     *
     * @param entity - Entity to predicate.
     * @return true to allow this entity; false otherwise.
     */
    public boolean predicateEntity(@Nonnull LivingEntity entity) {
        return true;
    }

    /**
     * Re-casts this ray cast.
     * <p>
     * This will dispose of the old {@link #result} and performs a new ray cast.
     *
     * @return the ray cast result.
     */
    @Nonnull
    public RaycastResult recast() {
        this.result = null;
        return cast();
    }

    /**
     * Performs a ray cast.
     * <p>
     * <i>Note:</i> If already cast, this will simply return the ray cast result.
     *
     * @return the ray cast result.
     */
    @Nonnull
    public RaycastResult cast() {
        if (result != null) {
            return result;
        }

        final RaycastResult result = new RaycastResult();

        for (double d = 0.0d; d < maxDistance; d += shift) {
            if (result.isHitBoth() || (result.isHitBlock() && requireLos)) {
                break;
            }

            final double x = vector.getX() * d;
            final double y = vector.getY() * d;
            final double z = vector.getZ() * d;

            location.add(x, y, z);

            // Hit block
            final Block hitBlock = location.getBlock();

            if (predicateBlock(hitBlock)) {
                result.setHitBlock(hitBlock);
            }

            // Hit entity
            if (!result.isHitEntity()) {
                final List<Entity> hitEntities = Lists.newArrayList(world.getNearbyEntities(
                        location,
                        entitySearchRadius,
                        entitySearchRadius,
                        entitySearchRadius
                ));

                hitEntities.removeIf(entity -> {
                    if (!(entity instanceof LivingEntity livingEntity)) {
                        return true;
                    }

                    if (excludeSet.contains(livingEntity)) {
                        return true;
                    }

                    if (!predicateEntity(livingEntity)) {
                        return true;
                    }

                    return false;
                });

                if (!hitEntities.isEmpty()) {
                    hitEntities.sort((o1, o2) -> {
                        final double d1 = o1.getLocation().distance(location);
                        final double d2 = o2.getLocation().distance(location);

                        return Double.compare(d1, d2);
                    });

                    final Entity entity = hitEntities.get(0);

                    if (entity instanceof LivingEntity livingEntity) {
                        result.setHitEntity(livingEntity);
                    }
                }
            }

            location.subtract(x, y, z);
        }

        this.result = result;
        return result;
    }

}

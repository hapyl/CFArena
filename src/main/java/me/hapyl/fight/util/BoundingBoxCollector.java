package me.hapyl.fight.util;

import com.google.common.collect.Sets;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Bounding box extension with collection methods.
 */
public class BoundingBoxCollector extends BoundingBox {

    // represents an empty bounding box to remove nullability
    public static final BoundingBoxCollector EMPTY = new BoundingBoxCollector(0, 0, 0, 0, 0, 0) {

        @Override
        public Collection<Entity> collect(@Nonnull World world, @Nullable Predicate<Entity> filter) {
            return Sets.newHashSet();
        }

        @Override
        public Collection<LivingEntity> collectValid(@Nonnull World world) {
            return Sets.newHashSet();
        }

        @Override
        public Collection<Player> collectPlayers(@Nonnull World world) {
            return Sets.newHashSet();
        }
    };

    public BoundingBoxCollector(double x1, double y1, double z1, double x2, double y2, double z2) {
        super(x1, y1, z1, x2, y2, z2);
    }

    /**
     * Collects all entities in the provided world that are within this bounding box.
     *
     * @param world  - World.
     * @param filter - Filter.
     * @return collection of entities that are within this bounding box.
     */
    public Collection<Entity> collect(@Nonnull World world, @Nullable Predicate<Entity> filter) {
        return world.getNearbyEntities(this, filter);
    }

    /**
     * Collects all valid entities in the provided world that are within this bounding box.
     *
     * @param world - World.
     * @return collection of valid entities that are within this bounding box.
     */
    public Collection<LivingEntity> collectValid(@Nonnull World world) {
        return convert(collect(world, Utils::isEntityValid), LivingEntity.class);
    }

    /**
     * Collects all players in the provided world that are within this bounding box.
     *
     * @param world - World.
     * @return collection of players that are within this bounding box.
     */
    public Collection<Player> collectPlayers(@Nonnull World world) {
        return convert(collect(world, Utils::isEntityValid), Player.class);
    }

    // class cast converter
    private <T extends Entity> Collection<T> convert(Collection<Entity> collection, Class<T> clazz) {
        Set<T> set = Sets.newHashSet();

        collection.forEach(entity -> {
            if (clazz.isInstance(entity)) {
                set.add(clazz.cast(entity));
            }
        });

        return set;
    }

}

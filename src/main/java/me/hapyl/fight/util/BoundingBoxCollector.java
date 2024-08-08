package me.hapyl.fight.util;

import com.google.common.collect.Sets;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.eterna.module.util.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Bounding box extension with collection methods.
 */
public class BoundingBoxCollector extends BoundingBox {

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
    public Collection<LivingGameEntity> collect(@Nonnull World world, @Nullable Predicate<LivingGameEntity> filter) {
        return Collect.nearbyEntities(world, this, filter == null ? f -> true : filter);
    }

    /**
     * Collects all valid entities in the provided world that are within this bounding box.
     *
     * @param world - World.
     * @return collection of valid entities that are within this bounding box.
     */
    public Collection<LivingGameEntity> collect(@Nonnull World world) {
        return collect(world, null);
    }

    /**
     * Collects all players in the provided world that are within this bounding box.
     *
     * @param world - World.
     * @return collection of players that are within this bounding box.
     */
    public Collection<GamePlayer> collectGamePlayers(@Nonnull World world) {
        final Collection<LivingGameEntity> collect = collect(world, null);
        final Set<GamePlayer> set = Sets.newHashSet();

        collect.forEach(entity -> {
            if (entity instanceof GamePlayer gamePlayer) {
                set.add(gamePlayer);
            }
        });

        collect.clear();
        return set;
    }

    @Nonnull
    public Collection<Player> collectPlayers() {
        final World world = BukkitUtils.defWorld();

        return world.getNearbyEntities(this)
                .stream()
                .filter(entity -> entity instanceof Player)
                .map(e -> (Player) e)
                .collect(Collectors.toSet());
    }

    public boolean isWithin(LivingGameEntity gameEntity) {
        final Location location = gameEntity.getLocation();

        return contains(location.getX(), location.getY(), location.getZ());
    }

    public boolean isWithin(Player player) {
        final Location location = player.getLocation();

        return contains(location.getX(), location.getY(), location.getZ());
    }

}

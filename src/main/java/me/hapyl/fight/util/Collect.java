package me.hapyl.fight.util;

import com.google.common.collect.Lists;
import me.hapyl.fight.Main;
import me.hapyl.fight.annotate.ExplicitEntityValidation;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.team.GameTeam;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

// A utility class that allows special case collecting of living entities
public final class Collect {

    /**
     * Gets the list with game players that are considered enemies to a given player.
     *
     * @param player - Player.
     * @return the list of player's enemies.
     */
    public static List<GamePlayer> enemyPlayers(@Nonnull Player player) {
        return Manager.current()
                .getCurrentGame()
                .getAlivePlayers(predicate -> !predicate.isSpectator() && !predicate.compare(player) && !predicate.isTeammate(player));
    }

    /**
     * Gets the target living entity for a given player.
     * This performs a dot product to check.
     *
     * @param player    - Player.
     * @param radius    - Maximum radius.
     * @param dot       - Dot product threshold.
     * @param predicate - Predicate if needed.
     * @return a target living entity; or null if none.
     */
    @ExplicitEntityValidation
    @Nullable
    public static LivingEntity targetLivingEntity(@Nonnull Player player, double radius, double dot, @Nullable Predicate<LivingEntity> predicate) {
        final List<LivingEntity> nearbyEntities = nearbyLivingEntities(player.getLocation(), radius);
        final Vector casterDirection = player.getLocation().getDirection().normalize();

        double closestDot = 0.0d;
        LivingEntity closestEntity = null;

        for (LivingEntity entity : nearbyEntities) {
            // Test Predicate
            if ((!Utils.isEntityValid(entity, player)) || (predicate != null && !predicate.test(entity))) {
                continue;
            }

            final Vector direction = entity.getLocation().subtract(player.getLocation()).toVector().normalize();

            final double dotProduct = casterDirection.dot(direction);
            final double distance = player.getLocation().distance(entity.getLocation());

            if (dotProduct > dot && distance <= radius && dotProduct > closestDot) {
                closestDot = dotProduct;
                closestEntity = entity;
            }
        }

        return closestEntity;
    }

    /**
     * Gets the target player for a given player.
     * This performs a ray-cast distance to check.
     *
     * @param player      - Player.
     * @param maxDistance - Max distance to check.
     * @return a target living entity; or null if none.
     * @deprecated {@link #targetLivingEntity(Player, double, double, Predicate)} is better.
     */
    @ExplicitEntityValidation
    @Nullable
    @Deprecated
    public static Player targetPlayer(@Nonnull Player player, double maxDistance) {
        return (Player) targetLivingEntity(
                player,
                maxDistance,
                entity -> entity != player && entity instanceof Player p && Manager.current().isPlayerInGame(p)
        );
    }

    /**
     * Gets the target player for a given player.
     * This performs a ray-cast distance to check.
     *
     * @param player      - Player.
     * @param maxDistance - Max distance to check.
     * @param predicate   - Predicate.
     * @return a target living entity; or null if none.
     * @deprecated {@link #targetLivingEntity(Player, double, double, Predicate)} is better.
     */
    @ExplicitEntityValidation
    @Nullable
    @Deprecated
    public static LivingEntity targetLivingEntity(@Nonnull Player player, double maxDistance, @Nonnull Predicate<LivingEntity> predicate) {
        final Location location = player.getLocation().add(0, 1.5, 0);
        final Vector vector = location.getDirection().normalize();
        final float radius = 1.25f;

        for (double i = 0; i < maxDistance; i += 0.5d) {
            final double x = vector.getX() * i;
            final double y = vector.getY() * i;
            final double z = vector.getZ() * i;
            location.add(x, y, z);

            for (final LivingEntity entity : nearbyLivingEntities(location, radius)) {
                if (!entity.hasLineOfSight(player) || !predicate.test(entity)) {
                    continue;
                }
                return entity;
            }

            location.subtract(x, y, z);
        }

        return null;
    }

    /**
     * Gets the online operators and the console as a list.
     * Used to send messages to admins.
     *
     * @return the list of online operators and the console.
     * @deprecated {@link Main#getLogger()}
     */
    @Nonnull
    @Deprecated
    public static List<CommandSender> onlineOperatorsAndConsole() {
        final List<CommandSender> list = Lists.newArrayList(Bukkit.getConsoleSender());
        Bukkit.getOnlinePlayers().stream().filter(Player::isOp).forEach(list::add);

        return list;
    }

    /**
     * Gets the nearest player to the given location.
     *
     * @param location - Location.
     * @param radius   - Max radius.
     * @param exclude  - Player to exclude in calculations.
     * @return the nearest player; or null if none.
     */
    @ExplicitEntityValidation
    @Nullable
    public static Player nearestPlayer(@Nonnull Location location, double radius, @Nonnull Player exclude) {
        return (Player) nearestEntityRaw(
                location,
                radius,
                entity -> entity instanceof Player && entity != exclude && Manager.current().isPlayerInGame((Player) entity) &&
                        !GameTeam.isTeammate(exclude, (Player) entity)
        );
    }

    /**
     * Gets the nearest living entity to the given location.
     *
     * @param location - Location.
     * @param radius   - Max radius.
     * @param player   - Player to exclude in the calculations.
     * @return the nearest living entity; or null if none.
     */
    @ExplicitEntityValidation
    @Nullable
    public static LivingEntity nearestLivingEntity(@Nonnull Location location, double radius, @Nonnull Player player) {
        return (LivingEntity) nearestEntityRaw(location, radius, entity -> {
            if (!(entity instanceof LivingEntity)) {
                return false;
            }

            return Utils.isEntityValid(entity, player);
        });
    }

    /**
     * Gets the nearest living entity to the given location, but prioritized players before other entities.
     *
     * @param location  - Location.
     * @param radius    - Max radius.
     * @param predicate - Predicate.
     * @return the nearest living entity; or null if none.
     */
    @ExplicitEntityValidation
    @Nullable
    public static LivingEntity nearestLivingEntityPrioritizePlayers(@Nonnull Location location, double radius, @Nonnull Predicate<LivingEntity> predicate) {
        final LivingEntity nearestPlayer = nearestLivingEntity(
                location,
                radius,
                check -> check instanceof Player && predicate.test(check)
        );

        if (nearestPlayer != null) {
            return nearestPlayer;
        }

        return nearestLivingEntity(location, radius, predicate);
    }

    /**
     * Gets a list with nearby living entities to the given player.
     *
     * @param player - Player.
     * @param radius - Max radius.
     * @return a list with nearby living entities to the given player.
     */
    @ExplicitEntityValidation
    @Nonnull
    public static List<LivingEntity> nearbyLivingEntities(@Nonnull Player player, double radius) {
        return nearbyLivingEntities(player.getLocation(), radius).stream()
                .filter(entity -> entity != player && Utils.isEntityValid(entity, player))
                .collect(Collectors.toList());
    }

    /**
     * Gets a list with nearby living entities to the given location.
     * <p>
     * Since spigot gets entities in a cuboid x*x*x region, it is not the best way
     * to get entities for a "round" region. This method will remove all entities
     * whose distance to the location is greater than the radius.
     * Do note though that distance checks require square root, which is a little heavy!
     *
     * @param location - Location.
     * @param radius   - Max radius.
     * @return a list with nearby living entities to the given location.
     */
    @ExplicitEntityValidation
    @Nonnull
    public static List<LivingEntity> nearbyLivingEntitiesValidate(@Nonnull Location location, double radius) {
        final List<LivingEntity> entities = nearbyLivingEntities(location, radius);
        entities.removeIf(entity -> entity.getLocation().distance(location) > radius);

        return entities;
    }

    /**
     * Gets a list with nearby living entities to the given location with radius validation.
     * <p>
     * Since spigot gets entities in a cuboid x*x*x region, it is not the best way
     * to get entities for a "round" region. This method will remove all entities
     * whose distance to the location is greater than the radius.
     * Do note though that distance checks require square root, which is a little heavy!
     *
     * @param location - Location.
     * @param radius   - Max distance.
     * @return a list with nearby players to the given location.
     */
    @ExplicitEntityValidation
    @Nonnull
    public static List<Player> nearbyPlayersValidate(@Nonnull Location location, double radius) {
        return nearbyLivingEntitiesValidate(location, radius)
                .stream()
                .filter(entity -> entity instanceof Player)
                .map(entity -> (Player) entity)
                .collect(Collectors.toList());
    }

    /**
     * Gets a list with nearby living entities to the given location.
     *
     * @param location - Location.
     * @param radius   - Max radius.
     * @param filter   - Filter.
     * @return a list with nearby living entities to the given location.
     */
    @ExplicitEntityValidation
    @Nonnull
    public static List<LivingEntity> nearbyLivingEntities(@Nonnull Location location, double radius, @Nonnull Predicate<LivingEntity> filter) {
        return nearbyLivingEntities(location, radius).stream().filter(filter).collect(Collectors.toList());
    }

    /**
     * Gets a list of living entity to the given location.
     *
     * @param location - Location.
     * @param radius   - Max radius.
     * @return a list of living entities to the given location.
     */
    @ExplicitEntityValidation
    @Nonnull
    public static List<LivingEntity> nearbyLivingEntities(@Nonnull Location location, double radius) {
        final World world = location.getWorld();
        final List<LivingEntity> entities = new ArrayList<>();

        if (world == null) {
            return entities;
        }

        world.getNearbyEntities(location, radius, radius, radius)
                .stream()
                .filter(entity -> Utils.isEntityValid(entity, null) && entity instanceof LivingEntity)
                .forEach(entity -> entities.add((LivingEntity) entity));

        return entities;
    }

    /**
     * Gets the nearest living entity to the given location.
     *
     * @param location - Location.
     * @param radius   - Max radius.
     * @param filter   - Filter.
     * @return the nearest living entity; or null if none.
     */
    public static LivingEntity nearestLivingEntity(@Nonnull Location location, double radius, @Nonnull Predicate<LivingEntity> filter) {
        return (LivingEntity) nearestEntityRaw(location, radius, test -> {
            if (!(test instanceof LivingEntity)) {
                return false;
            }

            return filter.test((LivingEntity) test) && Utils.isEntityValid(test, null);
        });
    }

    /**
     * Gets a list with nearby players to the given location.
     *
     * @param location - Location.
     * @param radius   - Max radius.
     * @return a list with nearby players to the given location.
     */
    public static List<Player> nearbyPlayers(@Nonnull Location location, double radius) {
        final World world = location.getWorld();
        final List<Player> players = new ArrayList<>();

        if (world == null) {
            return players;
        }

        world.getNearbyEntities(location, radius, radius, radius)
                .stream()
                .filter(entity -> entity instanceof Player && Manager.current().isPlayerInGame((Player) entity))
                .forEach(player -> players.add((Player) player));

        return players;

    }

    /**
     * Gets the nearest entity to the given location <b>without</b> performing {@link Utils#isEntityValid(Entity)} check.
     * It is <b>not</b> recommended to use this method, because it is missing the important checks.
     *
     * @param location  - Location.
     * @param radius    - Max radius.
     * @param predicate - Filter.
     * @return the nearest entity; or null if none.
     */
    @Nullable
    public static Entity nearestEntityRaw(@Nonnull Location location, double radius, @Nonnull Predicate<Entity> predicate) {
        if (location.getWorld() == null) {
            throw new NullPointerException("unloaded world");
        }

        final List<Entity> list = location.getWorld()
                .getNearbyEntities(location, radius, radius, radius)
                .stream()
                .filter(predicate)
                .toList();

        Entity nearest = null;
        double dist = -1;

        for (Entity entity : list) {
            final double distance = entity.getLocation().distance(location);

            if (nearest == null || distance <= dist) {
                nearest = entity;
                dist = distance;
            }
        }

        return nearest;
    }
}

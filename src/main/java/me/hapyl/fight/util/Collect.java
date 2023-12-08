package me.hapyl.fight.util;

import com.google.common.collect.Lists;
import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import me.hapyl.fight.annotate.ExplicitEntityValidation;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.team.Entry;
import me.hapyl.fight.game.team.GameTeam;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A utility class that allows special case collecting of game entities.
 */
public final class Collect {

    /**
     * Gets the list with game players that are considered enemies to a given player.
     *
     * @param player - Player.
     * @return the list of player's enemies.
     */
    public static List<GamePlayer> enemyPlayers(@Nonnull GamePlayer player) {
        return CF.getAlivePlayers(predicate -> {
            return !predicate.isSpectator() && !predicate.compare(player) && !predicate.isTeammate(player);
        });
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
    public static LivingGameEntity targetEntity(@Nonnull GamePlayer player, double radius, double dot, @Nullable Predicate<LivingGameEntity> predicate) {
        final List<LivingGameEntity> nearbyEntities = nearbyEntities(player.getLocation(), radius);
        final Vector casterDirection = player.getLocation().getDirection().normalize();

        double closestDot = 0.0d;
        LivingGameEntity closestEntity = null;

        for (LivingGameEntity entity : nearbyEntities) {
            // Test Predicate
            if ((!entity.isValid(player)) || (predicate != null && !predicate.test(entity))) {
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
     */
    @ExplicitEntityValidation
    @Nullable
    @Deprecated
    public static Player targetPlayer(@Nonnull GamePlayer player, double maxDistance) {
        return (Player) targetEntity(
                player,
                maxDistance,
                entity -> entity.equals(player)
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
     */
    @ExplicitEntityValidation
    @Nullable
    public static LivingGameEntity targetEntity(@Nonnull GamePlayer player, double maxDistance, @Nonnull Predicate<LivingGameEntity> predicate) {
        final Location location = player.getLocation().add(0, 1.5, 0);
        final Vector vector = location.getDirection().normalize();
        final float radius = 1.25f;

        for (double i = 0; i < maxDistance; i += 0.5d) {
            final double x = vector.getX() * i;
            final double y = vector.getY() * i;
            final double z = vector.getZ() * i;
            location.add(x, y, z);

            for (final LivingGameEntity entity : nearbyEntities(location, radius)) {
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
                entity -> entity instanceof GamePlayer
                        && entity.isNot(exclude)
                        && Manager.current().isPlayerInGame((GamePlayer) entity)
                        && !GameTeam.isTeammate(Entry.of(exclude), Entry.of(entity))
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
    public static LivingGameEntity nearestEntity(@Nonnull Location location, double radius, @Nonnull GamePlayer player) {
        return nearestEntityRaw(location, radius, entity -> {
            if (entity == null) {
                return false;
            }

            return entity.isValid(player);
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
    public static LivingGameEntity nearestEntityPrioritizePlayers(@Nonnull Location location, double radius, @Nonnull Predicate<LivingGameEntity> predicate) {
        final LivingGameEntity nearestPlayer = nearestEntity(
                location,
                radius,
                check -> check.is(Player.class) && predicate.test(check)
        );

        if (nearestPlayer != null) {
            return nearestPlayer;
        }

        return nearestEntity(location, radius, predicate);
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
    public static List<LivingGameEntity> nearbyEntities(@Nonnull GamePlayer player, double radius) {
        return nearbyEntities(player.getLocation(), radius).stream()
                .filter(entity -> !entity.equals(player) && entity.isValid(player))
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
    @Deprecated
    public static List<LivingGameEntity> nearbyLivingEntitiesValidate(@Nonnull Location location, double radius) {
        return nearbyEntities(location, radius); // validated by default
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
    @Deprecated
    public static List<GamePlayer> nearbyPlayersValidate(@Nonnull Location location, double radius) {
        return nearbyPlayers(location, radius); // validated by default
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
    public static List<LivingGameEntity> nearbyEntities(@Nonnull Location location, double radius, @Nonnull Predicate<LivingGameEntity> filter) {
        return nearbyEntities(location, radius).stream().filter(filter).collect(Collectors.toList());
    }

    @ExplicitEntityValidation
    @Nonnull
    public static List<LivingGameEntity> nearbyEntities(@Nonnull World world, @Nonnull BoundingBox boundingBox, @Nonnull Predicate<LivingGameEntity> predicate) {
        final Collection<Entity> entities = world.getNearbyEntities(boundingBox);
        final List<LivingGameEntity> list = Lists.newArrayList();

        entities.forEach(entity -> {
            if (!(entity instanceof LivingEntity livingEntity)) {
                return;
            }

            final LivingGameEntity gameEntity = CF.getEntity(livingEntity);
            if (gameEntity == null || !gameEntity.isValid() || !predicate.test(gameEntity)) {
                return;
            }

            list.add(gameEntity);
        });

        return list;
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
    public static List<LivingGameEntity> nearbyEntities(@Nonnull Location location, double radius) {
        final World world = location.getWorld();
        final List<LivingGameEntity> entities = Lists.newArrayList();

        if (world == null) {
            return entities;
        }

        world.getNearbyEntities(location, radius, radius, radius)
                .stream()
                .filter(entity -> {
                    if (!(entity instanceof LivingEntity living)) {
                        return false;
                    }

                    final LivingGameEntity livingEntity = CF.getEntity(living);

                    if (livingEntity == null) {
                        return false;
                    }

                    final Location entityLocation = livingEntity.getLocation();
                    final double distance = entityLocation.distance(location);

                    return livingEntity.isValid() && distance <= (radius * 2);
                })
                .forEach(entity -> entities.add(CF.getEntity((LivingEntity) entity)));

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
    public static LivingGameEntity nearestEntity(@Nonnull Location location, double radius, @Nonnull Predicate<LivingGameEntity> filter) {
        return nearestEntityRaw(location, radius, test -> {
            return test != null && (filter.test(test) && test.isValid(null));
        });
    }

    /**
     * Gets a list with nearby players to the given location.
     *
     * @param location - Location.
     * @param radius   - Max radius.
     * @return a list with nearby players to the given location.
     */
    public static List<GamePlayer> nearbyPlayers(@Nonnull Location location, double radius) {
        final World world = location.getWorld();
        final List<GamePlayer> players = Lists.newArrayList();

        if (world == null) {
            return players;
        }

        world.getNearbyEntities(location, radius, radius, radius).forEach(entity -> {
            if (!(entity instanceof LivingEntity livingEntity)) {
                return;
            }

            final LivingGameEntity gameEntity = CF.getEntity(livingEntity);
            if (!(gameEntity instanceof GamePlayer player)) {
                return;
            }

            if (Manager.current().isPlayerInGame(player) && validateRadius(player, location, radius)) {
                players.add(player);
            }

        });

        return players;

    }

    @Nonnull
    public static List<GamePlayer> aliveGamePlayers() {
        final Manager manager = Manager.current();
        return manager.getAlivePlayers();
    }

    /**
     * Gets the nearest entity to the given location <b>without</b> performing {@link CFUtils#isEntityValid(Entity)} check.
     * It is <b>not</b> recommended to use this method, because it is missing the important checks.
     *
     * @param location  - Location.
     * @param radius    - Max radius.
     * @param predicate - Filter.
     * @return the nearest entity; or null if none.
     */
    @Nullable
    public static LivingGameEntity nearestEntityRaw(@Nonnull Location location, double radius, @Nonnull Predicate<LivingGameEntity> predicate) {
        if (location.getWorld() == null) {
            throw new NullPointerException("unloaded world");
        }

        final List<Entity> list = location.getWorld()
                .getNearbyEntities(location, radius, radius, radius)
                .stream()
                .filter(e -> {
                    if (e instanceof LivingEntity living) {
                        final LivingGameEntity livingEntity = CF.getEntity(living);
                        return livingEntity != null && predicate.test(livingEntity);
                    }

                    return false;
                })
                .toList();

        LivingEntity nearest = null;
        double dist = -1;

        for (Entity entity : list) {
            if (!(entity instanceof LivingEntity living)) {
                continue;
            }

            final double distance = entity.getLocation().distance(location);

            if (nearest == null || distance <= dist) {
                nearest = living;
                dist = distance;
            }
        }

        return CF.getEntity(nearest);
    }

    private static boolean validateRadius(GamePlayer entity, Location location, double radius) {
        if (entity == null || location == null || radius <= 0.0d) {
            return false;
        }

        final Location entityLocation = entity.getLocation();
        return entityLocation.distance(location) <= radius;
    }
}

package me.hapyl.fight.util;

import com.google.common.collect.Lists;
import me.hapyl.fight.Main;
import me.hapyl.fight.game.*;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.geometry.Quality;
import me.hapyl.spigotutils.module.math.geometry.WorldParticle;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.reflect.Reflect;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Utilities for the plugin
 */
public class Utils {

    public static String colorString(String str, String defColor) {
        final StringBuilder builder = new StringBuilder();
        final String[] strings = str.split(" ");
        for (final String string : strings) {
            if (string.endsWith("%")) {
                builder.append(ChatColor.RED);
            }
            else if (string.endsWith("s") && string.contains("[0-9]")) {
                builder.append(ChatColor.AQUA);
            }
            else {
                builder.append(defColor);
            }
            builder.append(string).append(" ");
        }
        return builder.toString().trim();
    }

    /**
     * Returns List of GamePlayers that are not:
     * Spectator, Teammate nor player itself
     */
    public static List<GamePlayer> getEnemyPlayers(Player player) {
        return Manager.current()
                .getCurrentGame()
                .getAlivePlayers(predicate -> !predicate.isSpectator() && !predicate.compare(player) && !predicate.isTeammate(player));
    }

    public static void setEquipment(LivingEntity entity, Consumer<EntityEquipment> consumer) {
        Nulls.runIfNotNull(entity.getEquipment(), consumer::accept);
    }

    public static double scaleParticleOffset(double v) {
        return v * v / 8.0d;
    }

    @Nonnull
    public static World getWorld(Location location) {
        final World world = location.getWorld();
        if (world == null) {
            throw new NullPointerException("world is unloaded");
        }

        return world;
    }

    public static <E> List<String> collectionToStringList(Collection<E> e, java.util.function.Function<E, String> fn) {
        final List<String> list = new ArrayList<>();
        e.forEach(el -> list.add(fn.apply(el)));
        return list;
    }

    @Nullable
    public static Team getEntityTeam(Entity entity) {
        final Scoreboard scoreboard = Bukkit.getScoreboardManager() == null ? null : Bukkit.getScoreboardManager().getMainScoreboard();
        if (scoreboard == null) {
            return null;
        }

        return getEntityTeam(entity, scoreboard);
    }

    @Nullable
    public static Team getEntityTeam(Entity entity, Scoreboard scoreboard) {
        return scoreboard.getEntryTeam(entity instanceof Player ? entity.getName() : entity.getUniqueId().toString());
    }

    public static void playSoundAndCut(Location location, Sound sound, float pitch, int cutAt) {
        final Set<Player> playingTo = new HashSet<>();
        Manager.current().getCurrentGame().getAlivePlayers().forEach(gp -> {
            final Player player = gp.getPlayer();
            player.playSound(location, sound, SoundCategory.RECORDS, 20f, pitch);
            playingTo.add(player);
        });
        new GameTask() {
            @Override
            public void run() {
                playingTo.forEach(player -> {
                    player.stopSound(sound, SoundCategory.RECORDS);
                });
                playingTo.clear();
            }
        }.runTaskLater(cutAt);
    }

    public static void playSoundAndCut(Player player, Sound sound, float pitch, int cutAt) {
        PlayerLib.playSound(player, sound, pitch);
        new GameTask() {
            @Override
            public void run() {
                player.stopSound(sound, SoundCategory.RECORDS);
            }
        }.runTaskLater(cutAt);
    }

    public static boolean compare(@Nullable Object a, @Nullable Object b) {
        // true if both objects are null
        if (a == null && b == null) {
            return true;
        }
        // false if only one object is null
        if (a == null || b == null) {
            return false;
        }
        return a.equals(b);
    }

    public static <E> void clearCollectionAnd(Collection<E> collection, Consumer<E> action) {
        collection.forEach(action);
        collection.clear();
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        final List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    public static void hidePlayer(Player player) {
        Manager.current().getCurrentGame().getAlivePlayers().forEach(gp -> {
            if (gp.getPlayer() == player || gp.isSpectator() || GameTeam.isTeammate(gp.getPlayer(), player)) {
                return;
            }

            gp.getPlayer().hidePlayer(Main.getPlugin(), player);
        });
    }

    public static void showPlayer(Player player) {
        Manager.current().getCurrentGame().getPlayers().forEach((uuid, gp) -> {
            if (gp.getPlayer() != player) {
                gp.getPlayer().showPlayer(Main.getPlugin(), player);
            }
        });
    }

    public static void hideEntity(Entity entity, Player player) {
        Reflect.hideEntity(entity, player);
    }

    public static void showEntity(Entity entity, Player player) {
        Reflect.showEntity(entity, player);
    }

    public static void rayTracePath(Location start, Location end, double shift, double searchRange, Function<LivingEntity> funcLiving, Function<Location> funcLoc) {
        final double maxDistance = start.distance(end);
        final Vector vector = end.toVector().subtract(start.toVector()).normalize().multiply(shift);

        new GameTask() {
            private double tick = maxDistance;

            @Override
            public void run() {
                if (tick < 0) {
                    this.cancel();
                    return;
                }

                start.add(vector);

                Nulls.runIfNotNull(funcLiving, f -> Utils.getEntitiesInRange(start, searchRange).forEach(f::execute));
                Nulls.runIfNotNull(funcLoc, f -> f.execute(start));

                tick -= shift;

            }
        }.runTaskTimer(0, 1);

    }

    public static void rayTraceLine(Player shooter, double maxDistance, double shift, double damage, @Nullable EnumDamageCause cause, @Nullable Consumer<Location> onMove, @Nullable Consumer<LivingEntity> onHit) {
        final Location location = shooter.getLocation().add(0, 1.5, 0);
        final Vector vector = location.getDirection().normalize();

        main:
        for (double i = 0; i < maxDistance; i += shift) {

            double x = vector.getX() * i;
            double y = vector.getY() * i;
            double z = vector.getZ() * i;
            location.add(x, y, z);

            // check for the hitting a block and an entity
            if (location.getBlock().getType().isOccluding()) {
                break;
            }

            for (final LivingEntity living : getEntitiesInRange(location, 0.5)) {
                if (living == shooter || living instanceof Player player && !Manager.current().isPlayerInGame(player)) {
                    continue;
                }

                if (onHit != null) {
                    onHit.accept(living);
                }

                if (damage > 0.0d) {
                    GamePlayer.damageEntity(living, damage, shooter, cause);
                }
                break main;
            }

            if (i > 1.0) {
                if (onMove != null) {
                    onMove.accept(location);
                }
            }
            location.subtract(x, y, z);

        }
    }

    /**
     * Roots to the actual damager from projectile, owner etc.
     *
     * @param entity the entity to root
     * @return the root damager
     */
    @Nonnull
    public static LivingEntity rootDamager(@Nonnull LivingEntity entity) {
        if (entity instanceof Projectile projectile && projectile.getShooter() instanceof LivingEntity livingEntity) {
            return livingEntity;
        }

        else if (entity instanceof Tameable tameable && tameable.getOwner() instanceof LivingEntity livingEntity) {
            return livingEntity;
        }

        return entity;
    }

    public static Location lerp(Location start, Location end, double percent) {
        return BukkitUtils.newLocation(start).add(end.toVector().subtract(start.toVector()).multiply(percent));
    }

    public static Location lerpf(Location start, Location end, float f) {
        double x = start.getX() + f * (end.getX() - start.getX());
        double y = start.getY() + f * (end.getY() - start.getY());
        double z = start.getZ() + f * (end.getZ() - start.getZ());

        return new Location(start.getWorld(), x, y, z);
    }

    @Nullable
    public static LivingEntity getTargetEntity(Player player, double range, double dot, @Nullable Predicate<LivingEntity> predicate) {
        final List<LivingEntity> nearbyEntities = Utils.getEntitiesInRange(player.getLocation(), range);
        Vector casterDirection = player.getLocation().getDirection().normalize();

        for (LivingEntity entity : nearbyEntities) {
            // Test Predicate
            if ((!isEntityValid(entity, player)) || (predicate != null && !predicate.test(entity))) {
                continue;
            }

            final Vector playerDirection = entity.getLocation().subtract(player.getLocation()).toVector().normalize();

            final double dotProduct = casterDirection.dot(playerDirection);
            final double distance = player.getLocation().distance(entity.getLocation());

            if (dotProduct > dot && distance <= range) {
                return entity;
            }
        }

        return null;
    }

    public static void rayTraceLine(Player shooter, double maxDistance, double shift, double damage, @Nullable Consumer<Location> onMove, @Nullable Consumer<LivingEntity> onHit) {
        rayTraceLine(shooter, maxDistance, shift, damage, null, onMove, onHit);
    }

    public static Response playerCanUseAbility(Player player) {
        final IGamePlayer gp = GamePlayer.getPlayer(player);

        if (gp.hasEffect(GameEffectType.STUN)) {
            return Response.error("Talent is locked!");
        }

        if (gp.hasEffect(GameEffectType.LOCK_DOWN)) {
            return Response.error("Talent is locked! (Lockdown)");
        }

        if (gp.hasEffect(GameEffectType.ARCANE_MUTE)) {
            return Response.error("Unable to use talent! (Arcane Mute)");
        }

        if (Manager.current().isGameInProgress()) {
            final State state = Manager.current().getCurrentGame().getGameState();
            if (state != State.IN_GAME) {
                return Response.error("Game is not yet started!");
            }
        }

        return Response.OK;
    }

    public static Player getTargetPlayer(Player player, double maxDistance) {
        return (Player) getTargetEntity(
                player,
                maxDistance,
                entity -> entity != player && entity instanceof Player p && Manager.current().isPlayerInGame(p)
        );
    }

    @Nullable
    public static LivingEntity getTargetEntity(Player player, double maxDistance, Predicate<LivingEntity> predicate) {
        final Location location = player.getLocation().add(0, 1.5, 0);
        final Vector vector = location.getDirection().normalize();
        final float radius = 1.25f;

        for (double i = 0; i < maxDistance; i += 0.5d) {
            final double x = vector.getX() * i;
            final double y = vector.getY() * i;
            final double z = vector.getZ() * i;
            location.add(x, y, z);

            for (final LivingEntity entity : Utils.getEntitiesInRange(location, radius)) {
                if (!entity.hasLineOfSight(player) || !predicate.test(entity)) {
                    continue;
                }
                return entity;
            }

            location.subtract(x, y, z);
        }

        return null;
    }

    public static <E> void clearCollection(Collection<E> collection) {
        for (final E entry : collection) {
            if (entry == null) {
                continue;
            }
            if (entry instanceof Entity entity) {
                entity.remove();
            }
            if (entry instanceof Block block) {
                block.getState().update(true, false);
            }
        }
        collection.clear();
    }

    public static List<CommandSender> getOnlineOperatorsAndConsole() {
        final List<CommandSender> list = Lists.newArrayList(Bukkit.getConsoleSender());

        Bukkit.getOnlinePlayers().stream().filter(Player::isOp).forEach(list::add);

        return list;
    }

    public static Player getNearestPlayer(Location location, double radius, Player exclude) {
        return (Player) getNearestEntity(location, radius, entity -> {
            return entity instanceof Player && entity != exclude && Manager.current().isPlayerInGame((Player) entity) &&
                    !GameTeam.isTeammate(exclude, (Player) entity);
        });
    }

    public static LivingEntity getNearestLivingEntity(Location location, double radius, Player player) {
        return (LivingEntity) getNearestEntity(location, radius, entity -> {
            if (!(entity instanceof LivingEntity)) {
                return false;
            }

            return isEntityValid(entity, player);
        });
    }

    public static boolean isEntityValid(Entity entity) {
        return isEntityValid(entity, null);
    }

    public static boolean isEntityValid(Entity entity, @Nullable Player player) {
        // null entities, self or armor stands are not valid
        if (entity == null || (player != null && entity == player) || entity instanceof ArmorStand) {
            return false;
        }

        // dead or invisible entities are not valid
        if (entity instanceof LivingEntity livingEntity) {
            if (livingEntity.isDead() || livingEntity.isInvisible()) {
                return false;
            }

            // players are only valid if they are alive and not on the same team
            if (entity instanceof Player targetPlayer) {
                if (Manager.current().isGameInProgress() && !GamePlayer.getPlayer(targetPlayer).isAlive()) {
                    return false;
                }
                return !GameTeam.isTeammate(player, targetPlayer);
            }

            // Dummy check
            if (livingEntity.getScoreboardTags().contains("dummy")) {
                return true;
            }

            return livingEntity.hasAI();
        }

        // other entities are valid
        return true;
    }

    public static List<LivingEntity> getEntitiesInRange(Player player, double range) {
        return getEntitiesInRange(player.getLocation(), range).stream()
                .filter(entity -> entity != player && isEntityValid(entity, player))
                .collect(Collectors.toList());
    }

    // This method gets entities in range but checks for distance to make sure
    public static List<LivingEntity> getEntitiesInRangeValidateRange(Location location, double range) {
        final List<LivingEntity> entities = getEntitiesInRange(location, range);
        entities.removeIf(entity -> entity.getLocation().distance(location) > range);

        return entities;
    }

    // This method gets entities in range but checks for distance to make sure
    public static List<Player> getPlayersInRangeValidateRange(Location location, double range) {
        return getEntitiesInRangeValidateRange(location, range).stream()
                .filter(entity -> entity instanceof Player)
                .map(entity -> (Player) entity)
                .collect(Collectors.toList());
    }

    public static List<LivingEntity> getEntitiesInRange(Location location, double range, Predicate<LivingEntity> filter) {
        return getEntitiesInRange(location, range).stream().filter(filter).collect(Collectors.toList());
    }

    public static List<LivingEntity> getEntitiesInRange(Location location, double range) {
        final World world = location.getWorld();
        final List<LivingEntity> entities = new ArrayList<>();

        if (world == null) {
            return entities;
        }

        world.getNearbyEntities(location, range, range, range)
                .stream()
                .filter(entity -> isEntityValid(entity, null) && entity instanceof LivingEntity)
                .forEach(entity -> entities.add((LivingEntity) entity));

        return entities;
    }

    public static LivingEntity getNearestLivingEntity(Location location, double radius, Predicate<LivingEntity> predicate) {
        return (LivingEntity) getNearestEntity(location, radius, test -> {
            if (!(test instanceof LivingEntity)) {
                return false;
            }

            return predicate.test((LivingEntity) test) && isEntityValid(test, null);
        });
    }

    @Warning(reason = "This does not do any checks")
    public static Entity getNearestEntity(Location fromWhere, double radius, Predicate<Entity> predicate) {
        if (fromWhere.getWorld() == null) {
            throw new NullPointerException("Cannot find entity in null world!");
        }

        final List<Entity> list = fromWhere.getWorld()
                .getNearbyEntities(fromWhere, radius, radius, radius)
                .stream()
                .filter(predicate)
                .toList();

        Entity nearest = null;
        double dist = -1;
        for (Entity entity : list) {
            final double distance = entity.getLocation().distance(fromWhere);
            // init
            if (nearest == null) {
                nearest = entity;
                dist = distance;
            }
            else {
                if (distance <= dist) {
                    nearest = entity;
                    dist = distance;
                }
            }
        }
        return nearest;
    }

    public static List<Player> getPlayersInRange(Location location, double range) {
        final World world = location.getWorld();
        final List<Player> players = new ArrayList<>();

        if (world == null) {
            return players;
        }

        world.getNearbyEntities(location, range, range, range)
                .stream()
                .filter(entity -> entity instanceof Player && Manager.current().isPlayerInGame((Player) entity))
                .forEach(player -> players.add((Player) player));

        return players;

    }

    public static void main(String[] args) {
        System.out.println(colorString("Increased damage by 10% fo 30s.", "&7"));
    }

    public static void createExplosion(Location location, double range, double damage, Consumer<LivingEntity> consumer) {
        createExplosion(location, range, damage, null, null, consumer);
    }

    public static void createExplosion(Location location, double range, double damage, @Nullable LivingEntity damager, @Nullable EnumDamageCause cause) {
        createExplosion(location, range, damage, damager, cause, null);
    }

    public static void createExplosion(Location location, double range, double damage, @Nullable LivingEntity damager, @Nullable EnumDamageCause cause, @Nullable Consumer<LivingEntity> consumer) {
        final World world = location.getWorld();
        if (world == null) {
            return;
        }

        Utils.getEntitiesInRange(location, range).forEach(entity -> {
            if (damage > 0.0d) {
                GamePlayer.damageEntity(entity, damage, damager, cause);
            }
            if (consumer != null) {
                consumer.accept(entity);
            }
        });

        // Fx
        Geometry.drawCircle(location, range, Quality.NORMAL, new WorldParticle(Particle.CRIT));
        Geometry.drawCircle(location, range + 0.5d, Quality.NORMAL, new WorldParticle(Particle.ENCHANTMENT_TABLE));
        PlayerLib.spawnParticle(location, Particle.EXPLOSION_HUGE, 1, 1, 0, 1, 0);
        PlayerLib.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1.0f);
    }

    public static void createExplosion(Location location, double range, double damage) {
        createExplosion(location, range, damage, null);
    }

    public static void lockArmorStand(ArmorStand stand) {
        for (final EquipmentSlot value : EquipmentSlot.values()) {
            for (final ArmorStand.LockType lockType : ArmorStand.LockType.values()) {
                stand.addEquipmentLock(value, lockType);
            }
        }
    }

    public static void unlockArmorStand(ArmorStand stand) {
        for (final EquipmentSlot value : EquipmentSlot.values()) {
            for (final ArmorStand.LockType lockType : ArmorStand.LockType.values()) {
                stand.removeEquipmentLock(value, lockType);
            }
        }
    }

    public static class ProgressBar implements IBuilder<String> {

        private final String indicator;
        private final ChatColor[] colors;
        private int max;

        public ProgressBar(String indicator, ChatColor color, int max) {
            this.indicator = indicator;
            this.colors = new ChatColor[] { ChatColor.GRAY, color };
            this.max = max;
        }

        public ProgressBar(String indicator, ChatColor color) {
            this(indicator, color, 20);
        }

        public ProgressBar(String indicator) {
            this(indicator, ChatColor.GREEN);
        }

        public void setPrimaryColor(ChatColor color) {
            colors[0] = color;
        }

        public void setSecondaryColor(ChatColor color) {
            colors[1] = color;
        }

        public int getMax() {
            return max;
        }

        public void setMax(int max) {
            this.max = max;
        }

        public String build(int value) {
            final StringBuilder builder = new StringBuilder();
            for (int i = 0; i < max; i++) {
                builder.append(value <= i ? getColor(true) : getColor(false));
                builder.append(indicator);
            }
            return builder.toString();
        }

        @Override
        public String build() {
            return build(0);
        }

        private ChatColor getColor(boolean primary) {
            return colors[primary ? 0 : 1];
        }
    }

}

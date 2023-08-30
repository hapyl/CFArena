package me.hapyl.fight.util;

import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.spigotutils.module.annotate.TestedOn;
import me.hapyl.spigotutils.module.annotate.Version;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.geometry.Quality;
import me.hapyl.spigotutils.module.math.geometry.WorldParticle;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.reflect.Reflect;
import net.minecraft.network.protocol.game.PacketPlayOutBlockAction;
import net.minecraft.world.entity.boss.wither.EntityWither;
import net.minecraft.world.level.block.Blocks;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;
import org.joml.Matrix4f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Utilities for the plugin
 */
public class Utils {

    public static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + '&' + "[0-9A-FK-ORX]");
    public static final Object[] DISAMBIGUATE = new Object[] {};

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.0");
    private static final DecimalFormat TICK_FORMAT = new DecimalFormat("0.00");
    private static String SERVER_IP;

    public static String stripColor(String message) {
        message = ChatColor.stripColor(message);
        message = STRIP_COLOR_PATTERN.matcher(message).replaceAll("");

        return message;
    }

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

    public static void setEquipment(LivingEntity entity, /*@IjSg("equipment")*/ Consumer<EntityEquipment> consumer) {
        Nulls.runIfNotNull(entity.getEquipment(), consumer);
    }

    public static double scaleParticleOffset(double v) {
        return v * v / 8.0d;
    }

    /**
     * Gets a loaded world from location or throws an error if the world is null.
     *
     * @param location - Location.
     * @return a loaded world from location or throws an error if the world is null.
     */
    @Nonnull
    public static World getWorld(Location location) {
        final World world = location.getWorld();
        if (world == null) {
            throw new NullPointerException("world is unloaded");
        }

        return world;
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
        CF.getAlivePlayers().forEach(gp -> {
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

    /**
     * Compares 2 objects.
     *
     * <pre>
     *     a && b == null -> TRUE
     *     a || b == null -> FALSE
     *     a == b         -> TRUE
     * </pre>
     *
     * @param a - First object.
     * @param b - Second object.
     * @return if two objects either both null or equal, false otherwise.
     */
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

    /**
     * @deprecated use {@link me.hapyl.fight.game.effect.GameEffectType#INVISIBILITY}
     */
    @Deprecated
    public static void hidePlayer(Player player) {
        CF.getAlivePlayers().forEach(gp -> {
            if (gp.getPlayer() == player || gp.isSpectator() || GameTeam.isTeammate(gp.getPlayer(), player)) {
                return;
            }

            gp.getPlayer().hidePlayer(Main.getPlugin(), player);
        });
    }

    /**
     * @deprecated use {@link me.hapyl.fight.game.effect.GameEffectType#INVISIBILITY}
     */
    @Deprecated
    public static void showPlayer(Player player) {
        CF.getPlayers().forEach(gp -> {
            if (gp.isNot(player)) {
                gp.getPlayer().showPlayer(Main.getPlugin(), player);
            }
        });
    }

    public static void rayTracePath(@Nonnull Location start, @Nonnull Location end, double shift, double searchRange, @Nullable Consumer<LivingGameEntity> funcLiving, @Nullable Consumer<Location> funcLoc) {
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

                Nulls.runIfNotNull(funcLiving, f -> Collect.nearbyEntities(start, searchRange).forEach(f));
                Nulls.runIfNotNull(funcLoc, f -> f.accept(start));

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

            for (final LivingGameEntity gameEntity : Collect.nearbyEntities(location, 1.0)) {
                if (gameEntity.is(shooter)) {
                    continue;
                }

                if (onHit != null) {
                    onHit.accept(gameEntity.getEntity());
                }

                if (damage > 0.0d) {
                    gameEntity.damage(damage, CF.getEntity(shooter), cause);
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

    public static void rayTraceLine(Player shooter, double maxDistance, double shift, double damage, @Nullable Consumer<Location> onMove, @Nullable Consumer<LivingEntity> onHit) {
        rayTraceLine(shooter, maxDistance, shift, damage, null, onMove, onHit);
    }

    /**
     * Performs a "smart" collection clear, with removing entities and updating block states.
     *
     * @param collection - Collection.
     */
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

    @Nonnull
    public static <K, V> V getElementOrThrowErrorIfNull(Map<K, V> map, K key, String errorMessage) {
        final V v = map.get(key);

        if (v != null) {
            return v;
        }

        throw new IllegalArgumentException(errorMessage);
    }

    @TestedOn(version = Version.V1_20)
    public static void setWitherInvul(Wither wither, int invul) {
        //Reflect.setDataWatcherValue(
        //        Objects.requireNonNull(Reflect.getMinecraftEntity(wither)),
        //        DataWatcherType.INT,
        //        19,
        //        invul,
        //        Bukkit.getOnlinePlayers().toArray(new Player[] {})
        //);
        ((EntityWither) Objects.requireNonNull(Reflect.getMinecraftEntity(wither))).s(invul);
    }

    /**
     * Forces entity to look at provided location.
     *
     * @param entity - Entity.
     * @param at     - Look at.
     */
    public static void lookAt(@Nonnull LivingEntity entity, @Nonnull Location at) {
        final Vector dirBetweenLocations = at.toVector().subtract(entity.getLocation().toVector());
        final Location location = entity.getLocation();

        location.setDirection(dirBetweenLocations);
        entity.teleport(location);
    }

    public static boolean isEntityValid(Entity entity) {
        return isEntityValid(entity, null);
    }

    /**
     * Performs an entity check to validate if the entity is considered "valid."
     *
     * @param entity - Entity to check.
     * @param player - Player if team check is needed.
     * @return true if entity is "valid," false otherwise.
     */
    public static boolean isEntityValid(Entity entity, @Nullable Player player) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return false;
        }

        final LivingGameEntity gameEntity = CF.getEntity(livingEntity);
        if (gameEntity == null) {
            return false;
        }

        return gameEntity.isValid(player);
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

        Collect.nearbyEntities(location, range).forEach(entity -> {
            if (damage > 0.0d) {
                entity.damage(damage, CF.getEntity(damager), cause);
            }
            if (consumer != null) {
                consumer.accept(entity.getEntity());
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

    public static <E> E fetchFirstFromLinkedMap(LinkedHashMap<?, E> map, E def) {
        for (E value : map.values()) {
            return value;
        }

        return def;
    }

    public static <E> List<String> collectionToStringList(Collection<E> e, java.util.function.Function<E, String> fn) {
        final List<String> list = new ArrayList<>();
        e.forEach(el -> list.add(fn.apply(el)));
        return list;
    }

    @Nonnull
    public static String getServerIp() {
        if (SERVER_IP == null) {
            String ip = Bukkit.getIp();
            final int port = Bukkit.getPort();

            if (ip.isEmpty() || ip.isBlank()) {
                try {
                    ip = InetAddress.getLocalHost().getHostAddress();
                } catch (Exception ignored0) {
                    try {
                        ip = Inet4Address.getLocalHost().getHostAddress();
                    } catch (Exception ignored1) {
                        try {
                            ip = Inet6Address.getLocalHost().getHostAddress();
                        } catch (Exception ignored2) {
                            SERVER_IP = "Unavailable";
                            return SERVER_IP;
                        }
                    }
                }
            }

            SERVER_IP = ip + ":" + port;
        }

        return SERVER_IP;
    }

    public static String formatTick(int tick) {
        return DECIMAL_FORMAT.format(tick / 20L);
    }

    public static void modifyKnockback(@Nonnull LivingEntity target, @Nonnull Function<Double, Double> fn, @Nonnull Consumer<LivingEntity> consumer) {
        final AttributeInstance attribute = target.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE);

        if (attribute == null) {
            consumer.accept(target);
            Debug.warn("%s does not have GENERIC_KNOCKBACK_RESISTANCE");
            return;
        }

        final double base = attribute.getBaseValue();
        final double newValue = fn.apply(base);

        attribute.setBaseValue(newValue);
        consumer.accept(target);
        attribute.setBaseValue(base);
    }

    public static Matrix4f parseMatrix(@Range(min = 16, max = 16) float... matrix) {
        if (matrix.length != 16) {
            throw new IllegalArgumentException("matrix length must be 16, not " + matrix.length);
        }

        return new Matrix4f(
                matrix[0], matrix[4], matrix[8], matrix[12],
                matrix[1], matrix[5], matrix[9], matrix[13],
                matrix[2], matrix[6], matrix[10], matrix[14],
                matrix[3], matrix[7], matrix[11], matrix[15]
        );
    }

    public static void playChestAnimation(Block block, boolean open) {
        net.minecraft.world.level.block.Block nmsBlock = switch (block.getType()) {
            case CHEST -> Blocks.cv;
            case TRAPPED_CHEST -> Blocks.gV;
            case ENDER_CHEST -> Blocks.fG;
            default -> throw new IllegalArgumentException("invalid chest type: " + block.getType());
        };

        final PacketPlayOutBlockAction packet = new PacketPlayOutBlockAction(
                Reflect.getBlockPosition(block),
                nmsBlock,
                1,
                open ? 1 : 0
        );

        Bukkit.getOnlinePlayers().forEach(player -> Reflect.sendPacket(player, packet));
    }

    @Nonnull
    public static String decimalFormat(int tick) {
        return TICK_FORMAT.format(tick / 20.0d) + "s";
    }

    // Anchors location to the ground
    public static Location anchorLocation(@Nonnull Location location) {
        final World world = location.getWorld();
        if (world == null) {
            return location;
        }

        final int minHeight = world.getMinHeight();

        while (true) {
            final double y = location.getY();
            if (y <= minHeight || location.getBlock().getType().isOccluding()) {
                Debug.particle(location, Particle.VILLAGER_HAPPY);
                return location;
            }

            location.subtract(0.0d, 0.1d, 0.0d);
        }
    }

}

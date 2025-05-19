package me.hapyl.fight.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import me.hapyl.eterna.module.annotate.TestedOn;
import me.hapyl.eterna.module.annotate.Version;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.math.Cuboid;
import me.hapyl.eterna.module.math.Geometry;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.math.geometry.Quality;
import me.hapyl.eterna.module.math.geometry.WorldParticle;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.reflect.Reflect;
import me.hapyl.fight.CF;
import me.hapyl.fight.Main;
import me.hapyl.fight.annotate.ClonedBeforeMutation;
import me.hapyl.fight.game.Constants;
import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.task.GameTask;
import net.minecraft.network.protocol.game.PacketPlayOutBlockAction;
import net.minecraft.world.entity.boss.wither.EntityWither;
import net.minecraft.world.level.block.Blocks;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Range;
import org.joml.Matrix4f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Utilities for the plugin
 */
public class CFUtils {
    
    public static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + '&' + "[0-9A-FK-ORX]");
    public static final double ANGLE_IN_RAD = 6.283185307179586d;
    
    public static final String INF_CHAR = "∞";
    public static final String RIGHT_CLICK = " &8(%sRight Click&8)".formatted(Color.BUTTON.bold());
    
    private static final DecimalFormat TICK_FORMAT = new DecimalFormat("0.0");
    private static final Random RANDOM = new Random();
    
    private static String SERVER_IP;
    
    /**
     * Strips the color from the given message.
     *
     * @param message - Message.
     * @return the uncolored message.
     */
    @Nonnull
    public static String stripColor(@Nonnull String message) {
        message = ChatColor.stripColor(message);
        message = STRIP_COLOR_PATTERN.matcher(message).replaceAll("");
        
        return message;
    }
    
    /**
     * Sets the {@link LivingEntity}'s equipment.
     * <p>
     * Because {@link LivingEntity#getEquipment()} is nullable for some reason.
     *
     * @param entity   - Entity.
     * @param consumer - Consumer.
     */
    public static void setEquipment(@Nonnull LivingEntity entity, /*@IjSg("equipment")*/ @Nonnull Consumer<EntityEquipment> consumer) {
        Nulls.runIfNotNull(entity.getEquipment(), consumer);
    }
    
    /**
     * Scales the particle offset to be a 1:1 block ratio.
     *
     * @param v - offset.
     * @return the scaled offset.
     */
    public static double scaleParticleOffset(double v) {
        return v * v / 8.0d;
    }
    
    /**
     * Gets the {@link World} from the given {@link Location}, or throws {@link IllegalStateException} if the world is unloaded.
     *
     * @param location - Location.
     * @return the world from the given location, or throws an error if the world is unloaded.
     */
    @Nonnull
    public static World getWorld(@Nonnull Location location) {
        final World world = location.getWorld();
        
        if (world == null) {
            throw new IllegalStateException("unloaded world");
        }
        
        return world;
    }
    
    /**
     * Gets the {@link Team} the given {@link Entity} belongs to in the main scoreboard; or null if there are none.
     *
     * @param entity - Entity.
     * @return the team the given entity belongs to in the main scoreboard; or null if there are none.
     */
    @Nullable
    public static Team getEntityTeam(@Nonnull Entity entity) {
        final Scoreboard scoreboard = Bukkit.getScoreboardManager() == null ? null : Bukkit.getScoreboardManager().getMainScoreboard();
        
        if (scoreboard == null) {
            return null;
        }
        
        return getEntityTeam(entity, scoreboard);
    }
    
    /**
     * Gets the {@link Team} the given {@link Entity} belongs to in the given scoreboard; or null if there are none.
     *
     * @param entity     - Entity.
     * @param scoreboard - Scoreboard.
     * @return the team the given entity belongs to in the given scoreboard; or null if there are none.
     */
    @Nullable
    public static Team getEntityTeam(@Nonnull Entity entity, @Nonnull Scoreboard scoreboard) {
        return scoreboard.getEntryTeam(entity instanceof Player ? entity.getName() : entity.getUniqueId().toString());
    }
    
    public static void playSoundAndCut(Location location, Sound sound, float pitch, int cutAt) {
        final Set<Player> playingTo = new HashSet<>();
        CF.getAlivePlayers().forEach(gp -> {
            final Player player = gp.getEntity();
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
    
    public static void hidePlayer(@Nonnull GamePlayer player) {
        CF.getAlivePlayers().forEach(gamePlayer -> {
            if (gamePlayer.equals(player) || gamePlayer.isSpectator() || gamePlayer.isTeammate(player)) {
                return;
            }
            
            player.hide(gamePlayer.getEntity());
        });
    }
    
    public static void showPlayer(@Nonnull GamePlayer player) {
        CF.getPlayers().forEach(gamePlayer -> {
            if (gamePlayer.equals(player)) {
                return;
            }
            
            player.show(gamePlayer.getEntity());
        });
    }
    
    public static void rayTracePath(
            @Nonnull Location start,
            @Nonnull Location end,
            double shift,
            double searchRange,
            @Nullable Consumer<LivingGameEntity> funcLiving,
            @Nullable Consumer<Location> funcLoc
    ) {
        final double maxDistance = start.distance(end);
        final Vector vector = end.toVector().subtract(start.toVector()).normalize().multiply(shift);
        
        new GameTask() {
            private double tick = maxDistance;
            
            @Override
            public void run() {
                if (tick < 0) {
                    cancel();
                    return;
                }
                
                start.add(vector);
                
                Nulls.runIfNotNull(funcLiving, f -> Collect.nearbyEntities(start, searchRange).forEach(f));
                Nulls.runIfNotNull(funcLoc, f -> f.accept(start));
                
                tick -= shift;
            }
        }.runTaskTimer(0, 1);
        
    }
    
    public static void rayTraceLine(
            GamePlayer shooter,
            double maxDistance,
            double shift,
            double damage,
            @Nullable DamageCause cause,
            @Nullable Consumer<Location> onMove,
            @Nullable Consumer<LivingGameEntity> onHit
    ) {
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
                if (gameEntity.equals(shooter)) {
                    continue;
                }
                
                if (onHit != null) {
                    onHit.accept(gameEntity);
                }
                
                if (damage > 0.0d) {
                    gameEntity.damage(damage, shooter, cause);
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
    
    public static void rayTraceLine(GamePlayer shooter, double maxDistance, double shift, double damage, @Nullable Consumer<Location> onMove, @Nullable Consumer<LivingGameEntity> onHit) {
        rayTraceLine(shooter, maxDistance, shift, damage, null, onMove, onHit);
    }
    
    /**
     * Performs a "smart" collection clear, with removing entities and updating block states.
     *
     * @param collection - Collection.
     */
    public static <E> void clearCollection(Collection<E> collection) {
        for (final E entry : collection) {
            doClearEntry(entry);
        }
        collection.clear();
    }
    
    public static <E> void clearArray(E[] array) {
        for (E t : array) {
            doClearEntry(t);
        }
    }
    
    @Nonnull
    public static <K, V> V getElementOrThrowErrorIfNull(Map<K, V> map, K key, String errorMessage) {
        final V v = map.get(key);
        
        if (v != null) {
            return v;
        }
        
        throw new IllegalArgumentException(errorMessage);
    }
    
    @TestedOn(version = Version.V1_21_3)
    public static void setWitherInvul(Wither wither, int invul) {
        ((EntityWither) Objects.requireNonNull(Reflect.getMinecraftEntity(wither))).b(invul);
    }
    
    /**
     * Forces entity to look at provided location.
     *
     * @param entity - Entity.
     * @param at     - Look at.
     */
    public static void lookAt(@Nonnull Entity entity, @Nonnull Location at) {
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
    public static boolean isEntityValid(Entity entity, @Nullable GamePlayer player) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return false;
        }
        
        final LivingGameEntity gameEntity = CF.getEntity(livingEntity);
        if (gameEntity == null) {
            return false;
        }
        
        return gameEntity.isValid(player);
    }
    
    public static void createExplosion(Location location, double range, double damage, Consumer<LivingGameEntity> consumer) {
        createExplosion(location, range, damage, null, null, consumer);
    }
    
    public static void createExplosion(Location location, double range, double damage, @Nullable LivingGameEntity damager, @Nullable DamageCause cause) {
        createExplosion(location, range, damage, damager, cause, null);
    }
    
    @Deprecated
    public static void createExplosion(Location location, double range, double damage, @Nullable LivingGameEntity damager, @Nullable DamageCause cause, @Nullable Consumer<LivingGameEntity> consumer) {
        final World world = location.getWorld();
        if (world == null) {
            return;
        }
        
        Collect.nearbyEntities(location, range).forEach(entity -> {
            if (damage > 0.0d) {
                entity.damage(damage, damager, cause);
            }
            if (consumer != null) {
                consumer.accept(entity);
            }
        });
        
        // Fx
        Geometry.drawCircle(location, range, Quality.NORMAL, new WorldParticle(Particle.CRIT));
        Geometry.drawCircle(location, range + 0.5d, Quality.NORMAL, new WorldParticle(Particle.ENCHANT));
        PlayerLib.spawnParticle(location, Particle.EXPLOSION_EMITTER, 1, 1, 0, 1, 0);
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
                }
                catch (Exception ignored0) {
                    try {
                        ip = Inet4Address.getLocalHost().getHostAddress();
                    }
                    catch (Exception ignored1) {
                        try {
                            ip = Inet6Address.getLocalHost().getHostAddress();
                        }
                        catch (Exception ignored2) {
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
    
    public static void modifyKnockback(@Nonnull LivingEntity target, @Nonnull Function<Double, Double> fn, @Nonnull Consumer<LivingEntity> consumer) {
        final AttributeInstance attribute = target.getAttribute(Attribute.KNOCKBACK_RESISTANCE);
        
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
    
    public static Matrix4f parseMatrix(@Range(from = 16, to = 16) float... matrix) {
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
    public static String formatTick(int tick) {
        return tick > Constants.MAX_COOLDOWN || tick == Constants.INFINITE_DURATION ? INF_CHAR : Tick.format(tick);
    }
    
    @Nonnull
    public static String decimalFormatTick(int tick) {
        return tick > Constants.MAX_COOLDOWN ? INF_CHAR : decimalFormat(tick / 20d) + "s";
    }
    
    @Nonnull
    public static String decimalFormat(double number) {
        return TICK_FORMAT.format(number);
    }
    
    public static void setGlowing(@Nonnull Player player, @Nonnull Entity entity, @Nonnull String teamName, @Nonnull ChatColor color) {
        final Scoreboard scoreboard = player.getScoreboard();
        Team team = scoreboard.getTeam(teamName);
        
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
        }
        
        team.setColor(color);
        team.addEntry(entity instanceof Player playerEntity ? playerEntity.getName() : entity.getUniqueId().toString());
    }
    
    public static <T> void forEach(T[] array, Consumer<T> consumer) {
        for (T t : array) {
            if (t == null) {
                continue;
            }
            
            consumer.accept(t);
        }
    }
    
    public static void modifyAttribute(LivingEntity entity, Attribute attribute, Consumer<AttributeInstance> consumer) {
        final AttributeInstance instance = entity.getAttribute(attribute);
        
        if (instance != null) {
            consumer.accept(instance);
        }
    }
    
    public static double getAttributeValue(LivingEntity entity, Attribute attribute) {
        final AttributeInstance instance = entity.getAttribute(attribute);
        
        if (instance == null) {
            return 0.0d;
        }
        
        return instance.getBaseValue();
    }
    
    public static void setAttributeValue(LivingEntity entity, Attribute attribute, double value) {
        final AttributeInstance instance = entity.getAttribute(attribute);
        
        if (instance == null) {
            return;
        }
        
        instance.setBaseValue(value);
    }
    
    /**
     * Calculates a dot product between two {@link Location}.
     *
     * @param start - Start.
     * @param end   - End.
     * @return the dot product between locations.
     */
    public static double dot(@Nonnull Location start, @Nonnull @ClonedBeforeMutation Location end) {
        final Vector vector = end.clone().subtract(start).toVector().normalize();
        
        return start.getDirection().normalize().dot(vector);
    }
    
    /**
     * Calculates a dot product between two {@link Location}.
     *
     * @param start    - Start.
     * @param end      - End.
     * @param distance - Distance to check.
     * @return the dot product if the distance between locations is <code><=</code> <code>distance</code>; -1 otherwise.
     */
    public static double dot(@Nonnull Location start, @Nonnull @ClonedBeforeMutation Location end, double distance) {
        if (start.distance(end) < distance) {
            return -1.0d;
        }
        
        return dot(start, end);
    }
    
    /**
     * Calculates a dot product between two {@link Location} and checks if it is greater or equals <code>>=</code> to the parameter.
     *
     * @param start    - Start.
     * @param end      - End.
     * @param dot      - Dot to match.
     * @param distance - Distance to check.
     * @return true if the dot product is <code>>=</code> <code>dot</code> and <code>distance</code> is <code><=</code> between location.
     */
    public static boolean dot(@Nonnull Location start, @Nonnull @ClonedBeforeMutation Location end, float dot, double distance) {
        final double theDot = dot(start, end, distance);
        
        return theDot >= dot;
    }
    
    /**
     * Gets the {@link ItemStack} display name; or an empty string is there is no meta.
     * <p>
     * This method <b>stripes</b> the color from a name if there is any.
     *
     * @param item - Item.
     * @return item's display name.
     */
    @Nonnull
    public static String getItemName(@Nonnull ItemStack item) {
        final ItemMeta itemMeta = item.getItemMeta();
        
        if (itemMeta == null) {
            return "";
        }
        
        return ChatColor.stripColor(itemMeta.getDisplayName());
    }
    
    /**
     * Returns a random double between origin and bound, either positive or negavite.
     *
     * @param origin - Origin.
     * @param bound  - Bound (exclusive).
     * @return a random positive or negative bound.
     */
    public static double randomAxis(double origin, double bound) {
        final double d = random(origin, bound);
        return RANDOM.nextBoolean() ? d : -d;
    }
    
    /**
     * Gets a random double between origin and bound (exclusive).
     *
     * @param origin - Origin.
     * @param bound  - Bound (exclusive).
     * @return a random double.
     */
    public static double random(double origin, double bound) {
        return RANDOM.nextDouble(origin, bound);
    }
    
    public static float random(float origin, float bound) {
        return RANDOM.nextFloat(origin, bound);
    }
    
    /**
     * Gets a random {@link EulerAngle}.
     *
     * @return a random EulerAngle.
     */
    @Nonnull
    public static EulerAngle randomEulerAngle() {
        return new EulerAngle(random(0.0d, ANGLE_IN_RAD), random(0.0d, ANGLE_IN_RAD), random(0.0d, ANGLE_IN_RAD));
    }
    
    /**
     * Center the location based on its block coordinates.
     * This does not center the <code>Y</code> coordinate.
     *
     * @param location - Location to center.
     * @return a new, centered location.
     */
    @Nonnull
    public static Location centerLocation(@Nonnull Location location) {
        return new Location(
                location.getWorld(),
                location.getBlockX() + 0.5d,
                location.getY(),
                location.getBlockZ() + 0.5d,
                location.getYaw(),
                location.getPitch()
        );
    }
    
    /**
     * Creates an exact copy of the given list.
     *
     * @param list - List to copy from.
     * @return an exact copy of the list.
     */
    @Nonnull
    public static <T> List<T> copyList(@Nonnull List<T> list) {
        return Lists.newArrayList(list);
    }
    
    /**
     * Returns true is the material a slab.
     *
     * @param material - Material.
     * @return true if the material is a blab.
     */
    public static boolean isBlockSlab(@Nonnull Material material) {
        if (!material.isBlock()) {
            return false;
        }
        
        return material.name().endsWith("_SLAB");
    }
    
    /**
     * Performs an entry clear based in the entry type.
     *
     * @param e - Entry.
     */
    public static <E> void doClearEntry(@Nullable E e) {
        switch (e) {
            case Entity entity -> entity.remove();
            case GameEntity entity -> entity.remove();
            case Block block -> block.getState().update(true, false);
            case null, default -> {
            }
        }
        
    }
    
    @Nullable
    public static URL urlFromString(String url) {
        try {
            return new URL(url);
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public static <T> Collection<T> computeCollection(@Nonnull Collection<T> collection, @Nonnull T t, boolean add) {
        if (add) {
            collection.add(t);
        }
        else {
            collection.remove(t);
        }
        
        return collection;
    }
    
    public static void validateVarArgs(Object[] objects) {
        if (objects == null || objects.length == 0) {
            throw new IllegalArgumentException("there must be at least one var arg!");
        }
    }
    
    public static int divide(int a, int b) {
        return (int) ((double) (a / b));
    }
    
    @Nonnull
    public static <K> Cache<K, Boolean> createCache(long expireAfter) {
        return CacheBuilder.newBuilder()
                           .expireAfterWrite(expireAfter, TimeUnit.MILLISECONDS)
                           .build(new CacheLoader<K, Boolean>() {
                               @Nonnull
                               @Override
                               public Boolean load(@Nonnull K key) throws Exception {
                                   return true;
                               }
                           });
    }
    
    /**
     * Gets a copy of a mapped list; or empty list.
     *
     * @param hashMap - Hash map.
     * @param key     - Key.
     * @return a copy of a mapped list.
     */
    @Nonnull
    public static <K, V> List<V> copyMapList(@Nonnull Map<K, List<V>> hashMap, @Nonnull K key) {
        final List<V> list = hashMap.get(key);
        
        return list != null ? new ArrayList<>(list) : new ArrayList<>();
    }
    
    @Nonnull
    public static <K, V> List<V> immutableMapListCopy(@Nonnull Map<K, List<V>> hashMap, @Nonnull K key) {
        return Collections.unmodifiableList(hashMap.getOrDefault(key, Lists.newArrayList()));
    }
    
    /**
     * Increments an integer if the <code>condition</code> is <code>true</code> or sets to <code>min</code> otherwise.
     *
     * @param integer    - Integer.
     * @param condition- Condition.
     * @param min        - Min.
     * @param max        - Max
     * @return <code>min(int+1, max)</code> if the condition is met; <code>min</code> otherwise.
     */
    public static int incrementIntegerConditionallyAndClamp(int integer, boolean condition, int min, int max) {
        return condition ? Math.min(integer + 1, max) : min;
    }
    
    public static <T> T[] requireVarArgs(@Nullable T[] varArgs) {
        if (varArgs == null || varArgs.length == 0) {
            throw new IllegalArgumentException("VarArgs must not be null nor empty!");
        }
        
        return varArgs;
    }
    
    public static <T> void removeIf(@Nonnull Collection<T> collection, @Nonnull Predicate<T> test, @Nonnull Consumer<T> accept) {
        final Iterator<T> iterator = collection.iterator();
        
        while (iterator.hasNext()) {
            final T next = iterator.next();
            
            if (test.test(next)) {
                accept.accept(next);
                iterator.remove();
            }
        }
    }
    
    public static void later(@Nonnull Runnable runnable, int delay) {
        new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskLater(CF.getPlugin(), delay);
    }
    
    public static void dumpStackTrace() {
        final RuntimeException exception = new RuntimeException();
        final StackTraceElement[] stackTrace = exception.getStackTrace();
        final Deque<String> deque = Queues.newArrayDeque();
        final String pluginName = Main.getPlugin().getDescription().getName();
        
        for (int i = stackTrace.length - 1; i >= 0; i--) {
            final StackTraceElement trace = stackTrace[i];
            final String classLoaderName = trace.getClassLoaderName();
            
            if (classLoaderName == null
                    || !classLoaderName.contains(pluginName + ".jar")
            ) {
                continue;
            }
            
            String fileName = trace.getFileName();
            
            if (fileName != null) {
                fileName = fileName.replace(".java", "");
            }
            
            final String methodName = trace.getMethodName();
            deque.offer(fileName + "." + methodName + ":" + trace.getLineNumber());
        }
        
        final StrBuilder builder = new StrBuilder();
        
        int index = 0;
        for (String string : deque) {
            if (index != 0) {
                builder.append("\n");
            }
            
            builder.append(index % 2 == 0 ? ChatColor.BLUE : ChatColor.RED);
            
            if (index != deque.size() - 1) {
                builder.append("├─");
            }
            else {
                builder.append("└─");
            }
            
            builder.append(string);
            
            index++;
        }
        
        Bukkit.getOnlinePlayers().stream().filter(Player::isOp).forEach(player -> {
            Chat.sendHoverableMessage(
                    player,
                    builder.toString(),
                    "&c&lDEBUG &e" + deque.pollFirst() + "Dumped StackTrace. &6&lHOVER"
            );
        });
    }
    
    public static String cuboidToString(@Nonnull Cuboid cuboid) {
        return "[%s, %s, %s, %s, %s, %s]".formatted(
                cuboid.getMinX(),
                cuboid.getMinY(),
                cuboid.getMinZ(),
                cuboid.getMaxX(),
                cuboid.getMaxY(),
                cuboid.getMaxZ()
        );
    }
    
    public static String cuboidToString(@Nonnull BoundingBox cuboid) {
        return "[%s, %s, %s, %s, %s, %s]".formatted(
                cuboid.getMinX(),
                cuboid.getMinY(),
                cuboid.getMinZ(),
                cuboid.getMaxX(),
                cuboid.getMaxY(),
                cuboid.getMaxZ()
        );
    }
    
    public static double distance(@Nonnull Location a, @Nonnull Location b) {
        return distance0(a, b, Location::distance);
    }
    
    public static double distanceSquared(@Nonnull Location a, @Nonnull Location b) {
        return distance0(a, b, Location::distanceSquared);
    }
    
    public static void globalBlockChange(@Nonnull Location location, @Nonnull BlockData data) {
        Bukkit.getOnlinePlayers().forEach(player -> player.sendBlockChange(location, data));
    }
    
    public static boolean wasPreviousLowerThanValueAndCurrentIsHigherOrEqualsToValue(double previous, double current, double value) {
        return previous < value && current >= value;
    }
    
    @Nonnull
    public static String toWordCount(int i) {
        return switch (i) {
            case 1 -> "once";
            case 2 -> "twice";
            case 3 -> "thrice";
            case 4 -> "four times";
            case 5 -> "five times";
            case 6 -> "six times";
            case 7 -> "seven times";
            case 8 -> "eight times";
            case 9 -> "nine times";
            case 10 -> "ten times";
            default -> i + " times";
        };
    }
    
    @Nonnull
    public static String toWord(int i) {
        return switch (i) {
            case 1 -> "one";
            case 2 -> "two";
            case 3 -> "three";
            case 4 -> "four";
            case 5 -> "five";
            case 6 -> "six";
            case 7 -> "seven";
            case 8 -> "eight";
            case 9 -> "nine";
            case 10 -> "ten";
            default -> "unknown";
        };
    }
    
    private static double distance0(Location a, Location b, BiFunction<Location, Location, Double> fn) {
        final World aWorld = a.getWorld();
        final World bWorld = b.getWorld();
        
        if (!Objects.equals(aWorld, bWorld)) {
            return 0.0;
        }
        
        return fn.apply(a, b);
    }
    
    @Nonnull
    public static <K, V, R> Set<R> fetchKeySet(@Nonnull Map<K, V> hashMap, @Nonnull Class<R> clazz) {
        return Helper.fetchFromMap(hashMap, clazz, true);
    }
    
    @Nonnull
    public static <K, V, R> Set<R> fetchValues(@Nonnull Map<K, V> hashMap, @Nonnull Class<R> clazz) {
        return Helper.fetchFromMap(hashMap, clazz, false);
    }
    
    public static void offsetLocation(@Nonnull Location location, double x, double y, double z, @Nonnull Runnable then) {
        location.add(x, y, z);
        then.run();
        location.subtract(x, y, z);
    }
    
    public static void offsetLocation(@Nonnull Location location, double y, @Nonnull Runnable runnable) {
        offsetLocation(location, 0, y, 0, runnable);
    }
    
    @Nonnull
    public static <T> T returnOrThrow(@Nullable T t, @Nonnull String errorMessage) {
        if (t != null) {
            return t;
        }
        
        throw new IllegalArgumentException(errorMessage);
    }
    
    public static boolean arrayContains(Object[] array, Object containingElement) {
        if (array == null) {
            return false;
        }
        
        for (Object e : array) {
            if (Objects.equals(e, containingElement)) {
                return true;
            }
        }
        
        return false;
    }
    
    @SafeVarargs
    @Nonnull
    public static <E extends Enum<E>> List<E> enumSubList(@Nonnull Class<E> clazz, int startIndex, int maxSize, @Nullable E... ignore) {
        if (startIndex < 0) {
            throw new IllegalArgumentException("Start index cannot be negative.");
        }
        
        final List<E> list = new ArrayList<>();
        final E[] enumConstants = clazz.getEnumConstants();
        
        for (int i = 0; i < maxSize; i++) {
            if (startIndex + i >= enumConstants.length) {
                return list;
            }
            
            final E e = enumConstants[startIndex + i];
            
            if (arrayContains(ignore, e)) {
                continue;
            }
            
            list.add(e);
        }
        
        return list;
    }
    
    @Nullable
    public static <T> Method getMethod(@Nonnull Class<T> clazz, @Nonnull String methodName) {
        try {
            return clazz.getMethod(methodName);
        }
        catch (Exception e) {
            try {
                return clazz.getDeclaredMethod(methodName);
            }
            catch (Exception e2) {
                return null;
            }
        }
    }
    
    /**
     * Combines all the given {@link ChatColor} and prepends them before the name.
     *
     * @param name   - Name.
     * @param colors - Colors.
     * @return colored string.
     */
    @Nonnull
    public static String combineColors(@Nonnull String name, @Nonnull ChatColor... colors) {
        if (colors.length == 0) {
            throw new IllegalArgumentException("There must be at least one color!");
        }
        
        final StrBuilder builder = new StrBuilder();
        
        for (ChatColor color : colors) {
            builder.append(color);
        }
        
        return builder + name;
    }
    
    @Nonnull
    public static <T> T castNullable(@Nullable Object object, @Nonnull Class<T> cast) throws IllegalArgumentException {
        if (object == null) {
            throw new IllegalArgumentException("Cannot cast null object.");
        }
        
        return cast.cast(object);
    }
    
    @Nullable
    public static Entity getEntityById(int entityId) {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.getEntityId() == entityId) {
                    return entity;
                }
            }
        }
        
        return null;
    }
    
    public static void showEntity(@Nonnull LivingEntity entity) {
        Bukkit.getOnlinePlayers().forEach(player -> player.showEntity(CF.getPlugin(), entity));
    }
    
    @Nonnull
    public static String strikethroughText(@Nonnull ChatColor color) {
        return (color + "&m ").repeat(77);
    }
    
    private static class Helper {
        private static <K, V, R> Set<R> fetchFromMap(Map<K, V> hashMap, Class<R> clazz, boolean b) {
            Collection<?> collection = b ? hashMap.keySet() : hashMap.values();
            Set<R> hashSet = Sets.newHashSet();
            
            collection.forEach(obj -> {
                if (clazz.isInstance(obj)) {
                    hashSet.add(clazz.cast(obj));
                }
            });
            
            return hashSet;
        }
    }
    
    
}

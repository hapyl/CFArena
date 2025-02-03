package me.hapyl.fight;

import me.hapyl.eterna.module.command.CommandProcessor;
import me.hapyl.eterna.module.command.SimpleCommand;
import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.reflect.Reflect;
import me.hapyl.fight.anticheat.AntiCheat;
import me.hapyl.fight.config.Environment;
import me.hapyl.fight.database.Database;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.crate.CrateManager;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.ConsumerFunction;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.quest.CFQuestHandler;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.vehicle.VehicleManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.logging.Logger;

/**
 * This is a more of a "user-friendly" way of getting stuff, like {@link org.bukkit.Bukkit}.
 */
public final class CF {

    static Main plugin;
    static Manager manager;
    static CommandProcessor commandProcessor;

    private CF() {
    }

    /**
     * Gets the main class of the plugin.
     *
     * @return the main class of the plugin.
     */
    @Nonnull
    public static Main getPlugin() {
        return plugin;
    }

    /**
     * Gets the {@link Database} singleton.
     *
     * @return the database.
     */
    @Nonnull
    public static Database getServerDatabase() {
        return plugin.getDatabase();
    }

    /**
     * Gets player's {@link PlayerDatabase} instance.
     *
     * @param player - Player.
     * @return player's database.
     */
    @Nonnull
    public static PlayerDatabase getDatabase(@Nonnull Player player) {
        return getDatabase(player.getUniqueId());
    }

    /**
     * Gets player's {@link PlayerDatabase} instance.
     *
     * @param uuid - UUID of the player.
     * @return player's database.
     */
    @Nonnull
    public static PlayerDatabase getDatabase(@Nonnull UUID uuid) {
        return PlayerDatabase.getDatabase(uuid);
    }

    /**
     * Gets the {@link CrateManager} singleton.
     *
     * @return the crate manager.
     */
    @Nonnull
    public static CrateManager getCrateManager() {
        return plugin.getCrateManager();
    }

    /**
     * Gets an optional {@link LivingGameEntity} from a bukkit entity.
     *
     * @param entity - Bukkit entity.
     * @return an optional game entity.
     */
    @Nonnull
    public static Optional<LivingGameEntity> getEntityOptional(@Nullable LivingEntity entity) {
        if (entity == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(getEntity(entity.getUniqueId()));
    }

    /**
     * Gets an optional {@link LivingGameEntity} from a bukkit entity.
     *
     * @param entity - Bukkit entity.
     * @param as     - Entity class.
     * @return an optional of game entity.
     */
    @Nonnull
    public static <T extends LivingGameEntity> Optional<T> getEntity(@Nullable LivingEntity entity, @Nonnull Class<T> as) {
        if (entity == null) {
            return Optional.empty();
        }

        final LivingGameEntity gameEntity = getEntity(entity);
        if (as.isInstance(gameEntity)) {
            return Optional.of(as.cast(gameEntity));
        }

        return Optional.empty();
    }

    /**
     * Gets a {@link LivingGameEntity} from a bukkit entity.
     *
     * @param entity - Bukkit entity.
     * @return a game entity; or null.
     */
    @Nullable
    public static LivingGameEntity getEntity(@Nullable Entity entity) {
        if (entity == null) {
            return null;
        }

        final LivingGameEntity gameEntity = getEntity(entity.getUniqueId());
        return gameEntity == null ? null : gameEntity.getGameEntity();
    }

    /**
     * Creates a {@link GameEntity}.
     *
     * @param location - Location to create at.
     * @param type     - Type of the entity.
     * @param consumer - Consumer function on how to create an entity.
     * @return a newly created {@link GameEntity} instance.
     */
    @Nonnull
    public static <T extends LivingEntity, E extends GameEntity> E createEntity(@Nonnull Location location, @Nonnull Entities<T> type, @Nonnull ConsumerFunction<T, E> consumer) {
        return Manager.current().createEntity(location, type, consumer);
    }

    @Nonnull
    public static <T extends LivingEntity> LivingGameEntity createEntity(@Nonnull Location location, @Nonnull Entities<T> type) {
        return createEntity(location, type, LivingGameEntity::new);
    }

    /**
     * Gets a {@link LivingGameEntity} by its {@link UUID}.
     *
     * @param uuid - UUID.
     * @return a living game entity; or null.
     */
    @Nullable
    public static LivingGameEntity getEntity(@Nonnull UUID uuid) {
        return manager.getEntity(uuid);
    }

    /**
     * Gets a {@link GamePlayer} from a bukkit player.
     *
     * @param player - Bukkit player.
     * @return a game player; or null.
     */
    @Nullable
    public static GamePlayer getPlayer(@Nullable Player player) {
        if (player == null) {
            return null;
        }

        return manager.getPlayer(player);
    }

    /**
     * Gets a {@link GamePlayer} by their {@link UUID}.
     *
     * @param uuid - UUID.
     * @return a game player; or null.
     */
    @Nullable
    public static GamePlayer getPlayer(@Nonnull UUID uuid) {
        return manager.getPlayer(uuid);
    }

    /**
     * Gets a {@link GamePlayer} by their {@link OfflinePlayer}.
     *
     * @param player - Offline player.
     * @return a game player; or null.
     */
    @Nullable
    public static GamePlayer getPlayer(@Nonnull OfflinePlayer player) {
        return manager.getPlayer(player.getUniqueId());
    }

    /**
     * Gets a {@link GamePlayer} from a {@link PlayerEvent}.
     *
     * @param ev - Event.
     * @return a game player; or null.
     */
    @Nullable
    public static GamePlayer getPlayer(@Nonnull PlayerEvent ev) {
        final Player player = ev.getPlayer();

        return getPlayer(player);
    }

    /**
     * Gets an optional of {@link GamePlayer}.
     *
     * @param player - Bukkit player.
     * @return an optional of game player.
     */
    @Nonnull
    public static Optional<GamePlayer> getPlayerOptional(@Nonnull Player player) {
        return Optional.ofNullable(manager.getPlayer(player));
    }

    /**
     * Gets a copy of existing {@link GamePlayer}s.
     *
     * @return a copy of existing game players.
     */
    @Nonnull
    public static Set<GamePlayer> getPlayers() {
        return manager.getPlayers();
    }

    @Nonnull
    public static Set<GamePlayer> getPlayers(@Nonnull Predicate<GamePlayer> predicate) {
        return manager.getPlayers(predicate);
    }

    /**
     * Gets a copy of existing {@link GamePlayer}s who is {@link GamePlayer#isAlive()}.
     *
     * @return a copy of living players.
     */
    @Nonnull
    public static Set<GamePlayer> getAlivePlayers() {
        return manager.getAlivePlayers();
    }

    /**
     * Gets a copy of existing {@link GamePlayer}s who is {@link GamePlayer#isAlive()} and match the {@link Predicate}.
     *
     * @param predicate - Predicate to match.
     * @return a copy of living players matching the predicate.
     */
    @Nonnull
    public static Set<GamePlayer> getAlivePlayers(@Nonnull Predicate<GamePlayer> predicate) {
        return manager.getAlivePlayers(predicate);
    }

    /**
     * Gets a copy of existing {@link GamePlayer}s who is {@link GamePlayer#isAlive()} and have a matching {@link Hero} selected.
     *
     * @param enumHero - Selected hero.
     * @return a copy of living player matching the hero.
     */
    @Nonnull
    public static Set<GamePlayer> getAlivePlayers(@Nonnull Hero enumHero) {
        return manager.getAlivePlayers(enumHero);
    }

    /**
     * Gets a copy {@link Hero} that are being used by at least one existing {@link GamePlayer}.
     *
     * @return a copy of active heroes.
     */
    @Nonnull
    public static Set<Hero> getActiveHeroes() {
        return manager.getActiveHeroes();
    }

    /**
     * Gets a copy of all existing {@link GameEntity}s.
     *
     * @return a copy of all existing game entities.
     */
    @Nonnull
    public static Set<GameEntity> getEntities() {
        return manager.getEntities();
    }

    /**
     * Gets a copy of all existing {@link GameEntity}s that match a given class.
     *
     * @param clazz - Class to match.
     * @return a copy of all existing game entities matching a given class.
     */
    @Nonnull
    public static <T extends GameEntity> Set<T> getEntities(@Nonnull Class<T> clazz) {
        return manager.getEntities(clazz);
    }

    /**
     * Gets a copy of all existing {@link GameEntity} excluding {@link GamePlayer}s.
     *
     * @return a copy of all existing game entities excluding game players.
     */
    @Nonnull
    public static Set<GameEntity> getEntitiesExcludePlayers() {
        return manager.getEntitiesExcludePlayers();
    }

    /**
     * Create an AoE explosion at the given location.
     *
     * @param location  - Location; the center of the explosion.
     * @param damage    - Explosion damage.
     * @param radius    - Explosion radius.
     * @param damager   - Damager, if needed.
     * @param cause     - Cause, if needed.
     * @param predicate - Predicate for entity to damage.
     * @return list of affected entities.
     */
    @Nonnull
    public static List<LivingGameEntity> damageAoE(@Nonnull Location location, double damage, double radius, @Nullable LivingGameEntity damager, @Nullable EnumDamageCause cause, @Nonnull Predicate<LivingGameEntity> predicate) {
        final List<LivingGameEntity> entities = Collect.nearbyEntities(location, radius).stream().filter(predicate).toList();

        for (LivingGameEntity entity : entities) {
            entity.damage(damage, damager, cause);
        }

        return entities;
    }

    /**
     * Create an AoE explosion at the given location.
     *
     * @param location  - Location; the center of the explosion.
     * @param damage    - Explosion damage.
     * @param radius    - Explosion radius.
     * @param damager   - Damager, if needed.
     * @param cause     - Cause, if needed.
     * @param predicate - Predicate for entity to damage.
     * @return list of affected entities.
     */
    @Nonnull
    public static List<LivingGameEntity> damageAoE(Location location, double damage, double radius, @Nullable LivingEntity damager, @Nullable EnumDamageCause cause, @Nonnull Predicate<LivingGameEntity> predicate) {
        return damageAoE(location, damage, radius, CF.getEntity(damager), cause, predicate);
    }

    /**
     * Gets the logger for the plugin.
     *
     * @return the logger for the plugin.
     */
    @Nonnull
    public static Logger getLogger() {
        return plugin.getLogger();
    }

    /**
     * Registers the given {@link Listener} to the plugin.
     *
     * @param listener - Listener.
     */
    public static void registerEvents(@Nonnull Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    /**
     * Registers the given {@link Listener}s to the plugin.
     *
     * @param listeners - Listeners to register.
     */
    public static void registerEvents(@Nonnull List<Listener> listeners) {
        for (Listener listener : listeners) {
            registerEvents(listener);
        }
    }

    /**
     * Gets an {@link GameEntity} by its entity Id.
     *
     * @param entityId - Entity Id.
     * @return a game entity; or null.
     */
    @Nullable
    public static GameEntity getEntityById(int entityId) {
        return manager.getEntityById(entityId);
    }

    /**
     * Gets the string version of the game.
     *
     * @return string version of the game.
     */
    @Nonnull
    public static String getVersion() {
        return plugin.getDescription().getVersion();
    }

    /**
     * Gets the string version of the game without '-SNAPSHOT'.
     *
     * @return the string version of the game without '-SNAPSHOT'.
     */
    @Nonnull
    public static String getVersionNoSnapshot() {
        return getVersion().replace("-SNAPSHOT", "");
    }

    /**
     * Gets the server's most recent tps.
     *
     * @return the server's most recent tps.
     */
    public static double getTps() {
        return Math.min(Reflect.getMinecraftServer().recentTps[0], 20);
    }

    @Nonnull
    public static String getTpsFormatted() {
        String color;
        final double tps = getTps();

        if (tps >= 20) {
            color = "&2";
        }
        else if (tps >= 19) {
            color = "&a";
        }
        else if (tps >= 18) {
            color = "&e";
        }
        else if (tps >= 15) {
            color = "&6";
        }
        else if (tps >= 10) {
            color = "&c";
        }
        else if (tps >= 5) {
            color = "&4";
        }
        else {
            color = "&4&l";
        }

        return color + "%.1f".formatted(tps);
    }

    /**
     * Gets online player count, respecting player's hidden status.
     *
     * @return online player count.
     */
    public static int getOnlinePlayerCount() {
        int onlineCount = 0;

        for (Player player : Bukkit.getOnlinePlayers()) {
            final PlayerProfile profile = CF.getProfileOrNull(player);

            if (profile == null || profile.isHidden()) {
                continue;
            }

            onlineCount++;
        }

        return onlineCount;
    }

    /**
     * Gets the name of the game.
     *
     * @return the name of the game.
     */
    @Nonnull
    public static String getName() {
        return Main.GAME_NAME;
    }

    @Nonnull
    public static CommandProcessor getCommandProcessor() {
        if (commandProcessor == null) {
            commandProcessor = new CommandProcessor(getPlugin());
        }

        return commandProcessor;
    }

    public static void registerCommand(@Nonnull SimpleCommand command) {
        getCommandProcessor().registerCommand(command);
    }

    @Nonnull
    public static AntiCheat getAntiCheat() {
        return AntiCheat.getInstance();
    }

    @Nonnull
    public static String getMinecraftVersion() {
        return Bukkit.getMinecraftVersion();
    }

    @Nonnull
    public static VehicleManager getVehicleManager() {
        return plugin.getVehicleManager();
    }

    @Nullable
    public static PlayerProfile getProfileOrNull(@Nonnull Player player) {
        return manager.getProfileOrNull(player);
    }

    @Nonnull
    public static PlayerProfile getProfile(@Nonnull Player player) {
        return manager.getProfile(player);
    }

    public static boolean hasProfile(@Nonnull Player player) {
        return manager.hasProfile(player);
    }

    @Nonnull
    public static CFQuestHandler getQuestHandler() {
        return plugin.getQuestHandler();
    }

    @Nonnull
    public static Environment environment() {
        return plugin.environment();
    }

}

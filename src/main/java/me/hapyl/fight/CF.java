package me.hapyl.fight;

import me.hapyl.fight.database.Database;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.cosmetic.crate.CrateManager;
import me.hapyl.fight.game.entity.ConsumerFunction;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.util.Collect;
import me.hapyl.spigotutils.module.entity.Entities;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

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

    private CF() {
    }

    @Nonnull
    public static Main getPlugin() {
        return plugin;
    }

    @Nonnull
    public static Database getDatabase() {
        return plugin.getDatabase();
    }

    @Nonnull
    public static PlayerDatabase getDatabase(@Nonnull Player player) {
        return getDatabase(player.getUniqueId());
    }

    @Nonnull
    public static PlayerDatabase getDatabase(@Nonnull UUID uuid) {
        return PlayerDatabase.getDatabase(uuid);
    }

    @Nonnull
    public static CrateManager getCrateManager() {
        return plugin.getCrateManager();
    }

    @Nonnull
    public static Optional<LivingGameEntity> getEntityOptional(@Nullable LivingEntity entity) {
        if (entity == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(getEntity(entity.getUniqueId()));
    }

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

    @Nullable
    public static LivingGameEntity getEntity(@Nullable Entity entity) {
        if (entity == null) {
            return null;
        }

        final LivingGameEntity gameEntity = getEntity(entity.getUniqueId());
        return gameEntity == null ? null : gameEntity.getGameEntity();
    }

    @Nonnull
    public static <T extends LivingEntity, E extends GameEntity> E createEntity(@Nonnull Location location, @Nonnull Entities<T> type, @Nonnull ConsumerFunction<T, E> consumer) {
        return Manager.current().createEntity(type.spawn(location, self -> Manager.current().addIgnored(self)), consumer);
    }

    @Nullable
    public static LivingGameEntity getEntity(@Nonnull UUID uuid) {
        return manager.getEntity(uuid);
    }

    @Nullable
    public static GamePlayer getPlayer(@Nullable Player player) {
        if (player == null) {
            return null;
        }

        return manager.getPlayer(player);
    }

    @Nullable
    public static GamePlayer getPlayer(@Nonnull UUID uuid) {
        return manager.getPlayer(uuid);
    }

    @Nonnull
    public static Set<GamePlayer> getPlayers() {
        return manager.getPlayers();
    }

    @Nonnull
    public static List<GamePlayer> getAlivePlayers() {
        return manager.getAlivePlayers();
    }

    @Nonnull
    public static List<GamePlayer> getAlivePlayers(@Nonnull Predicate<GamePlayer> predicate) {
        return manager.getAlivePlayers(predicate);
    }

    @Nonnull
    public static List<GamePlayer> getAlivePlayers(Heroes enumHero) {
        return manager.getAlivePlayers(enumHero);
    }

    @Nonnull
    public static Set<Heroes> getActiveHeroes() {
        return manager.getActiveHeroes();
    }

    @Nonnull
    public static Set<GameEntity> getEntities() {
        return manager.getEntities();
    }

    @Nonnull
    public static <T extends GameEntity> Set<T> getEntities(Class<T> clazz) {
        return manager.getEntities(clazz);
    }

    @Nonnull
    public static Set<GameEntity> getEntitiesExcludePlayers() {
        return manager.getEntitiesExcludePlayers();
    }

    @Nonnull
    public static Optional<GamePlayer> getPlayerOptional(Player player) {
        final GamePlayer gamePlayer = manager.getPlayer(player);

        if (gamePlayer == null) {
            return Optional.empty();
        }

        return Optional.of(gamePlayer);
    }

    @Nonnull
    public static List<LivingGameEntity> damageAoE(@Nonnull Location location, double damage, double radius, @Nullable LivingGameEntity damager, @Nullable EnumDamageCause cause, @Nonnull Predicate<LivingGameEntity> predicate) {
        final List<LivingGameEntity> entities = Collect.nearbyEntities(location, radius).stream().filter(predicate).toList();

        for (LivingGameEntity entity : entities) {
            entity.damage(damage, damager, cause);
        }

        return entities;
    }

    @Nonnull
    public static List<LivingGameEntity> damageAoE(Location location, double damage, double radius, @Nullable LivingEntity damager, @Nullable EnumDamageCause cause, @Nonnull Predicate<LivingGameEntity> predicate) {
        return damageAoE(location, damage, radius, CF.getEntity(damager), cause, predicate);
    }

    @Nonnull
    public static Logger getLogger() {
        return plugin.getLogger();
    }

    public static void registerEvents(@Nonnull Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    @Nullable
    public static GameEntity getEntityById(int entityId) {
        return manager.getEntityById(entityId);
    }
}

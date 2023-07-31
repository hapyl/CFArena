package me.hapyl.fight;

import me.hapyl.fight.database.Database;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.cosmetic.crate.CrateManager;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.util.Collect;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

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
    public static Optional<GameEntity> getEntityOptional(LivingEntity entity) {
        if (entity == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(getEntity(entity.getUniqueId()));
    }

    @Nullable
    public static GameEntity getEntity(LivingEntity entity) {
        if (entity == null) {
            return null;
        }

        return getEntity(entity.getUniqueId());
    }

    @Nullable
    public static GameEntity getEntity(@Nonnull UUID uuid) {
        return manager.getEntity(uuid);
    }

    @Nonnull
    public static GamePlayer getOrCreatePlayer(@Nonnull Player player) {
        return manager.getOrCreatePlayer(player);
    }

    @Nullable
    public static GamePlayer getPlayer(@Nullable Player player) {
        if (player == null) {
            return null;
        }

        return manager.getPlayer(player);
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
    public static List<GameEntity> damageAoE(@Nonnull Location location, double damage, double radius, @Nullable GameEntity damager, @Nullable EnumDamageCause cause, @Nonnull Predicate<GameEntity> predicate) {
        final List<GameEntity> entities = Collect.nearbyEntities(location, radius).stream().filter(predicate).toList();

        for (GameEntity entity : entities) {
            entity.damage(damage, damager, cause);
        }

        return entities;
    }

    @Nonnull
    public static List<GameEntity> damageAoE(Location location, double damage, double radius, @Nullable LivingEntity damager, @Nullable EnumDamageCause cause, @Nonnull Predicate<GameEntity> predicate) {
        return damageAoE(location, damage, radius, CF.getEntity(damager), cause, predicate);
    }

    @Nonnull
    public static Logger getLogger() {
        return plugin.getLogger();
    }
}

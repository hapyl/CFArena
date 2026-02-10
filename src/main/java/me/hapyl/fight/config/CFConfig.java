package me.hapyl.fight.config;

import me.hapyl.fight.game.maps.EnumLevel;
import me.hapyl.fight.game.type.EnumGameType;
import me.hapyl.fight.util.Sha256;
import org.bukkit.configuration.file.FileConfiguration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.BiFunction;

public interface CFConfig {
    
    @Nonnull
    String databaseConnectionLink();
    
    @Nullable
    Sha256 transferSecretSha256();
    
    @Nonnull
    EnumLevel currentLevel();
    
    void currentLevel(@Nonnull EnumLevel level);
    
    @Nonnull
    EnumGameType currentGameType();
    
    void currentGameType(@Nonnull EnumGameType type);
    
    void reload();
    
    void save();
    
    @Nonnull
    <T> Optional<T> get(@Nonnull String key, @Nonnull BiFunction<FileConfiguration, String, T> fn);
    
    @Nonnull
    default Optional<String> getString(@Nonnull String key) {
        return get(key, FileConfiguration::getString);
    }
    
    @Nonnull
    default Optional<Integer> getInt(@Nonnull String key) {
        return get(key, FileConfiguration::getInt);
    }
    
    <T> void set(@Nonnull String key, @Nullable T value);
}

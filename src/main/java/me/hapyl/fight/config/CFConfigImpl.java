package me.hapyl.fight.config;

import me.hapyl.fight.Main;
import me.hapyl.fight.annotate.Promise;
import me.hapyl.fight.game.maps.EnumLevel;
import me.hapyl.fight.game.type.EnumGameType;
import me.hapyl.fight.util.Sha256;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

public class CFConfigImpl implements CFConfig {
    
    private static final String configName = "config.yml";
    
    private final Main main;
    private final File configFile;
    
    @Promise("!null") private FileConfiguration config;
    
    public CFConfigImpl(Main main) {
        this.main = main;
        this.configFile = new File(main.getDataFolder(), configName);
        
        // Reload the config
        reload();
    }
    
    @Nonnull
    @Override
    public String databaseConnectionLink() {
        return getString("database_connection_link").orElse("");
    }
    
    @Nullable
    @Override
    public Sha256 transferSecretSha256() {
        try {
            final String base64 = getString("transfer_secret_sha256").orElse(null);
            
            return base64 != null ? Sha256.fromBase64(base64) : null;
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    @Nonnull
    @Override
    public EnumLevel currentLevel() {
        return getString("selected_level")
                .map(EnumLevel::byName)
                .orElse(EnumLevel.ARENA);
    }
    
    @Override
    public void currentLevel(@Nonnull EnumLevel level) {
        set("selected_level", level.name());
    }
    
    @Nonnull
    @Override
    public EnumGameType currentGameType() {
        return getString("selected_game_type")
                .map(EnumGameType::byName)
                .orElse(EnumGameType.FFA);
    }
    
    @Override
    public void currentGameType(@Nonnull EnumGameType type) {
        set("selected_game_type", type.name());
    }
    
    @Override
    public void reload() {
        // The load from the file
        this.config = YamlConfiguration.loadConfiguration(configFile);
        
        // Set defaults
        this.config.setDefaults(
                YamlConfiguration.loadConfiguration(new InputStreamReader(Objects.requireNonNull(main.getResource(configName), "Missing %s in /resources!".formatted(configName))))
        );
        
        this.config.options().copyDefaults(true); // Copy default values
    }
    
    @Override
    public void save() {
        try {
            config.save(configFile);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Nonnull
    public <T> Optional<T> get(@Nonnull String key, @Nonnull BiFunction<FileConfiguration, String, T> fn) {
        return Optional.ofNullable(fn.apply(config, key));
    }
    
    @Override
    public <T> void set(@Nonnull String key, @Nullable T value) {
        config.set(key, value);
        
        // Save right away
        save();
    }
    
}

package me.hapyl.fight;

import me.hapyl.fight.database.Database;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.game.cosmetic.crate.CrateManager;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.UUID;

public final class CF {

    static Main plugin;

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
}

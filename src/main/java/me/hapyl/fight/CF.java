package me.hapyl.fight;

import me.hapyl.fight.database.Database;
import me.hapyl.fight.game.cosmetic.crate.CrateManager;

import javax.annotation.Nonnull;

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
    public static CrateManager getCrateManager() {
        return plugin.getCrateManager();
    }
}

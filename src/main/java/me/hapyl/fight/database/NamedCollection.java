package me.hapyl.fight.database;

import javax.annotation.Nonnull;

public enum NamedCollection {

    PLAYERS("players"),
    PARKOUR("parkour"),
    HERO_STATS("hero_stats"),
    FRIENDS("friends"),
    ANTI_CHEAT("anti_cheat"),
    ENVIRONMENT("environment");

    private final String id;

    NamedCollection(@Nonnull String id) {
        this.id = id;
    }

    @Nonnull
    public String id() {
        return id;
    }
}

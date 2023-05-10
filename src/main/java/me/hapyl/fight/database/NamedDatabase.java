package me.hapyl.fight.database;

import javax.annotation.Nonnull;

public enum NamedDatabase {

    PRODUCTION("", false),
    DEVELOPMENT("@dev", true);

    private static final String DATABASE_NAME = "classes_fight";

    private final String suffix;
    private final boolean dev;

    NamedDatabase(String suffix, boolean dev) {
        this.suffix = suffix;
        this.dev = dev;
    }

    public boolean isDevelopment() {
        return dev;
    }

    @Nonnull
    public String getName() {
        return DATABASE_NAME + suffix;
    }

    @Nonnull
    public static NamedDatabase byName(String name) {
        for (NamedDatabase value : values()) {
            if (value.name().equalsIgnoreCase(name)) {
                return value;
            }
        }

        return PRODUCTION;
    }
}

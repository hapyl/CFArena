package me.hapyl.fight.game.artifact;

import me.hapyl.fight.database.EntryPath;

import javax.annotation.Nonnull;

public enum Type implements EntryPath {

    ACTIVE,
    PASSIVE;

    @Nonnull
    @Override
    public String getEntryPath() {
        return name().toLowerCase();
    }
}

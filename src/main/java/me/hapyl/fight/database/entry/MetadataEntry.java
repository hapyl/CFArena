package me.hapyl.fight.database.entry;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MetadataEntry extends PlayerDatabaseEntry {

    public MetadataEntry(PlayerDatabase playerDatabase) {
        super(playerDatabase);
    }

    public <T> void set(@Nonnull MetadataKey key, @Nullable T value) {
        setValue(key.getKey(), value);
    }

    @Nonnull
    public <T> T get(@Nonnull MetadataKey key, @Nonnull T def) {
        return getValue(key.getKey(), def);
    }

    public boolean has(@Nonnull MetadataKey key) {
        return getValue(key.getKey(), null) != null;
    }

}

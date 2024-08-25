package me.hapyl.fight.database.entry;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;
import me.hapyl.fight.registry.Key;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MetadataEntry extends PlayerDatabaseEntry {

    public static final String PARENT = "metadata.";

    public MetadataEntry(PlayerDatabase playerDatabase) {
        super(playerDatabase);
    }

    public <T> void set(@Nonnull Key key, @Nullable T value) {
        setValue(PARENT + key.getKey(), value);
    }

    @Nonnull
    public <T> T get(@Nonnull Key key, @Nonnull T def) {
        return getValue(PARENT + key.getKey(), def);
    }

    public boolean has(@Nonnull Key key) {
        return getValue(PARENT + key.getKey(), null) != null;
    }

}

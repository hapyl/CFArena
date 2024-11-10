package me.hapyl.fight.database.entry;

import me.hapyl.eterna.module.registry.Key;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class Metadata extends Key {
    public Metadata(@Nonnull String key) {
        super(key);
    }

    public boolean has(@Nonnull Player player) {
        return MetadataEntry.has(player, this);
    }

    public void set(@Nonnull Player player, boolean b) {
        MetadataEntry.set(player, this, b);
    }
}

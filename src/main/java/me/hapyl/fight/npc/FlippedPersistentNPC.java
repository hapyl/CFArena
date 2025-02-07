package me.hapyl.fight.npc;

import me.hapyl.eterna.module.registry.Key;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FlippedPersistentNPC extends PersistentNPC {

    protected FlippedPersistentNPC(@Nonnull Key key, @Nonnull Location location, @Nullable String name) {
        super(key, location, name, uuid -> "Dinnerbone");
    }
}

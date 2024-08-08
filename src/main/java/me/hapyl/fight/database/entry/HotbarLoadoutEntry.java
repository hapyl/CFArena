package me.hapyl.fight.database.entry;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseArrayEntry;
import me.hapyl.fight.game.loadout.HotbarSlots;
import me.hapyl.eterna.module.util.Enums;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HotbarLoadoutEntry extends PlayerDatabaseArrayEntry<HotbarSlots> {
    public HotbarLoadoutEntry(PlayerDatabase playerDatabase) {
        super(playerDatabase, "hotbar_prefs", 9);
    }

    @Nonnull
    @Override
    public HotbarSlots[] newArray() {
        return new HotbarSlots[size];
    }

    @Nullable
    @Override
    public HotbarSlots fromString(@Nonnull String string) {
        return Enums.byName(HotbarSlots.class, string);
    }

    @Nonnull
    @Override
    public String toString(@Nonnull HotbarSlots hotbarSlots) {
        return hotbarSlots.name();
    }
}

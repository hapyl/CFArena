package me.hapyl.fight.database.entry;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.KeyedEnum;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseArrayEntry;
import me.hapyl.fight.game.loadout.HotBarSlot;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HotbarLoadoutEntry extends PlayerDatabaseArrayEntry<HotBarSlot> {
    public HotbarLoadoutEntry(PlayerDatabase playerDatabase) {
        super(playerDatabase, "hotbar_prefs", 9);
    }

    @Nonnull
    @Override
    public HotBarSlot[] newArray() {
        return new HotBarSlot[size];
    }

    @Nullable
    @Override
    public HotBarSlot fromString(@Nonnull String string) {
        final Key key = Key.ofStringOrNull(string);

        return key != null ? KeyedEnum.of(HotBarSlot.class, key) : null;
    }

    @Nonnull
    @Override
    public String toString(@Nonnull HotBarSlot hotBarSlot) {
        return hotBarSlot.getKeyAsString();
    }
}

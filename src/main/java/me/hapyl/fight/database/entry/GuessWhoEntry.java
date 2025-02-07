package me.hapyl.fight.database.entry;

import me.hapyl.eterna.module.registry.KeyedEnum;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;

import javax.annotation.Nonnull;

public class GuessWhoEntry extends PlayerDatabaseEntry {
    public GuessWhoEntry(@Nonnull PlayerDatabase playerDatabase) {
        super(playerDatabase, "guess_who");
    }

    public long getStat(@Nonnull StatType type) {
        return getValue(type.getKeyAsString(), 0L);
    }

    public void incrementStat(@Nonnull StatType type) {
        setValue(type.getKeyAsString(), getStat(type) + 1);
    }

    public void resetStat(@Nonnull StatType type) {
        setValue(type.getKeyAsString(), 0L);
    }

    public enum StatType implements KeyedEnum {
        WINS,
        LOSES,
        FORFEITS,
        WIN_STREAK
    }

}

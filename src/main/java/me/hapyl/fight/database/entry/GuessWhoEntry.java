package me.hapyl.fight.database.entry;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;

import javax.annotation.Nonnull;

public class GuessWhoEntry extends PlayerDatabaseEntry {
    public GuessWhoEntry(PlayerDatabase playerDatabase) {
        super(playerDatabase);

        setPath("guess_who");
    }

    public long getStat(@Nonnull StatType type) {
        final String key = type.name();

        return getValueInPath(key, 0L);
    }

    public void incrementStat(@Nonnull StatType type) {
        setValueInPath(type.name(), getStat(type) + 1);
    }

    public void resetStat(@Nonnull StatType type) {
        setValueInPath(type.name(), 0L);
    }

    public enum StatType {
        WINS,
        LOSES,
        FORFEITS,
        WIN_STREAK
    }

}

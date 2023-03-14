package me.hapyl.fight.database.legacy;

import me.hapyl.fight.database.DatabaseLegacy;
import me.hapyl.fight.database.entry.StatisticEntry;

public class StatisticEntryLegacy extends StatisticEntry {

    public StatisticEntryLegacy(DatabaseLegacy database) {
        super(database);
    }

    private long getValue(Type type) {
        return getConfigLegacy().getLong(type.name, 0L);
    }

    private void setValue(Type type, long value) {
        getConfigLegacy().set(type.name, value);
    }

    private void addValue(Type type, long value) {
        getConfigLegacy().set(type.name, getValue(type) + value);
    }

}

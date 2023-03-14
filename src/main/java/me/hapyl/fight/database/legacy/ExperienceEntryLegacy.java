package me.hapyl.fight.database.legacy;

import me.hapyl.fight.database.DatabaseLegacy;
import me.hapyl.fight.database.entry.ExperienceEntry;

public class ExperienceEntryLegacy extends ExperienceEntry {

    public ExperienceEntryLegacy(DatabaseLegacy database) {
        super(database);
    }

    @Override
    public void reset(ExperienceEntry.Type type) {
        this.set(type, type.getMinValue());
    }

    @Override
    public long get(ExperienceEntry.Type type) {
        return getConfigLegacy().getLong(type.pathLegacy(), type.getMinValue());
    }

    @Override
    public void set(ExperienceEntry.Type type, long value) {
        getConfigLegacy().set(type.pathLegacy(), value);
        super.updateExperience();
    }

    @Override
    public void remove(ExperienceEntry.Type type, long value) {
        add(type, -value);
    }

    @Override
    public void add(ExperienceEntry.Type type, long value) {
        set(type, get(type) + value);
    }

}

package me.hapyl.fight.database.legacy;

import me.hapyl.fight.database.DatabaseLegacy;
import me.hapyl.fight.database.entry.SettingEntry;
import me.hapyl.fight.game.setting.Setting;

public class SettingEntryLegacy extends SettingEntry {
    public SettingEntryLegacy(DatabaseLegacy database) {
        super(database);
    }

    public boolean getValue(Setting setting) {
        return getConfigLegacy().getBoolean(setting.getPathLegacy(), setting.getDefaultValue());
    }

    public void setValue(Setting setting, boolean value) {
        getConfigLegacy().set(setting.getPathLegacy(), value);
    }

}

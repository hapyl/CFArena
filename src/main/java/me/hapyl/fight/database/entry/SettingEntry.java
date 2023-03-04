package me.hapyl.fight.database.entry;

import me.hapyl.fight.database.Database;
import me.hapyl.fight.database.DatabaseEntry;
import me.hapyl.fight.game.setting.Setting;

public class SettingEntry extends DatabaseEntry {
    public SettingEntry(Database database) {
        super(database);
    }

    public boolean getValue(Setting setting) {
        return getDocument("setting").getBoolean(setting.getPath(), setting.getDefaultValue());
    }

    public void setValue(Setting setting, boolean value) {
        fetchDocument("setting", document -> document.put(setting.getPath(), value));
    }

}

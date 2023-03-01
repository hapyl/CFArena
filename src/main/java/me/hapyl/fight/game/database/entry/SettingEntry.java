package me.hapyl.fight.game.database.entry;

import me.hapyl.fight.game.database.Database;
import me.hapyl.fight.game.database.DatabaseEntry;
import me.hapyl.fight.game.setting.Setting;

public class SettingEntry extends DatabaseEntry {
    public SettingEntry(Database database) {
        super(database);
    }

    public boolean getValue(Setting setting) {
        return getConfig().getBoolean(setting.getPath(), setting.getDefaultValue());
    }

    public void setValue(Setting setting, boolean value) {
        getConfig().set(setting.getPath(), value);
    }

}

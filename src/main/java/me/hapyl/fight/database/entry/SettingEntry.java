package me.hapyl.fight.database.entry;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;
import me.hapyl.fight.game.setting.Setting;

public class SettingEntry extends PlayerDatabaseEntry {
    public SettingEntry(PlayerDatabase playerDatabase) {
        super(playerDatabase);
    }

    public boolean getValue(Setting setting) {
        return getDocument("setting").getBoolean(setting.getPath(), setting.getDefaultValue());
    }

    public void setValue(Setting setting, boolean value) {
        fetchDocument("setting", document -> document.put(setting.getPath(), value));
    }

}

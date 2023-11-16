package me.hapyl.fight.database.entry;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;
import me.hapyl.fight.game.setting.Settings;

import javax.annotation.Nonnull;

public class SettingEntry extends PlayerDatabaseEntry {
    public SettingEntry(PlayerDatabase playerDatabase) {
        super(playerDatabase);
    }

    public boolean getValue(@Nonnull Settings setting) {
        return getInDocument("setting").getBoolean(setting.name(), setting.getSetting().getDefaultValue());
    }

    public void setValue(@Nonnull Settings setting, boolean value) {
        fetchDocument("setting", document -> document.put(setting.name(), value));
    }

}

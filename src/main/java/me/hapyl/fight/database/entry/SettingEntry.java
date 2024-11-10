package me.hapyl.fight.database.entry;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;
import me.hapyl.fight.game.setting.EnumSetting;

import javax.annotation.Nonnull;

public class SettingEntry extends PlayerDatabaseEntry {
    public SettingEntry(@Nonnull PlayerDatabase playerDatabase) {
        super(playerDatabase, "setting");
    }

    public boolean getValue(@Nonnull EnumSetting setting) {
        return getValue(setting.getKeyAsString(), setting.getDefaultValue());
    }

    public void setValue(@Nonnull EnumSetting setting, boolean value) {
        setValue(setting.getKeyAsString(), value);
    }

}

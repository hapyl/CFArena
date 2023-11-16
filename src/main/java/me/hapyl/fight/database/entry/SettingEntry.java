package me.hapyl.fight.database.entry;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;
import me.hapyl.fight.game.setting.EnumSetting;
import me.hapyl.fight.game.setting.Setting;

import javax.annotation.Nonnull;

public class SettingEntry extends PlayerDatabaseEntry {
    public SettingEntry(PlayerDatabase playerDatabase) {
        super(playerDatabase);
    }

    @Nonnull
    public String getValue(@Nonnull Setting<?> setting) {
        return getInDocument("setting").get(setting.getId(), setting.getDefaultValue().name());
    }

    public <E extends Enum<E> & EnumSetting> void setValue(@Nonnull Setting<?> setting, E value) {
        fetchDocument("setting", document -> document.put(setting.name(), value.name()));
    }

}

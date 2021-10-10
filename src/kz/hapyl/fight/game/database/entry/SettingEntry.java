package kz.hapyl.fight.game.database.entry;

import kz.hapyl.fight.game.database.Database;
import kz.hapyl.fight.game.database.DatabaseEntry;
import kz.hapyl.fight.game.database.Type;
import kz.hapyl.fight.game.setting.Setting;

public class SettingEntry extends DatabaseEntry {
	public SettingEntry(Database database) {
		super(database);
	}

	public boolean getValue(Setting setting) {
		return getDatabase().getValue(setting.getPath(), Type.BOOL);
	}

	public void setValue(Setting setting, boolean value) {
		getConfig().set(setting.getPath(), value);
	}

}

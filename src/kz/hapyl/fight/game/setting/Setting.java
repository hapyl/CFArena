package kz.hapyl.fight.game.setting;

import kz.hapyl.fight.game.database.Database;
import org.bukkit.entity.Player;

import java.util.Locale;

public enum Setting {

	SPECTATE("Spectate", "Whenever you will spectate the game instead of playing it."),

	;

	private final String path;
	private final String name;
	private final String info;
	private final boolean def;

	Setting(String name, String info, boolean def) {
		this.path = name().toLowerCase(Locale.ROOT);
		this.name = name;
		this.info = info;
		this.def = def;
	}

	Setting(String name, String info) {
		this(name, info, false);
	}

	public String getName() {
		return name;
	}

	public boolean getDefaultValue() {
		return def;
	}

	public String getPath() {
		return "setting." + path;
	}

	public String getInfo() {
		return info;
	}

	public boolean isEnabled(Player player) {
		return Database.getDatabase(player).getSettings().getValue(this);
	}

	public void setEnabled(Player player, boolean flag) {
		Database.getDatabase(player).getSettings().setValue(this, flag);
	}

}

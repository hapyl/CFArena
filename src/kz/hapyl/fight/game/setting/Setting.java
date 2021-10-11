package kz.hapyl.fight.game.setting;

import kz.hapyl.fight.game.database.Database;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Locale;

public enum Setting {

	SPECTATE("Spectate", "Whenever you will spectate the game instead of playing it."),
	CHAT_PING("Chat Notification", "Whenever you will hear a ping in chat if someone mentions you."),

	;

	private final Material material;
	private final String name;
	private final String info;
	private final boolean def;

	Setting(String name, String info, Material material, boolean def) {
		this.name = name;
		this.material = material;
		this.info = info;
		this.def = def;
	}

	Setting(String name, String info) {
		this(name, info, Material.COBBLESTONE, false);
	}

	public Material getMaterial() {
		return material;
	}

	public String getName() {
		return name;
	}

	public boolean getDefaultValue() {
		return def;
	}

	public String getPath() {
		return "setting." + name().toLowerCase(Locale.ROOT);
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

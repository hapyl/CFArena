package kz.hapyl.fight;

import org.bukkit.plugin.java.JavaPlugin;

public class Injectable {

	private final Main main;

	public Injectable(Main main) {
	    this.main = main;
	}

	public Main getPlugin() {
		return main;
	}
}

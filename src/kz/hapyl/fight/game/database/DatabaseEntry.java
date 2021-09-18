package kz.hapyl.fight.game.database;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class DatabaseEntry {

	private final Database database;

	public DatabaseEntry(Database database) {
		this.database = database;
	}

	public Database getDatabase() {
		return database;
	}

	public YamlConfiguration getConfig() {
		return this.database.getYaml();
	}

	public Player getPlayer() {
		return this.database.getPlayer();
	}

}

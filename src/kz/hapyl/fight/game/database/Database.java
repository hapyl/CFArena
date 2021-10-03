package kz.hapyl.fight.game.database;

import kz.hapyl.fight.Main;
import kz.hapyl.fight.game.database.entry.CurrencyEntry;
import kz.hapyl.fight.game.database.entry.HeroEntry;
import kz.hapyl.fight.game.database.entry.StatisticEntry;
import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Database {

	public static final Map<UUID, Database> byUuid = new HashMap<>();

	private final Player player;
	private File file;
	private YamlConfiguration config;

	public Database(Player player) {
		this.player = player;
		this.loadFile();
		this.loadEntries();
	}

	public static Database getDatabase(Player player) {
		Database database = byUuid.get(player.getUniqueId());
		if (database == null) {
			database = new Database(player);
		}
		return database;
	}

	public void saveToFile() {
		try {
			this.config.save(this.file);
			sendInfo("&aSuccessfully saved database for %s.", player.getName());
		}
		catch (IOException e) {
			e.printStackTrace();
			sendInfo("&cAn error occurred whilst trying to save database for %s.", player.getName());
		}
	}

	public Player getPlayer() {
		return player;
	}

	public YamlConfiguration getYaml() {
		return config;
	}

	// entries start
	private HeroEntry heroEntry;
	private CurrencyEntry currencyEntry;
	private StatisticEntry statisticEntry;

	public void loadEntries() {
		this.heroEntry = new HeroEntry(this);
		this.currencyEntry = new CurrencyEntry(this);
		this.statisticEntry = new StatisticEntry(this);
	}

	public StatisticEntry getStatistics() {
		return statisticEntry;
	}

	public CurrencyEntry getCurrency() {
		return currencyEntry;
	}

	public HeroEntry getHeroEntry() {
		return heroEntry;
	}

	// entries end

	public void loadFile() {
		try {
			this.file = new File(Main.getPlugin().getDataFolder() + "/players", this.player.getUniqueId() + ".yml");
			this.config = YamlConfiguration.loadConfiguration(this.file);
			this.config.options().copyDefaults(true);
			this.saveToFile();
		}
		catch (Exception error) {
			error.printStackTrace();
			sendInfo("&cError creating instance for %s.", this.player.getName());
		}
		finally {
			sendInfo("&aCreated database instance for %s.", this.player.getName());
			byUuid.put(this.player.getUniqueId(), this);
		}
	}


	// this sent to console and admins
	private void sendInfo(String info, Object... toReplace) {
		final String format = Chat.format("&e&lDEBUG: &f" + info, toReplace);
		System.out.println(format);
		Bukkit.getOnlinePlayers().stream().filter(Player::isOp).forEach(player -> player.sendMessage(format));
	}

}

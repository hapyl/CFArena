package kz.hapyl.fight;

import kz.hapyl.fight.cmds.*;
import kz.hapyl.fight.effect.EnumEffect;
import kz.hapyl.fight.event.EnderPearlController;
import kz.hapyl.fight.event.PlayerEvent;
import kz.hapyl.fight.game.ChatController;
import kz.hapyl.fight.game.Manager;
import kz.hapyl.fight.game.database.Database;
import kz.hapyl.fight.game.maps.GameMaps;
import kz.hapyl.fight.game.scoreboard.GamePlayerUI;
import kz.hapyl.fight.game.scoreboard.ScoreList;
import kz.hapyl.fight.game.task.TaskList;
import kz.hapyl.spigotutils.SpigotUtils;
import kz.hapyl.spigotutils.module.command.CommandProcessor;
import kz.hapyl.spigotutils.module.command.SimplePlayerCommand;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public class Main extends JavaPlugin {

	private static Main plugin;

	private Manager manager;
	private TaskList taskList;
	private ScoreList scoreList;

	@Override
	public void onEnable() {
		plugin = this;
		regCommands();
		regEvents();

		SpigotUtils.hookIntoAPI(this);

		for (final World world : Bukkit.getWorlds()) {
			world.setGameRule(GameRule.NATURAL_REGENERATION, false);
		}

		this.manager = new Manager();
		this.taskList = new TaskList();
		this.scoreList = new ScoreList();

		// update database
		for (final Player player : Bukkit.getOnlinePlayers()) {
			handlePlayer(player);
		}

		getConfig().options().copyDefaults(true);
		saveConfig();

	}

	public Manager getManager() {
		return manager;
	}

	public TaskList getTaskList() {
		return taskList;
	}

	public ScoreList getScoreList() {
		return scoreList;
	}

	public void handlePlayer(Player player) {
		Database.getDatabase(player); // this will create database again (load)
		this.manager.loadLastHero(player);
		new GamePlayerUI(player);

		// teleport to spawn
		if (player.getGameMode() != GameMode.CREATIVE) {
			player.teleport(GameMaps.SPAWN.getMap().getLocation());
		}
	}

	@Override
	public void onDisable() {
		for (final Player player : Bukkit.getOnlinePlayers()) {
			Database.getDatabase(player).saveToFile();
		}

		if (this.manager.isGameInProgress()) {
			this.manager.stopCurrentGame();
		}
	}

	private void regCommands() {
		final CommandProcessor processor = new CommandProcessor();
		processor.registerCommand(new HeroCommand("hero"));
		processor.registerCommand(new GameCommand("cf"));
		processor.registerCommand(new ReportCommandCommand("report"));
		processor.registerCommand(new UltimateCommand("ultimate"));
		processor.registerCommand(new ParticleCommand("part"));
		processor.registerCommand(new GameEffectCommand("gameeffect"));
		processor.registerCommand(new MapCommand("map"));

		// these are small shortcuts not feeling creating a class D:
		processor.registerCommand(new SimplePlayerCommand("start") {
			@Override
			protected void execute(Player player, String[] strings) {
				player.performCommand("cf start " + (strings.length > 0 ? strings[0] : ""));
			}
		});

		processor.registerCommand(new SimplePlayerCommand("stop") {
			@Override
			protected void execute(Player player, String[] args) {
				// true -> stop server, false -> stop game instance
				final boolean type = args.length == 1 && (args[0].equalsIgnoreCase("server") || args[0].equalsIgnoreCase("s"));
				player.performCommand(type ? "minecraft:stop" : "cf stop");
			}

			@Override
			protected List<String> tabComplete(CommandSender sender, String[] args) {
				return super.completerSort(Arrays.asList("game", "server"), args);
			}
		});

	}

	private void regEvents() {
		final PluginManager pm = Bukkit.getServer().getPluginManager();
		pm.registerEvents(new PlayerEvent(), this);
		pm.registerEvents(new ChatController(), this);
		pm.registerEvents(new EnderPearlController(), this);
		pm.registerEvents(new EnumEffect(), this);
	}

	public void addEvent(Listener listener) {
		getServer().getPluginManager().registerEvents(listener, this);
	}

	public static Main getPlugin() {
		return plugin;
	}

}

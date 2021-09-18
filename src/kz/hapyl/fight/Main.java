package kz.hapyl.fight;

import kz.hapyl.fight.cmds.GameCommand;
import kz.hapyl.fight.cmds.HeroCommand;
import kz.hapyl.fight.cmds.ReportCommandCommand;
import kz.hapyl.fight.event.PlayerEvent;
import kz.hapyl.fight.game.ChatController;
import kz.hapyl.fight.game.Manager;
import kz.hapyl.fight.game.database.Database;
import kz.hapyl.fight.game.task.TaskList;
import kz.hapyl.spigotutils.module.command.CommandProcessor;
import kz.hapyl.spigotutils.module.command.SimplePlayerCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
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

	@Override
	public void onEnable() {
		plugin = this;
		regCommands();
		regEvents();

		this.manager = new Manager();
		this.taskList = new TaskList();

		// update database
		for (final Player player : Bukkit.getOnlinePlayers()) {
			Database.getDatabase(player); // this will create database again (load)
			this.manager.loadLastHero(player);
		}

	}

	@Override
	public void onDisable() {
		for (final Player player : Bukkit.getOnlinePlayers()) {
			Database.getDatabase(player).saveToFile();
		}
	}

	public Manager getManager() {
		return manager;
	}

	public TaskList getTaskList() {
		return taskList;
	}

	private void regCommands() {
		final CommandProcessor processor = new CommandProcessor();
		processor.registerCommand(new HeroCommand("hero"));
		processor.registerCommand(new GameCommand("cf"));
		processor.registerCommand(new ReportCommandCommand("report"));

		// these are small shortcuts not feeling creating a class D:
		processor.registerCommand(new SimplePlayerCommand("start") {
			@Override
			protected void execute(Player player, String[] strings) {
				player.performCommand("cf start");
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
	}

	private void addCommand(String cmd, CommandExecutor exec) {
		this.getCommand(cmd).setExecutor(exec);
	}

	private void addEvent(Listener listener) {
		getServer().getPluginManager().registerEvents(listener, this);
	}

	private void addCommand(String cmd, CommandExecutor exec, boolean includeTabCompleter) {
		this.getCommand(cmd).setExecutor(exec);
		if (includeTabCompleter)
			this.getCommand(cmd).setTabCompleter((TabCompleter)exec);
	}

	public static Main getPlugin() {
		return plugin;
	}

}

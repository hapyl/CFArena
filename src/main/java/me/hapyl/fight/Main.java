package me.hapyl.fight;

import me.hapyl.fight.event.EnderPearlController;
import me.hapyl.fight.event.PlayerEvent;
import me.hapyl.fight.game.ChatController;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.cosmetic.CosmeticsListener;
import me.hapyl.fight.game.experience.Experience;
import me.hapyl.fight.game.lobby.LobbyItems;
import me.hapyl.fight.game.maps.GameMaps;
import me.hapyl.fight.game.maps.features.BoosterController;
import me.hapyl.fight.game.task.TaskList;
import me.hapyl.fight.game.tutorial.ChatTutorial;
import me.hapyl.fight.game.tutorial.Tutorial;
import me.hapyl.fight.protocol.ArcaneMuteProtocol;
import me.hapyl.spigotutils.EternaAPI;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main plugin;

    private ChatTutorial tutorial;
    private Manager manager;
    private TaskList taskList;
    private BoosterController boosters;
    private Experience experience;

    @Override
    public void onEnable() {
        plugin = this;

        // Register commands
        new CommandRegistry(this);

        registerEvents();
        regProtocol();

        // Init api
        new EternaAPI(this);

        // Preset gamerules
        for (final World world : Bukkit.getWorlds()) {
            world.setGameRule(GameRule.NATURAL_REGENERATION, false);
            world.setGameRule(GameRule.DO_FIRE_TICK, false);
            world.setGameRule(GameRule.MOB_GRIEFING, false);
            world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
            world.setGameRule(GameRule.DO_MOB_LOOT, false);
            world.setGameRule(GameRule.DO_TILE_DROPS, false);
            world.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
            world.setGameRule(GameRule.DISABLE_RAIDS, false);
            world.setGameRule(GameRule.NATURAL_REGENERATION, false);
            world.setGameRule(GameRule.KEEP_INVENTORY, true);
        }

        // Remove recipes and achievements
        Bukkit.clearRecipes();
        Bukkit.advancementIterator().forEachRemaining(advancement -> {
        });


        this.manager = new Manager();
        this.taskList = new TaskList();
        this.tutorial = new ChatTutorial();
        this.boosters = new BoosterController();
        this.experience = new Experience();

        // update database
        for (final Player player : Bukkit.getOnlinePlayers()) {
            handlePlayer(player);
        }

        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    private void registerEvents() {
        final PluginManager pm = Bukkit.getServer().getPluginManager();
        pm.registerEvents(new PlayerEvent(), this);
        pm.registerEvents(new ChatController(), this);
        pm.registerEvents(new EnderPearlController(), this);
        pm.registerEvents(new BoosterController(), this);
        pm.registerEvents(new CosmeticsListener(), this);
    }

    public Experience getExperience() {
        return experience;
    }

    public Manager getManager() {
        return manager;
    }

    public TaskList getTaskList() {
        return taskList;
    }

    public Tutorial getTutorial() {
        return tutorial;
    }

    public BoosterController getBoosters() {
        return boosters;
    }

    public void handlePlayer(Player player) {
        this.manager.createProfile(player);

        // teleport to spawn unless in creative
        if (player.getGameMode() != GameMode.CREATIVE) {
            player.teleport(GameMaps.SPAWN.getMap().getLocation());
        }

        LobbyItems.giveAll(player);
    }

    @Override
    public void onDisable() {
        runSafe(() -> {
            for (final Player player : Bukkit.getOnlinePlayers()) {
                Manager.current().getProfile(player).getDatabase().saveToFile();
            }
        }, "database save");

        runSafe(() -> {
            if (this.manager.isGameInProgress()) {
                this.manager.stopCurrentGame();
            }
        }, "game instance stop");

        runSafe(this::saveConfig, "config saving");
    }

    private void runSafe(Runnable runnable, String handler) {
        try {
            runnable.run();
        } catch (Exception e) {
            System.out.printf("§cCannot run %s onDisable()!%n", handler);
            e.printStackTrace();
        }
    }

    private void regProtocol() {
        new ArcaneMuteProtocol();
    }

    public void addEvent(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    public static Main getPlugin() {
        return plugin;
    }

}

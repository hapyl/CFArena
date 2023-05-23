package me.hapyl.fight;

import me.hapyl.fight.database.Database;
import me.hapyl.fight.event.EnderPearlHandler;
import me.hapyl.fight.event.PlayerHandler;
import me.hapyl.fight.event.SnowFormHandler;
import me.hapyl.fight.game.ChatController;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.achievement.AchievementRegistry;
import me.hapyl.fight.game.collectible.Collectibles;
import me.hapyl.fight.game.cosmetic.CosmeticsListener;
import me.hapyl.fight.game.experience.Experience;
import me.hapyl.fight.game.maps.features.BoosterController;
import me.hapyl.fight.game.maps.gamepack.HealthPackListener;
import me.hapyl.fight.game.parkour.CFParkourManager;
import me.hapyl.fight.game.task.TaskList;
import me.hapyl.fight.notifier.Notifier;
import me.hapyl.fight.npc.HumanManager;
import me.hapyl.fight.protocol.ArcaneMuteProtocol;
import me.hapyl.fight.protocol.DismountProtocol;
import me.hapyl.spigotutils.EternaAPI;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import test.Test;

public class Main extends JavaPlugin {

    public static final String GAME_NAME = "&e&lCLASSES FIGHT &c&lᴀʀᴇɴᴀ";

    private static long start;
    private static Main plugin;

    public Manager manager;
    public HumanManager humanManager;
    public TaskList taskList;
    public BoosterController boosters;
    public Experience experience;
    public Database database;
    public Notifier notifier;
    public CFParkourManager parkourManager;
    public Collectibles collectibles;
    public AchievementRegistry achievementRegistry;

    @Override
    public void onEnable() {
        // Assign singleton & start time
        plugin = this;
        start = System.currentTimeMillis();

        // Initiate API
        new EternaAPI(this);

        // Write default config
        getConfig().options().copyDefaults(true);
        saveConfig();

        // Create database connection
        database = new Database(this);
        database.createConnection();

        // Register 'managers' 🤪
        manager = new Manager(this);
        taskList = new TaskList(this);
        experience = new Experience(this);
        boosters = new BoosterController(this);
        notifier = new Notifier(this);
        parkourManager = new CFParkourManager(this);
        humanManager = new HumanManager(this);
        collectibles = new Collectibles(this);
        achievementRegistry = new AchievementRegistry(this);

        // Register events and protocol listeners
        registerEvents();
        registerProtocol();

        // Preset game rules
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
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        }

        // Remove recipes and achievements
        Bukkit.clearRecipes();
        Registry.ADVANCEMENT.iterator().forEachRemaining(advancement -> {
        });

        // Register Commands
        new CommandRegistry(this);

        // Update database in case of /reload
        for (final Player player : Bukkit.getOnlinePlayers()) {
            manager.handlePlayer(player);
        }

        // Check for reload
        ReloadChecker.check(this, 20);

        // Initiate runtime tests
        new Test(this);
    }

    @Override
    public void onDisable() {
        runSafe(() -> {
            for (final Player player : Bukkit.getOnlinePlayers()) {
                Manager.current().getOrCreateProfile(player).getDatabase().save();
            }
        }, "Player database save.");

        runSafe(database::stopConnection, "Database connection stop.");

        runSafe(() -> {
            if (this.manager.isGameInProgress()) {
                this.manager.stopCurrentGame();
            }
        }, "Game instance stop.");

        runSafe(this::saveConfig, "Config save.");
    }

    public Database getDatabase() {
        return database;
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

    public BoosterController getBoosters() {
        return boosters;
    }

    public Collectibles getCollectibles() {
        return collectibles;
    }

    public AchievementRegistry getAchievementRegistry() {
        return achievementRegistry;
    }

    private void registerEvents() {
        final PluginManager pluginManager = Bukkit.getServer().getPluginManager();

        pluginManager.registerEvents(new PlayerHandler(), this);
        pluginManager.registerEvents(new ChatController(), this);
        pluginManager.registerEvents(new EnderPearlHandler(), this);
        pluginManager.registerEvents(new CosmeticsListener(), this);
        pluginManager.registerEvents(new HealthPackListener(), this);
        pluginManager.registerEvents(new SnowFormHandler(), this);
    }

    private void runSafe(Runnable runnable, String handler) {
        try {
            runnable.run();
        } catch (Exception e) {
            getLogger().severe("Cannot run %s onDisable()!".formatted(handler));
            e.printStackTrace();
        }
    }

    private void registerProtocol() {
        new ArcaneMuteProtocol();
        new DismountProtocol();
        //new ConfusionPotionProtocol(); -> doesn't work as good as I thought :(
    }

    public static Main getPlugin() {
        return plugin;
    }

    public static long getStartupTime() {
        return start;
    }
}

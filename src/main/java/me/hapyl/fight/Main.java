package me.hapyl.fight;

import me.hapyl.fight.chat.ChatHandler;
import me.hapyl.fight.command.CommandRegistry;
import me.hapyl.fight.database.Database;
import me.hapyl.fight.event.*;
import me.hapyl.fight.fastaccess.FastAccessListener;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.achievement.AchievementRegistry;
import me.hapyl.fight.game.collectible.Collectibles;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.cosmetic.CosmeticsListener;
import me.hapyl.fight.game.cosmetic.crate.CrateManager;
import me.hapyl.fight.game.entity.event.EntityEventHandler;
import me.hapyl.fight.game.entity.overlay.OverlayListener;
import me.hapyl.fight.game.experience.Experience;
import me.hapyl.fight.game.maps.features.BoosterController;
import me.hapyl.fight.game.maps.gamepack.GamePackListener;
import me.hapyl.fight.game.parkour.CFParkourManager;
import me.hapyl.fight.game.talents.archive.bloodfiend.candlebane.CandlebaneProtocol;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.TaskList;
import me.hapyl.fight.garbage.CFGarbageCollector;
import me.hapyl.fight.notifier.Notifier;
import me.hapyl.fight.npc.HumanManager;
import me.hapyl.fight.protocol.ArcaneMuteProtocol;
import me.hapyl.fight.protocol.CameraProtocol;
import me.hapyl.fight.protocol.DismountProtocol;
import me.hapyl.fight.protocol.PlayerClickAtEntityProtocol;
import me.hapyl.spigotutils.EternaAPI;
import me.hapyl.spigotutils.module.util.Validate;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import test.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Main extends JavaPlugin {

    public static final String GAME_NAME_HEADER = Color.GOLD.bold() +
            "\uD835\uDE72\uD835\uDE95\uD835\uDE8A\uD835\uDE9C\uD835\uDE9C\uD835\uDE8E\uD835\uDE9C \uD835\uDE75\uD835\uDE92\uD835\uDE90\uD835\uDE91\uD835\uDE9D";
    public static final String GAME_NAME = GAME_NAME_HEADER;

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
    private CrateManager crateManager;

    @Override
    public void onEnable() {
        // Assign singleton & start time
        plugin = this;
        CF.plugin = this;
        start = System.currentTimeMillis();

        // Initiate API
        new EternaAPI(this);

        // Write default config
        getConfig().options().copyDefaults(true);
        saveConfig();

        // Create database connection
        database = new Database(this);
        database.createConnection();

        // Register a task list before manager
        taskList = new TaskList(this);

        // Register the main manager
        manager = new Manager(this);
        CF.manager = manager;

        experience = new Experience(this);
        boosters = new BoosterController(this);
        notifier = new Notifier(this);
        parkourManager = new CFParkourManager(this);
        collectibles = new Collectibles(this);
        humanManager = new HumanManager(this);
        achievementRegistry = new AchievementRegistry(this);
        crateManager = new CrateManager(this);

        //new LampGame(this);

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

        // Clear garbage entities
        GameTask.runLater(CFGarbageCollector::clearInAllWorlds, 20);

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
            //((SnakeParkour) ParkourCourse.SNAKE_PARKOUR.getParkour()).getSnake().stop();
        }, "Snake removal");

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

    public void setConfigValue(@Nonnull String path, @Nullable Object value) {
        getConfig().set(path, value);
        saveConfig();
    }

    public <T extends Enum<T>> T getConfigEnumValue(String path, Class<T> clazz, T def) {
        final String string = getConfig().getString(path, "");
        final T enumValue = Validate.getEnumValue(clazz, string);

        if (enumValue == null) {
            return def;
        }

        return enumValue;
    }

    public CrateManager getCrateManager() {
        return crateManager;
    }

    private void registerEvents() {
        final PluginManager pluginManager = Bukkit.getServer().getPluginManager();

        pluginManager.registerEvents(new PlayerHandler(), this);
        pluginManager.registerEvents(new EntityHandler(), this);
        pluginManager.registerEvents(new EntityEventHandler(), this);
        pluginManager.registerEvents(new ChatHandler(), this);
        pluginManager.registerEvents(new EnderPearlHandler(), this);
        pluginManager.registerEvents(new CosmeticsListener(), this);
        pluginManager.registerEvents(new GamePackListener(), this);
        pluginManager.registerEvents(new SnowFormHandler(), this);
        pluginManager.registerEvents(new ServerHandler(), this);
        pluginManager.registerEvents(new OverlayListener(), this);
        pluginManager.registerEvents(new CFGarbageCollector(), this);
        pluginManager.registerEvents(new FastAccessListener(), this);
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
        new CandlebaneProtocol();
        new CameraProtocol();
        new PlayerClickAtEntityProtocol();
        //new HandshakeProtocol();
        //new ConfusionPotionProtocol(); -> doesn't work as good as I thought :(
    }


    public static Main getPlugin() {
        return plugin;
    }

    public static long getStartupTime() {
        return start;
    }
}

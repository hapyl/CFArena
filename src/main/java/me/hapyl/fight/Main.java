package me.hapyl.fight;

import me.hapyl.fight.chat.ChatHandler;
import me.hapyl.fight.command.CommandRegistry;
import me.hapyl.fight.database.Database;
import me.hapyl.fight.event.*;
import me.hapyl.fight.fastaccess.FastAccessListener;
import me.hapyl.fight.filter.ProfanityFilter;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.achievement.AchievementRegistry;
import me.hapyl.fight.game.collectible.relic.RelicHunt;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.cosmetic.CosmeticsListener;
import me.hapyl.fight.game.cosmetic.crate.CrateManager;
import me.hapyl.fight.game.entity.event.EntityEventHandler;
import me.hapyl.fight.game.entity.overlay.OverlayListener;
import me.hapyl.fight.game.experience.Experience;
import me.hapyl.fight.game.maps.features.BoosterController;
import me.hapyl.fight.game.maps.gamepack.GamePackListener;
import me.hapyl.fight.game.parkour.CFParkourManager;
import me.hapyl.fight.game.talents.bloodfiend.candlebane.CandlebaneListener;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.TaskList;
import me.hapyl.fight.game.trial.TrialListener;
import me.hapyl.fight.garbage.CFGarbageCollector;
import me.hapyl.fight.notifier.Notifier;
import me.hapyl.fight.npc.HumanManager;
import me.hapyl.fight.npc.runtime.RuntimeNPCManager;
import me.hapyl.fight.protocol.*;
import me.hapyl.fight.script.ScriptManager;
import me.hapyl.fight.util.strict.StrictValidator;
import me.hapyl.spigotutils.EternaAPI;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.player.tablist.Tablist;
import me.hapyl.spigotutils.module.util.Validate;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class Main extends JavaPlugin {

    public static final String GAME_NAME = Color.GOLD.bold() +
            "&6&lᴄғ &eᴀʀᴇɴᴀ";

    public static final VersionInfo versionInfo = new VersionInfo(
            new UpdateTopic("1.20.6, here I come!", 49, 147, 232, 7, 97, 176)
    );

    public static final String requireEternaVersion = "3.0.0";
    public static final String requireMinecraftVersion = "1.20.6";

    private static long start;
    private static Main plugin;

    private ScriptManager scriptManager;
    private Manager manager;
    private HumanManager humanManager;
    private TaskList taskList;
    private BoosterController boosters;
    private Experience experience;
    private Database database;
    private Notifier notifier;
    private CFParkourManager parkourManager;
    private RelicHunt relicHunt;
    private AchievementRegistry achievementRegistry;
    private CrateManager crateManager;
    private RuntimeNPCManager npcManager;
    private ReloadChecker reloadChecker;

    @Override
    public void onEnable() {
        // Assign singleton & start time
        plugin = CF.plugin = this;
        start = System.currentTimeMillis();

        // Who knows why profanity is the first
        // thing initialized, but I'm not touching it
        ProfanityFilter.instantiate(this);

        // Initiate API
        new EternaAPI(this, requireEternaVersion);

        // Write default config
        getConfig().options().copyDefaults(true);
        saveConfig();

        // Create database connection
        database = new Database(this);
        database.createConnection();

        // Register a task list before manager
        taskList = new TaskList(this);

        // Register the main manager
        manager = CF.manager = new Manager(this);

        experience = new Experience(this);
        boosters = new BoosterController(this);
        notifier = new Notifier(this);
        parkourManager = new CFParkourManager(this);
        relicHunt = new RelicHunt(this);
        humanManager = new HumanManager(this);
        achievementRegistry = new AchievementRegistry(this);
        crateManager = new CrateManager(this);
        scriptManager = new ScriptManager(this);
        npcManager = new RuntimeNPCManager(this);

        //new LampGame(this);

        // Register events listeners
        registerEvents();

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
            world.setGameRule(GameRule.COMMAND_BLOCK_OUTPUT, false);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);

            // Unload nether and the end
            switch (world.getEnvironment()) {
                // Do NOT unload the end because it breaks portals
                case NETHER -> Bukkit.unloadWorld(world, false);
            }
        }

        // Remove recipes and achievements
        Registry.ADVANCEMENT.iterator().forEachRemaining(advancement -> {
            Bukkit.getUnsafe().removeAdvancement(advancement.getKey());
        });

        Bukkit.reloadData();
        Bukkit.clearRecipes();

        // Register Commands
        new CommandRegistry(this);

        // Check for reload
        this.reloadChecker = new ReloadChecker(this);
        this.reloadChecker.check(20);

        // Delayed operations
        GameTask.runLater(() -> {
            // We have teo re-create profiles in case of /reload
            Bukkit.getOnlinePlayers().forEach(player -> manager.createProfile(player));

            // Clear old entities, most likely because of /reload
            CFGarbageCollector.clearInAllWorlds();
        }, 20);

        // Load contributors
        //Contributors.loadContributors();

        // Load update hack
        //new UpdateBlockHackReplacer();

        new TrialListener();

        StrictValidator.validateAll(this);
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

        runSafe(() -> {
            Bukkit.getOnlinePlayers().forEach(player -> {
                final Tablist oldTablist = Tablist.getPlayerTabList(player);

                if (oldTablist != null) {
                    oldTablist.destroy();
                }
            });
        }, "Tablist removal.");
    }

    // *=* Getters *=* //

    @Nonnull
    public RuntimeNPCManager getNpcManager() {
        return npcManager;
    }

    @Nonnull
    public ScriptManager getScriptManager() {
        return scriptManager;
    }

    @Nonnull
    public Database getDatabase() {
        return database;
    }

    @Nonnull
    public Experience getExperience() {
        return experience;
    }

    @Nonnull
    public Manager getManager() {
        return manager;
    }

    @Nonnull
    public TaskList getTaskList() {
        return taskList;
    }

    @Nonnull
    public BoosterController getBoosters() {
        return boosters;
    }

    @Nonnull
    public RelicHunt getRelicHunt() {
        return relicHunt;
    }

    @Nonnull
    public AchievementRegistry getAchievementRegistry() {
        return achievementRegistry;
    }

    @Nonnull
    public CrateManager getCrateManager() {
        return crateManager;
    }

    @Nonnull
    public HumanManager getHumanManager() {
        return humanManager;
    }

    @Nonnull
    public Notifier getNotifier() {
        return notifier;
    }

    @Nonnull
    public CFParkourManager getParkourManager() {
        return parkourManager;
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
        pluginManager.registerEvents(new ArcaneMuteListener(), this);

        pluginManager.registerEvents(new DismountProtocol(), this);
        pluginManager.registerEvents(new CandlebaneListener(), this);
        pluginManager.registerEvents(new CameraListener(), this);
        pluginManager.registerEvents(new PlayerClickAtEntityProtocol(), this);
        pluginManager.registerEvents(new MotDProtocol(), this);
    }

    private void runSafe(Runnable runnable, String handler) {
        try {
            runnable.run();
        } catch (Exception e) {
            getLogger().severe("Cannot run %s onDisable()!".formatted(handler));
            e.printStackTrace();
        }
    }

    @Nonnull
    public static Main getPlugin() {
        return Objects.requireNonNull(plugin);
    }

    public static long getStartupTime() {
        return start;
    }
}

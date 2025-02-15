package me.hapyl.fight;

import me.hapyl.eterna.Eterna;
import me.hapyl.eterna.EternaAPI;
import me.hapyl.eterna.module.player.tablist.Tablist;
import me.hapyl.eterna.module.util.Enums;
import me.hapyl.fight.anticheat.AntiCheat;
import me.hapyl.fight.chat.ChatHandler;
import me.hapyl.fight.command.CommandRegistry;
import me.hapyl.fight.config.Environment;
import me.hapyl.fight.database.Database;
import me.hapyl.fight.event.*;
import me.hapyl.fight.fastaccess.FastAccessHandler;
import me.hapyl.fight.filter.ProfanityFilter;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.collectible.relic.RelicHunt;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.cosmetic.CosmeticHandler;
import me.hapyl.fight.game.crate.CrateManager;
import me.hapyl.fight.game.experience.Experience;
import me.hapyl.fight.game.lobby.LobbyItems;
import me.hapyl.fight.game.maps.features.BoosterController;
import me.hapyl.fight.game.maps.gamepack.GamePackHandler;
import me.hapyl.fight.game.parkour.CFParkourManager;
import me.hapyl.fight.game.talents.bloodfiend.candlebane.CandlebanePacketHandler;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.TaskList;
import me.hapyl.fight.game.trial.TrialListener;
import me.hapyl.fight.garbage.SynchronizedGarbageEntityCollector;
import me.hapyl.fight.notifier.NotificationManager;
import me.hapyl.fight.npc.PersistentNPCManager;
import me.hapyl.fight.protocol.ArcaneMutePacketHandler;
import me.hapyl.fight.protocol.DismountPacketHandler;
import me.hapyl.fight.protocol.MotDPacketHandler;
import me.hapyl.fight.protocol.PlayerClickAtEntityPacketHandler;
import me.hapyl.fight.quest.CFQuestHandler;
import me.hapyl.fight.registry.Registries;
import me.hapyl.fight.script.ScriptManager;
import me.hapyl.fight.store.Store;
import me.hapyl.fight.util.strict.StrictValidator;
import me.hapyl.fight.vehicle.VehicleManager;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class Main extends JavaPlugin {

    public static final String GAME_NAME = Color.GOLD.bold() +
            "&6&lᴄғ &eᴀʀᴇɴᴀ";

    public static final UpdateTopic updateTopic = new UpdateTopic("&4He's Back, Isn't He?");

    public static final String requireEternaVersion = "4.7.11";
    public static final String requireMinecraftVersion = "1.21.3"; // fixme: Either implement this or delete

    private static long start;
    private static Main plugin;

    private ScriptManager scriptManager;
    private Manager manager;
    private PersistentNPCManager persistentNPCManager;
    private TaskList taskList;
    private BoosterController boosters;
    private Experience experience;
    private Database database;
    private NotificationManager notificationManager;
    private CFParkourManager parkourManager;
    private RelicHunt relicHunt;
    private CrateManager crateManager;
    private ReloadChecker reloadChecker;
    private VehicleManager vehicleManager;
    private Registries registries;
    private Store store;
    private CFQuestHandler questHandler;
    private Environment environment;

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
        environment = new Environment(this);

        // Register the main manager
        manager = CF.manager = new Manager(this);

        // Registry must have priority!
        registries = new Registries(this);

        experience = new Experience(this);
        boosters = new BoosterController(this);
        notificationManager = new NotificationManager(this);
        parkourManager = new CFParkourManager(this);
        relicHunt = new RelicHunt(this);
        persistentNPCManager = new PersistentNPCManager(this);
        crateManager = new CrateManager(this);
        scriptManager = new ScriptManager(this);
        vehicleManager = new VehicleManager(this);
        store = new Store(this);
        questHandler = new CFQuestHandler(this);
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
        // todo - Use resourse pack for this the new ignore thingy
        Bukkit.advancementIterator().forEachRemaining(advancement -> {
            Bukkit.getUnsafe().removeAdvancement(advancement.getKey());
        });

        Bukkit.reloadData();
        Bukkit.clearRecipes();

        // Register Commands
        new CommandRegistry(this);

        // Instantiate anti cheat
        AntiCheat.getInstance();

        // Check for reload
        this.reloadChecker = new ReloadChecker(this);
        this.reloadChecker.check(20);

        // Delayed operations
        GameTask.runLater(
                () -> {
                    // We have teo re-create profiles in case of /reload
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        manager.createProfile(player);

                        // Fix quests
                        Eterna.getManagers().quest.simulateOnJoin(player);
                    });

                    // Clear old entities, most likely because of /reload
                    SynchronizedGarbageEntityCollector.clearInAllWorlds();

                    // Load lobby items
                    LobbyItems.values();
                }, 1
        ); // Sped up the profile creation, why was it 20 ticks anyway?

        // Load contributors
        //Contributors.loadContributors();

        // Load update hack
        //new UpdateBlockHackReplacer();

        StrictValidator.validateAll(this);
    }

    @Override
    public void onDisable() {
        runSafe(
                () -> {
                    for (final Player player : Bukkit.getOnlinePlayers()) {
                        Manager.current().getProfile(player).getDatabase().save();
                    }
                }, "Player database save."
        );

        runSafe(database::stopConnection, "Database connection stop.");

        runSafe(
                () -> {
                    //((SnakeParkour) ParkourCourse.SNAKE_PARKOUR.getParkour()).getSnake().stop();
                }, "Snake removal"
        );

        runSafe(
                () -> {
                    if (this.manager.isGameInProgress()) {
                        this.manager.stopCurrentGame();
                    }
                }, "Game instance stop."
        );

        runSafe(this::saveConfig, "Config save.");

        runSafe(
                () -> {
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        final Tablist oldTablist = Tablist.getPlayerTabList(player);

                        if (oldTablist != null) {
                            oldTablist.destroy();
                        }
                    });
                }, "Tablist removal."
        );
    }

    // *=* Getters *=* //

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
    public CrateManager getCrateManager() {
        return crateManager;
    }

    @Nonnull
    public PersistentNPCManager getHumanManager() {
        return persistentNPCManager;
    }

    @Nonnull
    public NotificationManager getNotifier() {
        return notificationManager;
    }

    @Nonnull
    public CFParkourManager getParkourManager() {
        return parkourManager;
    }

    @Nonnull
    public VehicleManager getVehicleManager() {
        return vehicleManager;
    }

    @Nonnull
    public Registries getRegistries() {
        return registries;
    }

    @Nonnull
    public Store getStore() {
        return store;
    }

    @Nonnull
    public CFQuestHandler getQuestHandler() {
        return questHandler;
    }

    public void setConfigValue(@Nonnull String path, @Nullable Object value) {
        getConfig().set(path, value);
        saveConfig();
    }

    public <T extends Enum<T>> T getConfigEnumValue(String path, Class<T> clazz, T def) {
        final String string = getConfig().getString(path, "");
        final T enumValue = Enums.byName(clazz, string);

        if (enumValue == null) {
            return def;
        }

        return enumValue;
    }

    @Nonnull
    public Environment environment() {
        return environment;
    }

    private void registerEvents() {
        CF.registerEvents(List.of(
                new PlayerHandler(),
                new EntityHandler(),
                new ChatHandler(),
                new EnderPearlHandler(),
                new CosmeticHandler(),
                new GamePackHandler(),
                new SnowFormHandler(),
                new ServerHandler(),
                new SynchronizedGarbageEntityCollector.Handler(),
                new FastAccessHandler(),
                new TrialListener(),

                // Packet Listeners
                new ArcaneMutePacketHandler(),
                new DismountPacketHandler(),
                new CandlebanePacketHandler(),
                new PlayerClickAtEntityPacketHandler(),
                new MotDPacketHandler()
        ));
    }

    private void runSafe(Runnable runnable, String handler) {
        try {
            runnable.run();
        } catch (Exception e) {
            getLogger().severe("Cannot run %s onDisable()!".formatted(handler));
            e.printStackTrace();
        }
    }

    /**
     * @deprecated {@link CF#getPlugin()}
     */
    @Nonnull
    @Deprecated
    public static Main getPlugin() {
        return Objects.requireNonNull(plugin);
    }

    public static long getStartupTime() {
        return start;
    }
}

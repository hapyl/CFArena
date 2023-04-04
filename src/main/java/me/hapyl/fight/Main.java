package me.hapyl.fight;

import me.hapyl.fight.database.DatabaseMongo;
import me.hapyl.fight.database.Databases;
import me.hapyl.fight.event.EnderPearlController;
import me.hapyl.fight.event.PlayerEvent;
import me.hapyl.fight.game.ChatController;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.cosmetic.CosmeticsListener;
import me.hapyl.fight.game.experience.Experience;
import me.hapyl.fight.game.lobby.LobbyItems;
import me.hapyl.fight.game.maps.GameMaps;
import me.hapyl.fight.game.maps.features.BoosterController;
import me.hapyl.fight.game.parkour.CFParkourManager;
import me.hapyl.fight.game.task.TaskList;
import me.hapyl.fight.notifier.Notifier;
import me.hapyl.fight.npc.HumanManager;
import me.hapyl.fight.protocol.ArcaneMuteProtocol;
import me.hapyl.spigotutils.EternaAPI;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.Runnables;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import test.Test;

public class Main extends JavaPlugin {

    public static final String GAME_NAME = "&e&lCLASSES FIGHT &c&lᴀʀᴇɴᴀ";
    private static Main plugin;

    private Manager manager;
    private HumanManager humanManager;
    private TaskList taskList;
    private BoosterController boosters;
    private Experience experience;
    private DatabaseMongo database;
    private Notifier notifier;
    private Databases databases;
    private CFParkourManager parkourManager;

    private boolean databaseLegacy;

    @Override
    public void onEnable() {
        plugin = this;
        databaseLegacy = false;

        // Init config
        getConfig().options().copyDefaults(true);
        saveConfig();

        // Register commands
        new CommandRegistry(this);

        // Auth database
        initDatabase();

        registerEvents();
        regProtocol();

        setNaggable(false);

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
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        }

        // Remove recipes and achievements
        Bukkit.clearRecipes();
        //Bukkit.advancementIterator().forEachRemaining(advancement -> {
        //    Bukkit.getUnsafe().removeAdvancement(advancement.getKey());
        //});
        //getServer().reloadData();
        //
        //System.out.println(Bukkit.advancementIterator());

        this.manager = new Manager();
        this.taskList = new TaskList();
        this.boosters = new BoosterController();
        this.experience = new Experience();
        this.notifier = new Notifier(this);

        if (isDatabaseLegacy()) {
            Bukkit.getLogger().severe("Databases are not supported in legacy mode!");
        }
        else {
            this.parkourManager = new CFParkourManager(this);
            this.databases = new Databases(this);
        }

        // update database
        for (final Player player : Bukkit.getOnlinePlayers()) {
            handlePlayer(player);
        }

        // Create NPCs
        humanManager = new HumanManager(this);

        checkReload();
    }

    private void checkReload() {
        Runnables.runLater(() -> {
            try {
                final Server server = getServer();
                final int reloadCount = (int) server.getClass().getDeclaredField("reloadCount").get(server);

                if (reloadCount > 0) {
                    Chat.broadcastOp("");
                    Chat.broadcastOp("&4&lServer Reload Detected!");
                    Chat.broadcastOp(
                            "&cNote that %s does &nnot&c support &e/reload&c and it's &nshould only&c be used in development.",
                            getDescription().getName()
                    );
                    Chat.broadcastOp("&cIf you are not a developer, please restart the server instead.");
                    Chat.broadcastOp("");

                    Bukkit.getOnlinePlayers().stream().filter(Player::isOp).forEach(player -> {
                        PlayerLib.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 0.0f);
                        PlayerLib.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 0.0f);
                    });
                }
            } catch (NoSuchFieldException | IllegalAccessException ignored) {
            }
        }, 20L);
    }

    @Override
    public void onDisable() {
        runSafe(() -> {
            databases.saveAll();
            //parkourManager.saveAll();
        }, "database save");

        runSafe(() -> {
            for (final Player player : Bukkit.getOnlinePlayers()) {
                Manager.current().getProfile(player).getDatabase().saveToFile();
            }
        }, "player database save");

        runSafe(() -> {
            database.stopConnection();
        }, "mongodb connection stop");

        runSafe(() -> {
            if (this.manager.isGameInProgress()) {
                this.manager.stopCurrentGame();
            }
        }, "game instance stop");

        runSafe(this::saveConfig, "config saving");
    }

    private void initDatabase() {
        this.database = new DatabaseMongo();

        final boolean useMongoDb = getConfig().getBoolean("database.use_mongodb");
        boolean connection = false;

        if (useMongoDb) {
            // Don't try to connect if disabled
            connection = this.database.createConnection();
        }

        if (!useMongoDb || !connection) {
            final String message = useMongoDb ?
                    (ChatColor.RED + "Failed to connect to MongoDB, initializing legacy database...") :
                    (ChatColor.YELLOW + "Using legacy database because MongoDB is disabled in config.yml!");

            Bukkit.getLogger().severe(message);
            Bukkit.getScheduler().runTaskLater(this, () -> Chat.broadcast("&6&lWarning! " + message), 20L);

            databaseLegacy = true;
        }

        // Init runtime tests
        new Test(this);
    }

    private void registerEvents() {
        final PluginManager pm = Bukkit.getServer().getPluginManager();
        pm.registerEvents(new PlayerEvent(), this);
        pm.registerEvents(new ChatController(), this);
        pm.registerEvents(new EnderPearlController(), this);
        pm.registerEvents(new BoosterController(), this);
        pm.registerEvents(new CosmeticsListener(), this);
    }

    public HumanManager getHumanManager() {
        return humanManager;
    }

    public Notifier getNotifier() {
        return notifier;
    }

    public Databases getDatabases() {
        return databases;
    }

    public DatabaseMongo getDatabase() {
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

    public void handlePlayer(Player player) {
        this.manager.createProfile(player);

        // teleport to spawn unless in creative
        if (player.getGameMode() != GameMode.CREATIVE) {
            player.teleport(GameMaps.SPAWN.getMap().getLocation());
            LobbyItems.giveAll(player);
        }
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
        //new ConfusionPotionProtocol(); -> doesn't work as good as I thought :(
    }

    public void addEvent(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    public static Main getPlugin() {
        return plugin;
    }

    public boolean isDatabaseLegacy() {
        return databaseLegacy;
    }
}

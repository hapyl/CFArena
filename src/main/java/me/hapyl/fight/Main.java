package me.hapyl.fight;

import me.hapyl.fight.database.Database;
import me.hapyl.fight.database.DatabaseRest;
import me.hapyl.fight.event.EnderPearlHandler;
import me.hapyl.fight.event.PlayerHandler;
import me.hapyl.fight.game.ChatController;
import me.hapyl.fight.game.IGameInstance;
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
import me.hapyl.fight.protocol.DismountProtocol;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.EternaAPI;
import me.hapyl.spigotutils.module.chat.CenterChat;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.Runnables;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import test.Test;

import javax.annotation.Nullable;
import java.util.List;

public class Main extends JavaPlugin {

    public static final String GAME_NAME = "&e&lCLASSES FIGHT &c&lᴀʀᴇɴᴀ";
    private static Main plugin;

    private Manager manager;
    private HumanManager humanManager;
    private TaskList taskList;
    private BoosterController boosters;
    private Experience experience;
    private Database database;
    private Notifier notifier;
    private DatabaseRest databaseCollection;
    private CFParkourManager parkourManager;

    @Override
    public void onEnable() {
        plugin = this;

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

        this.manager = new Manager(this);
        this.taskList = new TaskList();
        this.experience = new Experience();
        this.boosters = new BoosterController(this);
        this.notifier = new Notifier(this);

        this.parkourManager = new CFParkourManager(this);
        this.databaseCollection = new DatabaseRest(this);

        // update database
        for (final Player player : Bukkit.getOnlinePlayers()) {
            handlePlayer(player);
        }

        // Create NPCs
        humanManager = new HumanManager(this);

        checkReload();

        // Init runtime tests
        new Test(this);
    }

    @Override
    public void onDisable() {
        runSafe(() -> {
            databaseCollection.saveAll();
            //parkourManager.saveAll();
        }, "database save");

        runSafe(() -> {
            for (final Player player : Bukkit.getOnlinePlayers()) {
                Manager.current().getProfile(player).getDatabase().save();
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

    public HumanManager getHumanManager() {
        return humanManager;
    }

    public Notifier getNotifier() {
        return notifier;
    }

    public DatabaseRest getDatabases() {
        return databaseCollection;
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

    public void handlePlayer(Player player) {
        this.manager.createProfile(player);

        // teleport either to spawn or the map is there is a game in progress
        final IGameInstance game = this.manager.getCurrentGame();
        if (!game.isReal()) {
            final GameMode gameMode = player.getGameMode();
            if (gameMode == GameMode.CREATIVE || gameMode == GameMode.SPECTATOR) {
                return;
            }

            player.teleport(GameMaps.SPAWN.getMap().getLocation());
            LobbyItems.giveAll(player);
            return;
        }

        player.teleport(game.getMap().getMap().getLocation());
    }

    public void addEvent(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    private void checkReload() {
        Runnables.runLater(() -> {
            try {
                final Server server = getServer();
                final int reloadCount = (int) server.getClass().getDeclaredField("reloadCount").get(server);

                if (reloadCount > 0) {
                    sendCenterMessageToOperatorsAndConsole("");
                    sendCenterMessageToOperatorsAndConsole("&4&lWARNING");
                    sendCenterMessageToOperatorsAndConsole("&cSever Reload Detected!");
                    sendCenterMessageToOperatorsAndConsole("");

                    sendCenterMessageToOperatorsAndConsole(
                            "&cNote that %s does &nnot&c support &e/reload&c and it's &nshould only&c be used in development.",
                            getDescription().getName()
                    );

                    sendCenterMessageToOperatorsAndConsole("");

                    sendCenterMessageToOperatorsAndConsole("&cIf you are not a developer, please &lrestart&c the server instead.");
                    sendCenterMessageToOperatorsAndConsole("");

                    // sfx
                    Bukkit.getOnlinePlayers()
                            .stream()
                            .filter(Player::isOp)
                            .forEach(player -> {
                                PlayerLib.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 0.0f);
                                PlayerLib.playSound(player, Sound.BLOCK_NOTE_BLOCK_PLING, 0.0f);
                            });
                }
            } catch (NoSuchFieldException | IllegalAccessException ignored) {
            }
        }, 20L);
    }

    private void sendCenterMessageToOperatorsAndConsole(String message, @Nullable Object... format) {
        if (message.isEmpty() || message.isBlank()) {
            Utils.getOnlineOperatorsAndConsole().forEach(sender -> {
                Chat.sendMessage(sender, "", format);
            });
            return;
        }

        final List<String> strings = ItemBuilder.splitString("&c", Chat.format(message, format), 50);

        for (String string : strings) {
            final String centerString = CenterChat.makeString(string);

            Utils.getOnlineOperatorsAndConsole().forEach(sender -> {
                Chat.sendMessage(sender, centerString, format);
            });
        }
    }

    private void initDatabase() {
        this.database = new Database(this);
        this.database.createConnection();
    }

    private void registerEvents() {
        final PluginManager pm = Bukkit.getServer().getPluginManager();
        pm.registerEvents(new PlayerHandler(), this);
        pm.registerEvents(new ChatController(), this);
        pm.registerEvents(new EnderPearlHandler(), this);
        pm.registerEvents(new CosmeticsListener(), this);
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
        new DismountProtocol();
        //new ConfusionPotionProtocol(); -> doesn't work as good as I thought :(
    }

    public static Main getPlugin() {
        return plugin;
    }
}

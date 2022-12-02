package me.hapyl.fight;

import me.hapyl.fight.cmds.*;
import me.hapyl.fight.effect.EnumEffect;
import me.hapyl.fight.event.EnderPearlController;
import me.hapyl.fight.event.PlayerEvent;
import me.hapyl.fight.game.ChatController;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.exp.Experience;
import me.hapyl.fight.game.maps.GameMaps;
import me.hapyl.fight.game.maps.features.BoosterController;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.TaskList;
import me.hapyl.fight.game.tutorial.ChatTutorial;
import me.hapyl.fight.game.tutorial.Tutorial;
import me.hapyl.spigotutils.EternaAPI;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.chat.Gradient;
import me.hapyl.spigotutils.module.chat.gradient.Interpolators;
import me.hapyl.spigotutils.module.command.CommandProcessor;
import me.hapyl.spigotutils.module.command.SimplePlayerAdminCommand;
import me.hapyl.spigotutils.module.command.SimplePlayerCommand;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.reflect.DataWatcherType;
import me.hapyl.spigotutils.module.reflect.Reflect;
import me.hapyl.spigotutils.module.reflect.npc.HumanNPC;
import me.hapyl.spigotutils.module.util.Action;
import net.minecraft.world.entity.Entity;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        regCommands();
        regEvents();

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

    private void regCommands() {
        final CommandProcessor processor = new CommandProcessor(this);
        processor.registerCommand(new HeroCommand("hero"));
        processor.registerCommand(new GameCommand("cf"));
        processor.registerCommand(new ReportCommandCommand("report"));
        processor.registerCommand(new UltimateCommand("ultimate"));
        processor.registerCommand(new ParticleCommand("part"));
        processor.registerCommand(new GameEffectCommand("gameEffect"));
        processor.registerCommand(new MapCommand("map"));
        processor.registerCommand(new ModeCommand("mode"));
        processor.registerCommand(new AdminCommand("admin"));
        processor.registerCommand(new DebugBooster("debugBooster"));
        processor.registerCommand(new TrialCommand("trial"));
        processor.registerCommand(new SettingCommand("setting"));
        processor.registerCommand(new HelpCommand("help"));
        processor.registerCommand(new GamemodeShortcut("gamemode"));
        processor.registerCommand(new TeamCommand("team"));

        processor.registerCommand(new SimplePlayerAdminCommand("riptide") {

            private final Set<Player> riptideActive = new HashSet<>();
            private HumanNPC npc;

            @Override
            protected void execute(Player player, String[] args) {

                // launch
                if (args.length >= 1 && npc != null) {
                    Chat.sendMessage(player, "&aLaunch started!");
                    new GameTask() {
                        private int maxTick = 20;

                        @Override
                        public void run() {
                            if (maxTick-- < 0) {
                                Chat.sendMessage(player, "&aLaunch finished!");
                                this.cancel();
                                return;
                            }

                            final Location location = npc.getLocation();
                            location.setYaw(90f);
                            location.setPitch(90f);
                            location.add(player.getEyeLocation().getDirection().multiply(0.25d));
                            npc.setLocation(location);

                        }
                    }.runTaskTimer(0, 1);

                    return;
                }

                if (npc != null) {
                    npc.remove();
                    npc = null;
                    Chat.sendMessage(player, "&aRemoved!");
                    return;
                }

                final Location location = player.getLocation();
                location.add(0.0d, 1.8d, 0.0d);
                location.setYaw(90f);
                location.setPitch(90f);

                final HumanNPC npc = new HumanNPC(location, "", player.getName());
                npc.showAll();
                npc.bukkitEntity().setInvisible(true);
                npc.setDataWatcherByteValue(8, (byte) 0x04);

                this.npc = npc;
                Chat.sendMessage(player, "&aSpawned!");

                if (true) {
                    return;
                }

                if (riptideActive.contains(player)) {
                    riptideActive.remove(player);
                    return;
                }

                player.setVelocity(player.getLocation().getDirection().multiply(1.25d));
                new GameTask() {
                    private int maxTick = 40;

                    @Override
                    public void run() {
                        if (maxTick-- < 0) {
                            this.cancel();
                        }
                    }
                }.runTaskTimer(0, 1);
            }
        });

        processor.registerCommand(new SimplePlayerAdminCommand("spawnme") {

            HumanNPC npc;

            @Override
            protected void execute(Player player, String[] strings) {
                if (npc != null) {

                    if (strings.length > 0) {
                        Chat.sendMessage(player, "&aApplying skin...");
                        npc.setSkinAsync(strings[0]);
                        return;
                    }

                    npc.remove();
                    npc = null;
                    Chat.sendMessage(player, "&aRemoved!");
                    return;
                }

                npc = new HumanNPC(player.getLocation(), player.getName(), player.getName());
                npc.setLookAtCloseDist(5);
                npc.addDialogLine("Hello {player}, my name is {name} and I'm here as a test!", 40);
                npc.addDialogLine("I'm located at {location}", 20);
                npc.addDialogLine("That's it then, bye &c❤");
                npc.setInteractionDelay(60);
                npc.show(player);
                Chat.sendMessage(player, "&aSpawned!");

            }
        });

        processor.registerCommand(new SimplePlayerAdminCommand("spawnDancingPiglin") {
            @Override
            protected void execute(Player player, String[] args) {
                final Piglin piglin = Entities.PIGLIN.spawn(player.getLocation());
                final Entity minecraftEntity = Reflect.getMinecraftEntity(piglin);

                piglin.setCustomName(new Gradient("Dancing Piglin").rgb(Color.PINK, Color.RED, Interpolators.LINEAR));
                piglin.setCustomNameVisible(true);
                piglin.setImmuneToZombification(true);
                Chat.sendMessage(player, "&aSpawned!");

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (piglin.isDead()) {
                            this.cancel();
                            return;
                        }

                        Reflect.setDataWatcherValue(minecraftEntity, DataWatcherType.BOOL, 19, true);
                    }
                }.runTaskTimer(plugin, 0, 1);
            }
        });

        // these are small shortcuts not feeling creating a class D:

        processor.registerCommand(tinyCommand("start", (player, args) -> {
            player.performCommand("cf start " + (args.length > 0 ? args[0] : ""));
        }));

        processor.registerCommand(new SimplePlayerCommand("stop") {
            // true -> stop server, false -> stop game instance
            @Override
            protected void execute(Player player, String[] args) {
                final boolean type = args.length == 1 && (args[0].equalsIgnoreCase("server") || args[0].equalsIgnoreCase(
                        "s"));
                player.performCommand(type ? "minecraft:stop" : "cf stop");
            }

            @Override
            protected List<String> tabComplete(CommandSender sender, String[] args) {
                return super.completerSort(Arrays.asList("game", "server"), args);
            }
        });

    }

    private SimplePlayerCommand tinyCommand(String name, Action.AB<Player, String[]> action) {
        return new SimplePlayerCommand(name) {
            @Override
            protected void execute(Player player, String[] strings) {
                action.use(player, strings);
            }
        };
    }

    private void regEvents() {
        final PluginManager pm = Bukkit.getServer().getPluginManager();
        pm.registerEvents(new PlayerEvent(), this);
        pm.registerEvents(new ChatController(), this);
        pm.registerEvents(new EnderPearlController(), this);
        pm.registerEvents(new EnumEffect(), this);
        pm.registerEvents(new BoosterController(), this);
    }

    public void addEvent(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    public static Main getPlugin() {
        return plugin;
    }

}

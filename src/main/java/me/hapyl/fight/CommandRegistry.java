package me.hapyl.fight;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.hapyl.fight.cmds.*;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.TitleAnimation;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.chat.Gradient;
import me.hapyl.spigotutils.module.chat.gradient.Interpolators;
import me.hapyl.spigotutils.module.command.*;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.reflect.DataWatcherType;
import me.hapyl.spigotutils.module.reflect.Reflect;
import me.hapyl.spigotutils.module.reflect.glow.Glowing;
import me.hapyl.spigotutils.module.reflect.npc.HumanNPC;
import me.hapyl.spigotutils.module.reflect.npc.NPCPose;
import me.hapyl.spigotutils.module.util.Action;
import me.hapyl.spigotutils.module.util.Runnables;
import net.minecraft.world.entity.Entity;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class CommandRegistry {

    private final Main plugin;
    private final CommandProcessor processor;

    public CommandRegistry(Main main) {
        this.plugin = main;
        this.processor = new CommandProcessor(main);

        register(new HeroCommand("hero"));
        register(new GameCommand("cf"));
        register(new ReportCommandCommand("report"));
        register(new ParticleCommand("part"));
        register(new GameEffectCommand("gameEffect"));
        register(new MapCommand("map"));
        register(new ModeCommand("mode"));
        register(new AdminCommand("admin"));
        register(new DebugBooster("debugBooster"));
        register(new TrialCommand("trial"));
        register(new SettingCommand("setting"));
        register(new TutorialCommand("tutorial"));
        register(new TeamCommand("team"));
        register(new TestWinConditionCommand("testWinCondition"));
        register(new TriggerWinCommand("triggerWin"));
        register(new DummyCommand("dummy"));
        register(new CooldownCommand("cooldown"));
        register(new ExperienceCommand("experience"));
        register(new AchievementCommand("achievement"));
        register(new CosmeticCommand("cosmetic"));
        register(new SyncDatabaseCommand("syncDatabase"));
        register(new CastSpellCommand("cast"));
        register(new UpdateParkourLeaderboardCommand("updateParkourLeaderboard"));
        register(new InterruptCommand("interrupt"));
        register(new TestDatabaseCommand("testdatabase"));
        register(new EquipCommand("equip"));
        register(new HeadCommand("head"));
        register(new RankCommand("rank"));
        register(new DebugPlayerCommand("debugPlayer"));
        register(new DebugAchievementCommand("debugAchievement"));

        register(new SimpleAdminCommand("listProfiles") {
            @Override
            protected void execute(CommandSender commandSender, String[] strings) {
                Manager.current().listProfiles();
            }
        });

        register(new SimplePlayerAdminCommand("riptide") {

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
                npc.bukkitEntity().setInvisible(true);
                npc.showAll();

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

        register(new SimplePlayerAdminCommand("stopglow") {
            @Override
            protected void execute(Player player, String[] strings) {
                final Pig spawn = Entities.PIG.spawn(player.getLocation());

                Glowing.glowInfinitly(spawn, ChatColor.GOLD, player);

                Runnables.runLater(() -> {
                    Glowing.stopGlowing(player, spawn);
                    Chat.sendMessage(player, "Stop glowing");
                }, 60);
            }
        });

        register(new SimplePlayerAdminCommand("testtitleanimation") {
            @Override
            protected void execute(Player player, String[] args) {
                new TitleAnimation();
            }
        });

        register(new SimplePlayerAdminCommand("dumpBlockHardness") {
            @Override
            protected void execute(Player player, String[] strings) {
                Runnables.runAsync(() -> {
                    final Map<String, Float> hardness = Maps.newHashMap();

                    for (Material material : Material.values()) {
                        if (material.isBlock()) {
                            hardness.put(material.name(), material.getHardness());
                        }
                    }

                    final LinkedHashMap<String, Float> sorted = hardness.entrySet().stream()
                            .sorted(Map.Entry.comparingByValue())
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

                    final Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    final String json = gson.toJson(sorted);
                    final File path = new File(Main.getPlugin().getDataFolder(), "hardness.json");

                    hardness.clear();
                    sorted.clear();

                    try (FileWriter writer = new FileWriter(path)) {
                        writer.write(json);

                        Runnables.runSync(() -> {
                            Chat.sendMessage(player, "&aDumped into &e%s&a!", path.getAbsolutePath());
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });

        register(new SimplePlayerAdminCommand("spawnme") {

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

                npc.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
                npc.setPose(NPCPose.CROUCHING);

                npc.show(player);

                Chat.sendMessage(player, "&aSpawned!");
            }
        });

        register(new SimplePlayerAdminCommand("spawnDancingPiglin") {
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
                }.runTaskTimer(main, 0, 1);
            }
        });

        // these are small shortcuts not feeling creating a class D:

        register(tinyCommand("start", (player, args) -> {
            player.performCommand("cf start " + (args.length > 0 ? args[0] : ""));
        }));

        register(new SimpleAdminCommand("stop") {
            // true -> stop server, false -> stop game instance

            @Override
            protected void execute(CommandSender sender, String[] args) {
                final boolean type = args.length == 1 && (args[0].equalsIgnoreCase("server") || args[0].equalsIgnoreCase("s"));

                Bukkit.dispatchCommand(sender, type ? "minecraft:stop" : "cf stop");
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

    private void register(SimpleCommand command) {
        processor.registerCommand(command);
    }

}

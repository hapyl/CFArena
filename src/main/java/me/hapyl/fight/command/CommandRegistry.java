package me.hapyl.fight.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;
import me.hapyl.eterna.Eterna;
import me.hapyl.eterna.module.block.display.BDEngine;
import me.hapyl.eterna.module.block.display.DisplayData;
import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.chat.Gradient;
import me.hapyl.eterna.module.chat.LazyEvent;
import me.hapyl.eterna.module.chat.gradient.Interpolators;
import me.hapyl.eterna.module.command.*;
import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.hologram.Hologram;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.inventory.gui.EventListener;
import me.hapyl.eterna.module.inventory.gui.GUI;
import me.hapyl.eterna.module.inventory.gui.PlayerGUI;
import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.math.Cuboid;
import me.hapyl.eterna.module.math.Numbers;
import me.hapyl.eterna.module.math.nn.IntInt;
import me.hapyl.eterna.module.player.EffectType;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.player.PlayerSkin;
import me.hapyl.eterna.module.player.dialog.DialogInstance;
import me.hapyl.eterna.module.player.quest.Quest;
import me.hapyl.eterna.module.reflect.DataWatcherType;
import me.hapyl.eterna.module.reflect.Reflect;
import me.hapyl.eterna.module.reflect.glow.Glowing;
import me.hapyl.eterna.module.reflect.npc.Human;
import me.hapyl.eterna.module.reflect.npc.HumanNPC;
import me.hapyl.eterna.module.reflect.npc.NPCPose;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.*;
import me.hapyl.eterna.module.util.collection.Cache;
import me.hapyl.fight.CF;
import me.hapyl.fight.GVar;
import me.hapyl.fight.Main;
import me.hapyl.fight.Notifier;
import me.hapyl.fight.anticheat.PunishmentReport;
import me.hapyl.fight.build.NamedSignReader;
import me.hapyl.fight.chat.ChatChannel;
import me.hapyl.fight.database.Award;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.collection.AntiCheatCollection;
import me.hapyl.fight.database.collection.HeroStatsCollection;
import me.hapyl.fight.database.entry.DailyRewardEntry;
import me.hapyl.fight.database.entry.MasteryEntry;
import me.hapyl.fight.database.entry.MetadataEntry;
import me.hapyl.fight.database.entry.SkinEntry;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.filter.ProfanityFilter;
import me.hapyl.fight.fx.EntityFollowingParticle;
import me.hapyl.fight.fx.GiantItem;
import me.hapyl.fight.fx.Riptide;
import me.hapyl.fight.fx.beam.Quadrant;
import me.hapyl.fight.game.*;
import me.hapyl.fight.game.achievement.Achievement;
import me.hapyl.fight.game.achievement.AchievementRegistry;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.Attributes;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.challenge.ChallengeType;
import me.hapyl.fight.game.challenge.PlayerChallengeList;
import me.hapyl.fight.game.collectible.relic.Relic;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.CosmeticCollection;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.crate.Crates;
import me.hapyl.fight.game.crate.convert.CrateConvert;
import me.hapyl.fight.game.crate.convert.CrateConverts;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.*;
import me.hapyl.fight.game.entity.cooldown.Cooldown;
import me.hapyl.fight.game.entity.cooldown.CooldownData;
import me.hapyl.fight.game.entity.named.NamedEntityType;
import me.hapyl.fight.game.entity.shield.Shield;
import me.hapyl.fight.game.experience.Experience;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.PlayerRating;
import me.hapyl.fight.game.heroes.bloodfield.BatCloud;
import me.hapyl.fight.game.heroes.bloodfield.BloodfiendData;
import me.hapyl.fight.game.heroes.dark_mage.AnimatedWither;
import me.hapyl.fight.game.heroes.doctor.ElementType;
import me.hapyl.fight.game.heroes.mastery.HeroMastery;
import me.hapyl.fight.game.heroes.nyx.NyxData;
import me.hapyl.fight.game.loadout.HotBarSlot;
import me.hapyl.fight.game.lobby.LobbyItems;
import me.hapyl.fight.game.lobby.StartCountdown;
import me.hapyl.fight.game.maps.gamepack.GamePack;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.reward.DailyReward;
import me.hapyl.fight.game.reward.Reward;
import me.hapyl.fight.game.skin.Skins;
import me.hapyl.fight.game.talents.OverchargeUltimateTalent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.talents.engineer.Construct;
import me.hapyl.fight.game.talents.juju.Orbiting;
import me.hapyl.fight.game.talents.shaman.TotemPrison;
import me.hapyl.fight.game.talents.swooper.BlastPackEntity;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.game.team.Entry;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.game.ui.Season;
import me.hapyl.fight.game.ui.splash.SplashText;
import me.hapyl.fight.garbage.SynchronizedGarbageEntityCollector;
import me.hapyl.fight.github.Contributor;
import me.hapyl.fight.github.Contributors;
import me.hapyl.fight.gui.HeroPreviewGUI;
import me.hapyl.fight.gui.LegacyAchievementGUI;
import me.hapyl.fight.gui.styled.profile.DeliveryGUI;
import me.hapyl.fight.gui.styled.profile.achievement.AchievementGUI;
import me.hapyl.fight.infraction.HexID;
import me.hapyl.fight.loot.Loot;
import me.hapyl.fight.quest.CFQuestHandler;
import me.hapyl.fight.registry.Registries;
import me.hapyl.fight.script.Script;
import me.hapyl.fight.script.ScriptAction;
import me.hapyl.fight.script.Scripts;
import me.hapyl.fight.util.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.world.entity.Entity;
import org.bson.Document;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.List;
import java.util.Queue;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class CommandRegistry extends DependencyInjector<Main> implements Listener {

    private static Set<String> commands = Sets.newHashSet();

    private final CommandProcessor processor;

    public CommandRegistry(Main main) {
        super(main);

        CF.registerEvents(this);
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
        register(new ModifyParkourCommand("modifyParkour"));
        register(new InterruptCommand("interrupt"));
        register(new TestDatabaseCommand("testdatabase"));
        register(new EquipCommand("equip"));
        register(new HeadCommand("head"));
        register(new RankCommand("rank"));
        register(new DebugPlayerCommand("debugPlayer"));
        register(new DebugAchievementCommand("debugAchievement"));
        register(new ProfileCommand("profile"));
        register(new GVarCommand("gvar"));
        register(new PlayerAttributeCommand("playerAttribute"));
        register(new SnakeBuilderCommand("snakeBuilder"));
        register(new CrateCommandCommand("crate"));
        register(new GlobalConfigCommand("globalConfig"));
        register(new ArtifactCommand("artifact"));
        register(new RateHeroCommand("rateHero"));
        register(new ScriptCommand("script"));
        register(new TrialCommand("trial"));
        register(new GuessWhoCommand("guessWho"));
        register(new InviteCommand("invite"));
        register(new EmojisCommand("emojis"));
        register(new PersonalMessageCommand("tell"));
        register(new ReplyCommand("reply"));
        register(new GotoCommand("world"));
        register(new DumpHeroData("dumpHeroData"));
        register(new ArchetypeCommand("archetype"));
        register(new TermCommand("term", PlayerRank.DEFAULT));
        register(new MasteryCommand("mastery", PlayerRank.ADMIN));
        register(new LobbyCommand("lobby"));
        register(new HighlightLevel("highlightLevel"));
        register(new VehicleCommand("vehicle"));
        register(new FixMongoDbMigrationFiles("fixmongodbmigrationfiles"));
        register(new StartCountdownCommand("startCountdown"));
        register(new StoryCommand("story"));
        register(new StoreCommand("store"));

        // *=* Inner commands *=* //
        register("testserverlinks", (player, args) -> {
            final ServerLinks serverLinks = Bukkit.getServerLinks();
            try {
                final ServerLinks.ServerLink link = serverLinks.setLink(ServerLinks.Type.SUPPORT, new URI("testlink"));

                player.sendLinks(serverLinks);
                player.sendMessage("sent links!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        register("testvecrot", (player, args) -> {
            final Location location = player.getEyeLocation();
            final DirectionalMatrix matrix = new DirectionalMatrix(player.getEyeLocation().getDirection().setY(0.0d));

            PiHelper.rotate(300, 120, Math.PI / 16, d -> {
                final double x = Math.sin(d);
                final double z = Math.cos(d);

                matrix.transformLocation(location, x, 0, z, then -> {
                    PlayerLib.spawnParticle(location, Particle.COMPOSTER, 1);
                });
            });

            player.sendMessage(ChatColor.GREEN + "Displayed!");
        });

        register("testCooldown", (player, args) -> {
            final PlayerInventory inventory = player.getInventory();

            final ItemStack test1 = ItemBuilder.playerHeadUrl("2104f13c719f0c8af92f536dbd109285e6e3d21af3158ed91f603f7ecf7359b0")
                    .setCooldown(cd -> cd.setCooldownGroup(BukkitUtils.createKey("test_1")))
                    .toItemStack();
            final ItemStack test2 = ItemBuilder.playerHeadUrl("ce50e2e418b9d955837177ba643d2f75d4e7f593c3e1db6ee9d410741f43535e")
                    .setCooldown(cd -> cd.setCooldownGroup(BukkitUtils.createKey("test_2")))
                    .toItemStack();
            final ItemStack test3 = ItemBuilder.playerHeadUrl("ac964ed0f717ae671cdf0ed0e0341887ae8ccbd282c0058dc11276ef3cd78cc7")
                    .setCooldown(cd -> cd.setCooldownGroup(BukkitUtils.createKey("test_3")))
                    .toItemStack();
            final ItemStack test4 = ItemBuilder.playerHeadUrl("70fa3d8c2bad7be6196a21d43708e41454bc986a10856d604bbc2a0c21c2b91e")
                    .setCooldown(cd -> cd.setCooldownGroup(BukkitUtils.createKey("test_4")))
                    .toItemStack();

            inventory.clear();
            inventory.addItem(test1);
            inventory.addItem(test2);
            inventory.addItem(test3);
            inventory.addItem(test4);

            player.setCooldown(test1, 70);
            player.setCooldown(test2, 120);
            player.setCooldown(test3, 50);
            player.setCooldown(test4, 200);
        });

        register("testDecay", (player, args) -> {
            CF.getPlayerOptional(player)
                    .ifPresent(gp -> {
                        final double amount = args.get(0).toDouble();
                        final int duration = args.get(1).toInt();

                        if (amount <= 0.0d) {
                            gp.sendMessage("&cAmount cannot be zero or negative!");
                            return;
                        }

                        gp.setDecay(new Decay(amount, duration));
                        gp.sendMessage("&aApplied %s decay for %s!".formatted(amount, duration));
                    });
        });

        register(new CFCommand("testModel", PlayerRank.ADMIN) {

            @Override
            protected void execute(@Nonnull Player player, @Nonnull ArgumentList args, @Nonnull PlayerRank rank) {
                final DisplayData data = BDEngine.parse(
                        "/summon block_display ~-0.5 ~ ~-0.5 {Passengers:[{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;1608773508,-1271084427,-1742725610,-1723550642],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmFiOTIzMjIyNzY1MWYzYzk2YWE1M2Q2OGJiNDAxYWFjZWRiOTE3ODM2MDA1YTJhNGZmMDE3NWQ3YmVmOWU3YiJ9fX0=\"}]}}},item_display:\"none\",transformation:[1f,0f,0f,0.0006f,0f,1f,0f,2.8281f,0f,0f,1f,-0.0669f,0f,0f,0f,1f],Tags: [\"head\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;484189966,235562485,-426604314,1508866504],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDdmY2E4NjY0ODY0YTI0NmViNzY1ZmM3MjQ5N2U4YjFjOTE2MjI0NjE0MGUwOGU4ZWQ3Y2EwNGQ4MmRlNjBjMyJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.5f,0f,0f,0f,0f,0.8673f,0.0661f,2.3125f,0f,-0.1157f,0.4956f,0f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-421449231,-1477363886,1489181897,381866332],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDdmY2E4NjY0ODY0YTI0NmViNzY1ZmM3MjQ5N2U4YjFjOTE2MjI0NjE0MGUwOGU4ZWQ3Y2EwNGQ4MmRlNjBjMyJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.5f,0f,0f,0f,0f,0.875f,0f,1.9169f,0f,0f,0.5f,0.0481f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;1306925969,1905446903,2095328652,-1837142236],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDdmY2E4NjY0ODY0YTI0NmViNzY1ZmM3MjQ5N2U4YjFjOTE2MjI0NjE0MGUwOGU4ZWQ3Y2EwNGQ4MmRlNjBjMyJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.5f,0f,0f,0f,0f,0.8536f,-0.1099f,1.5275f,0f,0.1924f,0.4878f,0.0544f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;345578666,-1718594445,-808170745,8870679],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDdmY2E4NjY0ODY0YTI0NmViNzY1ZmM3MjQ5N2U4YjFjOTE2MjI0NjE0MGUwOGU4ZWQ3Y2EwNGQ4MmRlNjBjMyJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.5f,0f,0f,0f,0f,0.875f,0f,1.1306f,0f,0f,0.5f,-0.0375f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-312173002,1206235293,-1053032680,748162889],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjhhNzljNTEyY2ZmMWE5ZjQ3MzE5ZmQ3N2VmZDA2ZjY3M2E3NzJhYTk3NTk3NDM2YjAxNjBjNDA5YzhhMmU2MCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0f,-0.0514f,0.2022f,0.26f,-0.1875f,0f,0f,2.1181f,0f,-0.5542f,-0.0188f,-0.1469f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_wart_block\",Properties:{}},transformation:[0.0066f,0.3689f,0.0174f,-0.6169f,-0.0099f,0.246f,-0.026f,2.0756f,-0.3998f,0f,0.0009f,0.1606f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_wart_block\",Properties:{}},transformation:[0.0083f,0.084f,0.0312f,-0.3919f,-0.0009f,0.8883f,-0.0029f,0.5481f,-0.3999f,-0.0003f,0.0007f,0.1881f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;1408136501,-50173608,-2101705965,873901182],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDdmY2E4NjY0ODY0YTI0NmViNzY1ZmM3MjQ5N2U4YjFjOTE2MjI0NjE0MGUwOGU4ZWQ3Y2EwNGQ4MmRlNjBjMyJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.3813f,0f,0f,0f,0f,0.8671f,0.0533f,0.7956f,0f,-0.1172f,0.3939f,-0.0375f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_wart_block\",Properties:{}},transformation:[-0.3999f,-0.0003f,0.0007f,0.2075f,-0.0009f,1.0549f,-0.0029f,0.3869f,-0.0083f,-0.0997f,-0.0312f,0.3712f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_wart_block\",Properties:{}},transformation:[0.0083f,-0.084f,-0.0312f,0.4106f,0.0009f,0.8883f,-0.0029f,0.5472f,0.3999f,-0.0003f,0.0007f,-0.2312f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:black_concrete_powder\",Properties:{}},transformation:[-0.1465f,-0.0592f,-0.021f,0.3888f,0.0005f,0.9538f,-0.0029f,0.4894f,0.1338f,-0.068f,-0.023f,0.2044f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:basalt\",Properties:{axis:\"x\"}},transformation:[0.3375f,0f,0f,-0.1525f,0f,0.0949f,0f,1.4231f,0f,0f,0.1078f,0.16f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:basalt\",Properties:{axis:\"x\"}},transformation:[0f,0f,0.1078f,-0.3244f,0f,0.0949f,0f,1.4231f,-0.3375f,0f,0f,0.1644f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:basalt\",Properties:{axis:\"x\"}},transformation:[0f,0f,0.1078f,0.2338f,0f,0.0949f,0f,1.4231f,-0.3375f,0f,0f,0.1488f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:basalt\",Properties:{axis:\"x\"}},transformation:[0.1736f,0f,-0.0612f,0.18f,0f,0.0949f,0f,1.4231f,0.1198f,0f,0.0887f,-0.3256f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:basalt\",Properties:{axis:\"x\"}},transformation:[0.1736f,0f,-0.0612f,-0.2506f,0f,0.0949f,0f,1.4231f,0.1198f,0f,0.0887f,0.0725f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-1804519909,-550768275,-57154716,-339151212],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWM5ZmE3ODI4YjRiMzdlODI2MmQyYzIyMmEwNGY4ZTMyNjFhZjJhYjNiZDQ5NGVhODA3YTkzMmNjZDE4NGE1MCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0f,0f,-0.58f,0.0038f,0.4215f,0f,0f,1.45f,0f,-0.4125f,0f,-0.3675f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:basalt\",Properties:{axis:\"x\"}},transformation:[0.1736f,0f,0.0612f,-0.3312f,0f,0.0949f,0f,1.4231f,-0.1198f,0f,0.0887f,-0.1956f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:basalt\",Properties:{axis:\"x\"}},transformation:[0.1736f,0f,0.0612f,0.1025f,0f,0.0949f,0f,1.4231f,-0.1198f,0f,0.0887f,0.1919f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:black_concrete_powder\",Properties:{}},transformation:[-0.1465f,0.0592f,0.021f,-0.225f,-0.0005f,0.9538f,-0.0029f,0.4899f,-0.1338f,-0.068f,-0.023f,0.3475f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:black_concrete_powder\",Properties:{}},transformation:[-0.1465f,-0.0592f,0.021f,0.3677f,0.0005f,0.9538f,0.0029f,0.4865f,-0.1338f,0.068f,-0.023f,-0.2375f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:black_concrete_powder\",Properties:{}},transformation:[-0.1465f,0.0592f,-0.021f,-0.1977f,-0.0005f,0.9538f,0.0029f,0.4869f,0.1338f,0.068f,-0.023f,-0.3562f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;1030341735,-1035138481,979987612,1365477519],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzc3ZTQwYmIwYjM3MWIzNzQ1ZGZlNWVkNTIwMzNmZmNmYzhjODJmZWVmYzI1YjM0YmFjN2FlZDFmZjljZTU4ZiJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.2417f,0.0076f,0.1849f,-0.4006f,-0.0457f,0.0068f,0.9822f,1.1925f,0.0061f,-0.2498f,0.0323f,-0.1519f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:black_concrete_powder\",Properties:{}},transformation:[-0.0068f,0.0995f,-0.0311f,-0.3838f,-0.0003f,1.0552f,0.0029f,0.3856f,0.1486f,0.0069f,-0.0014f,-0.0912f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:black_concrete_powder\",Properties:{}},transformation:[-0.0068f,-0.0995f,0.0311f,0.4175f,0.0003f,1.0552f,0.0029f,0.3853f,-0.1486f,0.0069f,-0.0014f,0.0574f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-1697390209,94129626,713818941,-1638285711],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjhhNzljNTEyY2ZmMWE5ZjQ3MzE5ZmQ3N2VmZDA2ZjY3M2E3NzJhYTk3NTk3NDM2YjAxNjBjNDA5YzhhMmU2MCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0f,-0.4809f,0.1297f,0.0225f,-0.1875f,0f,0f,2.1223f,0f,-0.3992f,-0.1563f,-0.3212f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-1718111115,-1898299514,921829331,-470802009],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjhhNzljNTEyY2ZmMWE5ZjQ3MzE5ZmQ3N2VmZDA2ZjY3M2E3NzJhYTk3NTk3NDM2YjAxNjBjNDA5YzhhMmU2MCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0f,0.522f,0.0705f,0.3225f,-0.1875f,0f,0f,2.1223f,0f,-0.1931f,0.1905f,0.0638f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;1014034832,1161651296,1377053259,-1125806584],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjhhNzljNTEyY2ZmMWE5ZjQ3MzE5ZmQ3N2VmZDA2ZjY3M2E3NzJhYTk3NTk3NDM2YjAxNjBjNDA5YzhhMmU2MCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0f,-0.511f,0.1297f,0.0331f,-0.1875f,0f,0f,1.9661f,0f,-0.4242f,-0.1563f,-0.3487f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;934151430,100379817,-806574783,-1788822002],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjhhNzljNTEyY2ZmMWE5ZjQ3MzE5ZmQ3N2VmZDA2ZjY3M2E3NzJhYTk3NTk3NDM2YjAxNjBjNDA5YzhhMmU2MCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0f,-0.0525f,0.2022f,0.2831f,-0.1875f,0f,0f,1.9661f,0f,-0.5658f,-0.0188f,-0.1712f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-35686707,-125064048,-433943884,-1272401138],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjhhNzljNTEyY2ZmMWE5ZjQ3MzE5ZmQ3N2VmZDA2ZjY3M2E3NzJhYTk3NTk3NDM2YjAxNjBjNDA5YzhhMmU2MCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0f,0.4786f,0.0705f,0.3225f,-0.1875f,0f,0f,1.9661f,0f,-0.177f,0.1905f,0.0638f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-1564344584,669265178,2021242612,893332218],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjhhNzljNTEyY2ZmMWE5ZjQ3MzE5ZmQ3N2VmZDA2ZjY3M2E3NzJhYTk3NTk3NDM2YjAxNjBjNDA5YzhhMmU2MCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0f,-0.4809f,0.1297f,0.0775f,-0.1875f,0f,0f,1.8204f,0f,-0.3992f,-0.1563f,-0.3719f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-750633298,-417793487,-325781032,-1699740805],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjhhNzljNTEyY2ZmMWE5ZjQ3MzE5ZmQ3N2VmZDA2ZjY3M2E3NzJhYTk3NTk3NDM2YjAxNjBjNDA5YzhhMmU2MCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0f,-0.0533f,0.2022f,0.3181f,-0.1875f,0f,0f,1.8204f,0f,-0.5741f,-0.0188f,-0.2075f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;1259703469,975047208,-995808560,-17096679],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjhhNzljNTEyY2ZmMWE5ZjQ3MzE5ZmQ3N2VmZDA2ZjY3M2E3NzJhYTk3NTk3NDM2YjAxNjBjNDA5YzhhMmU2MCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0f,0.5384f,0.0705f,0.3588f,-0.1875f,0f,0f,1.8242f,0f,-0.1992f,0.1905f,0.0638f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;1952546901,865210129,-1958054609,1357939637],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjhhNzljNTEyY2ZmMWE5ZjQ3MzE5ZmQ3N2VmZDA2ZjY3M2E3NzJhYTk3NTk3NDM2YjAxNjBjNDA5YzhhMmU2MCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0f,-0.4809f,0.1297f,0.0813f,-0.1875f,0f,0f,1.6829f,0f,-0.3992f,-0.1563f,-0.3937f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;696774,-198365510,-1789390588,2087795399],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjhhNzljNTEyY2ZmMWE5ZjQ3MzE5ZmQ3N2VmZDA2ZjY3M2E3NzJhYTk3NTk3NDM2YjAxNjBjNDA5YzhhMmU2MCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0f,-0.0104f,0.2031f,0.3106f,-0.1875f,0f,0f,1.6829f,0f,-0.5965f,-0.0035f,-0.205f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;2116999602,-524170851,-917280352,436445215],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjhhNzljNTEyY2ZmMWE5ZjQ3MzE5ZmQ3N2VmZDA2ZjY3M2E3NzJhYTk3NTk3NDM2YjAxNjBjNDA5YzhhMmU2MCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0f,0.4786f,0.0705f,0.3225f,-0.1875f,0f,0f,1.6829f,0f,-0.177f,0.1905f,0.0712f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;455388714,537982893,-1389492851,1977883781],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjhhNzljNTEyY2ZmMWE5ZjQ3MzE5ZmQ3N2VmZDA2ZjY3M2E3NzJhYTk3NTk3NDM2YjAxNjBjNDA5YzhhMmU2MCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.1875f,0f,0f,-0.005f,0f,-0.6232f,0.0156f,1.8773f,0f,-0.0479f,-0.2025f,-0.3381f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;906127004,-565115624,2046065901,1894789699],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjhhNzljNTEyY2ZmMWE5ZjQ3MzE5ZmQ3N2VmZDA2ZjY3M2E3NzJhYTk3NTk3NDM2YjAxNjBjNDA5YzhhMmU2MCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0f,0.4809f,-0.1297f,-0.0275f,0.1875f,0f,0f,2.1223f,0f,-0.3992f,-0.1563f,-0.3325f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-860674204,-1858772280,-1256694480,159362818],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjhhNzljNTEyY2ZmMWE5ZjQ3MzE5ZmQ3N2VmZDA2ZjY3M2E3NzJhYTk3NTk3NDM2YjAxNjBjNDA5YzhhMmU2MCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0f,0.0514f,-0.2022f,-0.265f,0.1875f,0f,0f,2.1223f,0f,-0.5542f,-0.0188f,-0.1719f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-189659764,1553016431,745501483,1688094811],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjhhNzljNTEyY2ZmMWE5ZjQ3MzE5ZmQ3N2VmZDA2ZjY3M2E3NzJhYTk3NTk3NDM2YjAxNjBjNDA5YzhhMmU2MCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0f,-0.522f,-0.0705f,-0.3275f,0.1875f,0f,0f,2.1223f,0f,-0.1931f,0.1905f,0.0431f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-767039326,184731349,1831571602,-875157595],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjhhNzljNTEyY2ZmMWE5ZjQ3MzE5ZmQ3N2VmZDA2ZjY3M2E3NzJhYTk3NTk3NDM2YjAxNjBjNDA5YzhhMmU2MCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0f,0.511f,-0.1297f,-0.0381f,0.1875f,0f,0f,1.9661f,0f,-0.4242f,-0.1563f,-0.36f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;1594331702,-290086596,-361166661,-592086127],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjhhNzljNTEyY2ZmMWE5ZjQ3MzE5ZmQ3N2VmZDA2ZjY3M2E3NzJhYTk3NTk3NDM2YjAxNjBjNDA5YzhhMmU2MCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0f,0.0525f,-0.2022f,-0.2881f,0.1875f,0f,0f,1.9661f,0f,-0.5658f,-0.0188f,-0.1919f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;1179139773,-30725095,402372491,-1400261363],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjhhNzljNTEyY2ZmMWE5ZjQ3MzE5ZmQ3N2VmZDA2ZjY3M2E3NzJhYTk3NTk3NDM2YjAxNjBjNDA5YzhhMmU2MCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0f,-0.4786f,-0.0705f,-0.3275f,0.1875f,0f,0f,1.9661f,0f,-0.177f,0.1905f,0.0431f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-1081594423,6785381,-547401130,-256503738],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjhhNzljNTEyY2ZmMWE5ZjQ3MzE5ZmQ3N2VmZDA2ZjY3M2E3NzJhYTk3NTk3NDM2YjAxNjBjNDA5YzhhMmU2MCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0f,0.4809f,-0.1297f,-0.0825f,0.1875f,0f,0f,1.8204f,0f,-0.3992f,-0.1563f,-0.3831f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-1023389741,-1951715326,864261281,747551552],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjhhNzljNTEyY2ZmMWE5ZjQ3MzE5ZmQ3N2VmZDA2ZjY3M2E3NzJhYTk3NTk3NDM2YjAxNjBjNDA5YzhhMmU2MCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0f,0.0533f,-0.2022f,-0.3231f,0.1875f,0f,0f,1.8204f,0f,-0.5741f,-0.0188f,-0.2281f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;1927598337,998386509,-873258299,-98640829],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjhhNzljNTEyY2ZmMWE5ZjQ3MzE5ZmQ3N2VmZDA2ZjY3M2E3NzJhYTk3NTk3NDM2YjAxNjBjNDA5YzhhMmU2MCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0f,-0.5384f,-0.0705f,-0.3638f,0.1875f,0f,0f,1.8242f,0f,-0.1992f,0.1905f,0.0431f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;1191560378,-641784127,2093574723,-1024253872],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjhhNzljNTEyY2ZmMWE5ZjQ3MzE5ZmQ3N2VmZDA2ZjY3M2E3NzJhYTk3NTk3NDM2YjAxNjBjNDA5YzhhMmU2MCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0f,0.4809f,-0.1297f,-0.0862f,0.1875f,0f,0f,1.6829f,0f,-0.3992f,-0.1563f,-0.39f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-229347471,-1428962173,519286072,-1329624916],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjhhNzljNTEyY2ZmMWE5ZjQ3MzE5ZmQ3N2VmZDA2ZjY3M2E3NzJhYTk3NTk3NDM2YjAxNjBjNDA5YzhhMmU2MCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0f,0.0104f,-0.2031f,-0.3156f,0.1875f,0f,0f,1.6829f,0f,-0.5965f,-0.0035f,-0.2256f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-1938573160,-1088319532,1731905068,467520481],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjhhNzljNTEyY2ZmMWE5ZjQ3MzE5ZmQ3N2VmZDA2ZjY3M2E3NzJhYTk3NTk3NDM2YjAxNjBjNDA5YzhhMmU2MCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0f,-0.4786f,-0.0705f,-0.3275f,0.1875f,0f,0f,1.6829f,0f,-0.177f,0.1905f,0.0506f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone\",Properties:{}},transformation:[0.0346f,-0.0681f,0f,-0.3456f,0.0075f,0.315f,0f,1.5031f,0f,0f,0.4438f,-0.2681f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:netherite_block\",Properties:{}},transformation:[0.0354f,0.0157f,0f,-0.4188f,-0.0017f,0.3219f,0f,1.8119f,0f,0f,0.4438f,-0.2681f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone\",Properties:{}},transformation:[0.0346f,0.0672f,0.0011f,0.3213f,-0.0074f,0.3152f,-0.0016f,1.5106f,-0.0001f,0.0009f,0.4438f,-0.2681f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:netherite_block\",Properties:{}},transformation:[0.0354f,-0.0157f,0f,0.3836f,0.0017f,0.3219f,0f,1.8101f,0f,0f,0.4438f,-0.2681f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone\",Properties:{}},transformation:[0f,0f,-0.4438f,0.2244f,-0.0075f,0.315f,0f,1.5106f,0.0346f,0.0681f,0f,0.2188f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone\",Properties:{}},transformation:[0f,0f,-0.4438f,0.2244f,0.0017f,0.3219f,0f,1.8101f,0.0354f,-0.0157f,0f,0.2811f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone\",Properties:{}},transformation:[0.0201f,0.0396f,-0.1895f,0.3456f,-0.0075f,0.315f,0f,1.5105f,0.0281f,0.0554f,0.1356f,0.1044f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:netherite_block\",Properties:{}},transformation:[0.0206f,-0.0092f,-0.1895f,0.3819f,0.0017f,0.3219f,0f,1.81f,0.0287f,-0.0128f,0.1356f,0.1551f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone\",Properties:{}},transformation:[0.0201f,-0.0396f,0.1895f,-0.3625f,0.0075f,0.315f,0f,1.503f,-0.0281f,0.0554f,0.1356f,0.1325f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:netherite_block\",Properties:{}},transformation:[0.0206f,0.0092f,0.1895f,-0.3992f,-0.0017f,0.3219f,0f,1.8117f,-0.0287f,-0.0128f,0.1356f,0.1838f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:netherite_block\",Properties:{}},transformation:[0f,0f,-0.4272f,0.2244f,0.0237f,0.1094f,0f,2.1269f,0.0263f,-0.0989f,0f,0.2663f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone\",Properties:{}},transformation:[-0.0208f,0.1193f,0f,-0.3881f,0.0286f,0.0867f,0f,2.1269f,0f,0f,-0.3994f,0.1356f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone\",Properties:{}},transformation:[-0.0207f,-0.1196f,-0.001f,0.4061f,-0.0287f,0.0864f,0.0014f,2.1543f,0f,0.0006f,-0.405f,0.1414f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:netherite_block\",Properties:{}},transformation:[0.0155f,-0.0621f,-0.1893f,0.3819f,0.0241f,0.1181f,0.0043f,2.1106f,0.0208f,-0.0903f,0.1358f,0.1551f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:netherite_block\",Properties:{}},transformation:[0.0155f,0.0621f,0.1893f,-0.4f,-0.0241f,0.1181f,0.0043f,2.1347f,-0.0208f,-0.0903f,0.1358f,0.1759f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone\",Properties:{}},transformation:[0.0201f,0.0396f,0.1895f,0.1462f,-0.0075f,0.315f,0f,1.5105f,-0.0281f,-0.0554f,0.1356f,-0.3294f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone\",Properties:{}},transformation:[0.0201f,-0.0396f,-0.1895f,-0.1594f,0.0075f,0.315f,0f,1.503f,0.0281f,-0.0554f,0.1356f,-0.3575f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:netherite_block\",Properties:{}},transformation:[0.0209f,-0.0093f,0.1209f,0.2581f,0.0017f,0.3219f,0f,1.81f,-0.0285f,0.0127f,0.0888f,-0.3306f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:netherite_block\",Properties:{}},transformation:[0.0209f,0.0093f,-0.1209f,-0.2631f,-0.0017f,0.3219f,0f,1.8117f,0.0285f,0.0127f,0.0888f,-0.3591f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;2064011671,495645899,1452086140,501745066],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzc3ZTQwYmIwYjM3MWIzNzQ1ZGZlNWVkNTIwMzNmZmNmYzhjODJmZWVmYzI1YjM0YmFjN2FlZDFmZjljZTU4ZiJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.2351f,0.0083f,0.2932f,-0.4006f,-0.0703f,-0.0363f,0.9473f,1.1731f,0.0182f,-0.2472f,-0.1291f,0.0263f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_wart_block\",Properties:{}},transformation:[0.0072f,-0.3527f,-0.019f,0.6106f,0.0094f,0.2687f,-0.0249f,2.0563f,0.3998f,0f,0.0009f,-0.2392f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:chain\",Properties:{axis:\"x\"}},transformation:[-0.0783f,-0.4569f,0.0132f,0.3962f,0.5001f,-0.0716f,-0.084f,0.9431f,0.0802f,0f,0.5371f,-0.6425f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:chain\",Properties:{axis:\"x\"}},transformation:[0.008f,-0.4624f,-0.0013f,0.1162f,0.5061f,0.0073f,-0.0851f,0.9231f,0.0802f,0f,0.5371f,-0.6425f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:crimson_roots\",Properties:{}},transformation:[0.7f,0f,0f,-0.3306f,0f,1f,0f,1.5563f,0f,0f,0.45f,-0.3919f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_wart_block\",Properties:{}},transformation:[-0.3999f,0.0039f,0.0005f,0.1987f,-0.0011f,0.3573f,-0.0185f,2.0256f,-0.0074f,-0.2625f,-0.0252f,0.4287f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_wart_block\",Properties:{}},transformation:[-0.3286f,-0.0836f,0.013f,-0.1031f,-0.1742f,0.1944f,-0.0223f,2.1731f,-0.021f,-0.3052f,-0.0177f,0.4263f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_wart_block\",Properties:{}},transformation:[-0.3286f,0.0836f,-0.013f,0.425f,0.1742f,0.1944f,-0.0223f,1.9989f,0.021f,-0.3052f,-0.0177f,0.4053f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:chain\",Properties:{axis:\"x\"}},transformation:[0.1894f,-0.0965f,-0.3481f,0.3663f,0.0616f,0.4486f,-0.0207f,2.0194f,0.1611f,-0.0582f,0.4173f,-0.5606f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:chain\",Properties:{axis:\"x\"}},transformation:[0.1894f,0.0965f,0.3481f,-0.5556f,-0.0616f,0.4486f,-0.0207f,2.081f,-0.1611f,-0.0582f,0.4173f,-0.3995f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:chain\",Properties:{axis:\"x\"}},transformation:[0.3519f,0.0154f,-0.0193f,-0.1788f,-0.0125f,0.4613f,-0.0334f,2.0194f,0.0117f,0.029f,0.5424f,-0.6619f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:black_concrete_powder\",Properties:{}},transformation:[-0.0873f,-0.1592f,0.0231f,0.5988f,0.3833f,0.0009f,0.009f,1.7269f,-0.0741f,0.192f,0.0191f,0.1506f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:black_concrete_powder\",Properties:{}},transformation:[-0.049f,-0.2241f,0.0132f,0.4094f,0.3833f,0.0009f,0.009f,1.7269f,-0.1035f,0.1095f,0.0269f,0.365f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:black_concrete_powder\",Properties:{}},transformation:[-0.0873f,0.1592f,-0.0231f,-0.5338f,-0.3833f,0.0009f,0.009f,2.1101f,0.0741f,0.192f,0.0191f,0.0765f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:black_concrete_powder\",Properties:{}},transformation:[-0.049f,0.2241f,-0.0132f,-0.3826f,-0.3833f,0.0009f,0.009f,2.1101f,0.1035f,0.1095f,0.0269f,0.2615f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:black_concrete_powder\",Properties:{}},transformation:[-0.1767f,-0.0484f,-0.021f,0.5288f,0.0005f,0.7809f,-0.0029f,1.0738f,0.1613f,-0.0557f,-0.023f,0.3138f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:black_concrete_powder\",Properties:{}},transformation:[-0.1767f,0.0484f,0.021f,-0.4019f,-0.0005f,0.7809f,-0.0029f,1.0806f,-0.1613f,-0.0557f,-0.023f,0.4575f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:chain\",Properties:{axis:\"x\"}},transformation:[-0.0573f,-0.4596f,0.0017f,0.7169f,0.5069f,-0.0514f,0.053f,0.6463f,-0.0495f,0.0065f,0.5412f,0.1756f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:chain\",Properties:{axis:\"x\"}},transformation:[-0.0573f,0.4596f,-0.0017f,-0.6763f,-0.5069f,-0.0514f,0.053f,1.1531f,0.0495f,0.0065f,0.5412f,0.07f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_wart_block\",Properties:{}},transformation:[-0.3999f,-0.0004f,0.0007f,0.2075f,-0.0004f,1.2574f,-0.0012f,0.7506f,-0.0084f,-0.0483f,-0.0313f,0.4706f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:netherite_block\",Properties:{}},transformation:[0.11f,0f,0.0585f,0.2269f,-0.039f,0.0929f,0.0739f,2.2056f,-0.0433f,-0.0836f,0.0821f,-0.1581f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:netherite_block\",Properties:{}},transformation:[0.11f,0f,-0.0585f,-0.3206f,0.039f,0.0929f,0.0739f,2.1666f,0.0433f,-0.0836f,0.0821f,-0.2014f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:weeping_vines\",Properties:{}},transformation:[-0.0797f,0f,-0.0661f,0.4562f,0f,1f,0f,-0.4562f,0.9113f,0f,-0.0058f,-0.4775f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:weeping_vines\",Properties:{}},transformation:[0.0208f,0f,0.0664f,-0.3862f,0f,1f,0f,-0.4375f,-0.9146f,0f,0.0015f,0.4914f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:weeping_vines\",Properties:{}},transformation:[-0.9148f,0f,0.0001f,0.4662f,0f,1f,0f,-0.5f,-0.0016f,0f,-0.0664f,0.3746f,0f,0f,0f,1f],Tags: [\"torso\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone\",Properties:{}},transformation:[0.5685f,0.1075f,0f,0.3125f,-0.232f,0.2634f,0f,1.8801f,0f,0f,0.265f,-0.1631f,0f,0f,0f,1f],Tags: [\"right_arm\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-1931543666,916090057,-269574239,230189945],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDdmY2E4NjY0ODY0YTI0NmViNzY1ZmM3MjQ5N2U4YjFjOTE2MjI0NjE0MGUwOGU4ZWQ3Y2EwNGQ4MmRlNjBjMyJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.0911f,0.9509f,0f,1.346f,-0.3399f,0.2548f,0f,1.8694f,0f,0f,0.3188f,-0.0331f,0f,0f,0f,1f],Tags: [\"right_arm\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-812878447,1562157620,1157935521,2071378794],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDdmY2E4NjY0ODY0YTI0NmViNzY1ZmM3MjQ5N2U4YjFjOTE2MjI0NjE0MGUwOGU4ZWQ3Y2EwNGQ4MmRlNjBjMyJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.0339f,0.5359f,-0.1829f,1.5954f,-0.1058f,0.2376f,0.2954f,1.9956f,0.0968f,0.0719f,0.3869f,-0.0006f,0f,0f,0f,1f],Tags: [\"right_arm\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_wart_block\",Properties:{}},transformation:[0.0576f,0.113f,0f,0.864f,-0.0243f,0.2676f,0f,1.6537f,0f,0f,0.2969f,-0.175f,0f,0f,0f,1f],Tags: [\"right_arm\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_wart_block\",Properties:{}},transformation:[0.0576f,-0.113f,0f,-0.9465f,0.0243f,0.2676f,0f,1.6294f,0f,0f,0.2969f,-0.175f,0f,0f,0f,1f],Tags: [\"right_arm\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;1642989349,-9395865,-1938977434,1026065519],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjE4YzRlYzZmMTQ5MDk0MDNkNmZiMWNlMzExODRmZTkyZTFkOGI5MGI4ZTE5MTFlNDM4NjEyMTg4ZjUwMmE5MyJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.1791f,-0.4651f,0.4193f,-1.5431f,0.3409f,0.4448f,0.3354f,2.3925f,-0.5314f,0.1286f,0.3565f,0.0238f,0f,0f,0f,1f],Tags: [\"right_arm\"]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone\",Properties:{}},transformation:[0.5685f,-0.1075f,0f,-0.9059f,0.232f,0.2634f,0f,1.6481f,0f,0f,0.265f,-0.1631f,0f,0f,0f,1f],Tags: [\"right_arm\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-665736015,-1005443466,307293432,-1664422451],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDdmY2E4NjY0ODY0YTI0NmViNzY1ZmM3MjQ5N2U4YjFjOTE2MjI0NjE0MGUwOGU4ZWQ3Y2EwNGQ4MmRlNjBjMyJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.0911f,-0.9509f,0f,-1.3709f,0.3399f,0.2548f,0f,1.8694f,0f,0f,0.3188f,-0.0331f,0f,0f,0f,1f],Tags: [\"right_arm\"]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-2118636653,751873036,344721982,1851737464],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDdmY2E4NjY0ODY0YTI0NmViNzY1ZmM3MjQ5N2U4YjFjOTE2MjI0NjE0MGUwOGU4ZWQ3Y2EwNGQ4MmRlNjBjMyJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.0466f,-0.5594f,0.0278f,-1.659f,0.1397f,0.1875f,0.0099f,1.95f,-0.0051f,0.0264f,0.5192f,-0.015f,0f,0f,0f,1f],Tags: [\"right_arm\"]}]}"
                );

                data.spawn(player.getLocation());
            }
        });

        register("resetquest", (player, args) -> {
            final String stringKey = args.getString(0);
            final Key key = Key.ofStringOrNull(stringKey);

            if (key == null) {
                Notifier.error(player, "Invalid key {%s}.".formatted(stringKey));
                return;
            }

            final CFQuestHandler questHandler = CF.getQuestHandler();
            final Quest quest = questHandler.get(key);

            if (quest == null) {
                Notifier.error(player, "Invalid quest {%s}.".formatted(key.getKey()));
                return;
            }

            CF.getDatabase(player).questEntry.resetQuest(quest);
            Notifier.success(player, "Reset quest {%s}!".formatted(quest.getName()));
        });

        register("distanceToLos", (player, args) -> {
            final Block targetBlock = player.getTargetBlockExact(10);

            if (targetBlock == null) {
                Notifier.error(player, "No block in los!");
                return;
            }

            final Location location = player.getLocation();
            final Location blockLocation = targetBlock.getLocation();

            final double distanceSquared = blockLocation.distanceSquared(location);
            final double distance = blockLocation.distance(location);

            Notifier.success(
                    player,
                    "Distance to {%s} is {%.2f} (√{%.2f})".formatted(
                            targetBlock.getType().name().toLowerCase(),
                            distanceSquared,
                            distance
                    )
            );
        });

        register("metadata", (player, args) -> {
            final MetadataEntry entry = CF.getDatabase(player).metadataEntry;

            if (args.length == 0) {
                final Map<String, Object> mapped = MetadataEntry.map(player);

                int count = 0;
                for (Map.Entry<String, Object> mapEntry : mapped.entrySet()) {
                    final String key = mapEntry.getKey();
                    final Object value = mapEntry.getValue();

                    final ChatColor color = (count++ % 2 == 0 ? ChatColor.GREEN : ChatColor.DARK_GREEN);

                    Chat.sendClickableHoverableMessage(
                            player,
                            LazyEvent.runCommand("/metadata %s remove".formatted(key)),
                            LazyEvent.showText("&eClick to remove '%s'!".formatted(key)),
                            "%s &d&l= %s".formatted(color + key, color + value.toString())
                    );
                }

                return;
            }

            final String argument = args.get(0).toString();

            MetadataEntry.MetadataParent parent;
            String keyString;
            Key key;

            if (argument.contains(".")) {
                final String[] keySplits = argument.split("\\.");

                parent = entry.getParent(keySplits[0]);
                key = Key.ofStringOrNull(keyString = keySplits[1]);
            }
            else {
                parent = entry.NULL;
                key = Key.ofStringOrNull(keyString = argument);
            }

            if (key == null) {
                Notifier.error(player, "Illegal key; " + keyString);
                return;
            }

            final boolean isRemove = args.get(1).toString().equalsIgnoreCase("remove");

            if (isRemove) {
                parent.set(key, null);

                Notifier.success(player, "Removed metadata for key {%s}.".formatted(key));
            }
            else {
                final Object value = parent.get(key, null);

                if (value == null) {
                    Notifier.error(player, "There is no metadata for key {%s}!".formatted(key));
                }
                else {
                    Notifier.success(player, "Metadata for key {%s}: {%s}".formatted(key, value.toString()));
                }
            }
        });

        register("setTalkedDialog", (player, args) -> {
            final PlayerDatabase database = CF.getDatabase(player);
            final String id = args.get(0).toString();
            final boolean value = args.get(0).toBoolean();

            final Key key = Key.ofStringOrNull(id);

            if (key == null) {
                Notifier.error(player, "Invalid key: " + id);
                return;
            }

            database.metadataEntry.DIALOG.set(key, !value ? null : true);
            Notifier.success(player, "Set dialog metadata for dialog '{%s}' to {%s}.".formatted(key, value));
        });

        register("skipDialog", (player, args) -> {
            final DialogInstance dialog = Eterna.getManagers().dialog.get(player);

            if (dialog == null) {
                Notifier.error(player, "You're not in a dialog!");
                return;
            }

            dialog.cancel();
            Notifier.success(player, "Skipped.");
        });

        register("removeFoundRelic", (player, args) ->

        {
            final int id = args.getInt(0);
            final Relic relic = CF.getPlugin().getRelicHunt().byId(id);

            if (relic == null) {
                Notifier.error(player, "Invalid relic: " + id);
                return;
            }

            relic.take(player);
        });

        register("calculatePointReward", (player, args) ->

        {
            final AchievementRegistry registry = Registries.getAchievements();

            final String query = args.getString(0);
            final int completeCount = args.getInt(1);
            final Achievement achievement = registry.get(query);

            if (achievement == null) {
                Notifier.error(player, "Invalid achievement: {%s}".formatted(query));
                return;
            }

            final int reward = achievement.getPointRewardForCompleting(completeCount);

            Notifier.success(
                    player,
                    "Completing {%s} {%s} times grants {%s} points.".formatted(achievement.getName(), completeCount, reward)
            );
        });

        register(new SimplePlayerAdminCommand("testLoot") {

            private Loot testLoot;

            @Override
            protected void execute(Player player, String[] args) {
                if (testLoot == null) {
                    testLoot = new Loot();

                    testLoot.add(Reward.currency("test1").withCoins(1), 500);
                    testLoot.add(Reward.currency("test2").withCoins(20), 500);
                    testLoot.add(Reward.currency("test3").withCoins(30), 400);
                    testLoot.add(Reward.currency("test4").withCoins(100), 1);
                }

                Debug.info("Loot info:");

                for (WeightedCollection<Reward>.WeightedElement element : testLoot.getWeightedElements()) {
                    Debug.info(element.toString());
                }

                int lootTestTimes = 1000;
                Debug.info("Picking a random loot " + lootTestTimes + " times...");

                Map<Reward, Integer> testedLoot = new LinkedHashMap<>();

                for (int i = 0; i < lootTestTimes; i++) {
                    final Reward reward = testLoot.getRandomElement();

                    testedLoot.compute(reward, Compute.intAdd());
                }

                final Set<Map.Entry<Reward, Integer>> entries = testedLoot.entrySet();
                final List<Map.Entry<Reward, Integer>> testedLootSorted = entries.stream()
                        .sorted(Map.Entry.comparingByValue())
                        .toList();

                Debug.info("Results:");
                for (Map.Entry<Reward, Integer> entry : testedLootSorted) {
                    final Reward key = entry.getKey();
                    final Integer value = entry.getValue();

                    Debug.info("%s = %s".formatted(key.toString(), value));
                }

            }
        });

        register("testcustomtextcolor", (player, args) ->

        {
            final net.kyori.adventure.text.TextComponent component = Component
                    .text("This is a test")
                    .color(me.hapyl.fight.game.color.Color.WITHERS);

            player.sendMessage(component);
        });

        register("testUicmp", (player, args) ->

        {
            GamePlayer.getPlayerOptional(player)
                    .ifPresent(gp -> {
                        gp.ui(GamePlayer.class, "Test from game player yay!");
                    });
        });

        register("ridePiggy", (player, args) ->

        {
            final Pig pig = Entities.PIG.spawn(player.getLocation().add(0, 2, 0), self -> {
                self.setGravity(false);
            });

            pig.setHealth(1);
            pig.addPassenger(player);

            player.sendRichMessage("<green>Done!");
        });

        register("addAuroraBuff", (player, args) ->

        {
            GamePlayer.getPlayerOptional(player)
                    .ifPresent(gp -> {
                        HeroRegistry.AURORA.getPlayerData(gp).buff(gp);
                    });
        });

        register("hurtMe", (player, args) ->

        {
            GamePlayer.getPlayerOptional(player).ifPresent(gp -> gp.damage(gp.getHealth() * 0.99));
        });

        register("testTotemOffset", (player, args) ->

        {
            final Location location = player.getLocation();
            final int[][] offsets = TotemPrison.OFFSETS;

            new TickingGameTask() {
                @Override
                public void run(int tick) {
                    if (tick >= offsets.length) {
                        cancel();
                        return;
                    }

                    final int[] offset = offsets[tick];
                    final int x = offset[0];
                    final int z = offset[1];

                    location.add(x, 0, z);
                    location.getBlock().setType(Material.REDSTONE_BLOCK, false);
                    location.subtract(x, 0, z);
                }
            }.runTaskTimer(5, 5);
        });

        register("scaryWither", (player, args) ->

        {
            GamePlayer.getPlayerOptional(player)
                    .ifPresent(gp -> {
                        TalentRegistry.BLINDING_CURSE.scaryWither(gp);
                        gp.sendMessage("&8Boo!");
                    });
        });

        register("whatsmyenergy", (player, args) ->

        {
            GamePlayer.getPlayerOptional(player)
                    .ifPresent(gp -> {
                        final double energy = gp.getEnergy();
                        final UltimateTalent ultimate = gp.getUltimate();

                        gp.sendMessage("&aYour energy: %s".formatted(energy));

                        if (ultimate instanceof OverchargeUltimateTalent overcharge) {
                            gp.sendMessage("&bUltimate cost: %s/%s".formatted(overcharge.getMinCost(), overcharge.getCost()));
                        }
                        else {
                            gp.sendMessage("&bUltimate cost: %s".formatted(ultimate.getCost()));
                        }

                        gp.sendMessage("&cString: " + gp.getUltimateString(UltimateColor.PRIMARY));
                    });
        });

        register("loadClass", (player, args) ->

        {
            final String classToLoad = args.getString(0);

            if (classToLoad.isEmpty()) {
                player.sendRichMessage("<dark_red>Missing class name!");
                return;
            }

            player.sendRichMessage("<yellow>Loading class '%s'...".formatted(classToLoad));

            try {
                final Class<?> clazz = Class.forName(classToLoad);

                player.sendRichMessage("<green>Loaded class '%s'!".formatted(clazz.getName()));
            } catch (ClassNotFoundException e) {
                player.sendRichMessage("<dark_red>No such class '%s'!".formatted(classToLoad));
            }
        });

        register("testHealthTemper", (player, args) ->

        {
            GamePlayer.getPlayerOptional(player)
                    .ifPresent(gp -> {
                        gp.getAttributes().increaseTemporary(Temper.COMMAND, AttributeType.MAX_HEALTH, 50, 100);
                        gp.sendMessage("Increase health!");
                    });
        });

        register("testMongoSerialize", (player, args) ->

        {
            if (args.length == 0) {
                final List<PunishmentReport> reports = AntiCheatCollection.get(player.getUniqueId());

                player.sendMessage(CollectionUtils.wrapToString(reports));
                return;
            }

            // deserialize
            final HexID hexId = HexID.fromStringOrEmpty(args.getString(0));
            final PunishmentReport report = AntiCheatCollection.get(hexId);

            if (report == null) {
                player.sendRichMessage("<rainbow:2>No such report!");
                player.sendRichMessage("<rainbow:4>No such report!");
                player.sendRichMessage("<rainbow:6>No such report!");
                return;
            }

            player.sendMessage(report.toString());
        });

        register("testitemwithdefaultattributes", (player, args) ->

        {
            final Material material = args.get(0).toEnum(Material.class);
            final boolean hideFlags = args.get(0).toBoolean();

            final ItemBuilder builder = new ItemBuilder(material);

            if (hideFlags) {
                builder.hideFlag(ItemFlag.values());
            }

            player.getInventory().addItem(builder.toItemStack());
        });


        register("testoptional", (player, args) ->

        {
            final IOptional<Object> nullOptional = IOptional.of(null);
            final IOptional<Player> playerOptional = IOptional.of(player);

            nullOptional.ifPresent(__ -> {
                player.sendMessage("nullOptional ifPresent");
            }).orElse(() -> {
                player.sendMessage("nullOptional orElse");
            }).always(() -> {
                player.sendMessage("nullOptional always");
            });

            playerOptional.ifPresent(__ -> {
                player.sendMessage("playerOptional ifPresent");
            }).orElse(() -> {
                player.sendMessage("playerOptional orElse");
            }).always(() -> {
                player.sendMessage("playerOptional always");
            });
        });

        register("resistcrowncontrol", (player, args) ->

        {
            GamePlayer.getPlayerOptional(player)
                    .ifPresent(gp -> {
                        gp.sendMessage("resisted");
                        gp.hasEffectResistanceAndNotify();
                    });
        });

        register(new SimplePlayerAdminCommand("testCacheSet") {

            Cache<String> set = Cache.ofSet(2000);

            @Override
            protected void execute(Player player, String[] args) {
                if (args.length == 0) {
                    final HashSet<String> copy = new HashSet<>(set);

                    player.sendMessage("set=" + CollectionUtils.wrapToString(copy));
                    copy.clear();
                    return;
                }

                set.add(args[0]);
            }
        });

        register("playMasteryLevelUpEffect", (player, args) ->

        {
            final MasteryEntry entry = CF.getDatabase(player).masteryEntry;
            final Hero hero = Manager.current().getSelectedLobbyHero(player);
            final int level = entry.getLevel(hero);

            entry.playMasteryLevelUpEffect(hero, Math.max(level - 1, 0), level);
        });

        register("masteryExp", (player, args) -> HeroMastery.dumpExpMap(player));

        register("giveHideTooltipsItem", (player, args) ->

        {
            player.getInventory().addItem(new ItemBuilder(Material.IRON_PICKAXE).asIcon());
        });

        register("showSeasonalDecoration", (player, args) ->

        {
            final Season season = args.get(0).toEnum(Season.class);

            if (season == null) {
                Notifier.error(player, "Invalid season!");
                return;
            }

            player.sendMessage(season.toString());
        });

        register("nextNotification", (player, args) ->

        {
            player.sendMessage(ChatColor.YELLOW + "Triggered next notification!");
            Main.getPlugin().getNotifier().run();
        });

        register(new SimplePlayerAdminCommand("invokePlayerMethod") {

            static final Set<String> methodNames;

            static {
                methodNames = Sets.newHashSet();

                final Class<Player> playerClass = Player.class;

                appendMethodList(playerClass.getMethods());
                appendMethodList(playerClass.getDeclaredMethods());
            }

            static void appendMethodList(Method[] methods) {
                for (Method method : methods) {
                    if (isValidMethod(method)) {
                        methodNames.add(method.getName());
                    }
                }
            }

            static boolean isValidMethod(Method method) {
                return method.getParameterCount() == 0;
            }

            @Override
            protected void execute(Player player, String[] strings) {
                final String methodName = getArgument(strings, 0).toString();
                final Method method = CFUtils.getMethod(Player.class, methodName);

                if (method == null) {
                    Chat.sendMessage(player, "&cMethod does not exist!");
                    return;
                }

                try {
                    final Object invoked = method.invoke(player);

                    if (invoked != null) {
                        Chat.sendMessage(player, "&aInvoked method %s, return value: &e%s".formatted(methodName, invoked.toString()));
                    }
                    else {
                        Chat.sendMessage(player, "&aInvoked method %s, no return value.".formatted(methodName));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Chat.sendMessage(player, "&4Error! " + e.getMessage());
                }
            }

            @Nullable
            @Override
            protected List<String> tabComplete(CommandSender sender, String[] args) {
                return completerSort(Lists.newArrayList(methodNames), args, false);
            }
        });

        registerDebug("particleFollow", (player, args) ->

        {
            new EntityFollowingParticle(1, player.getLocation().add(0, 5, 0), player) {
                @Override
                public void draw(int tick, @Nonnull Location location) {
                    PlayerLib.spawnParticle(location, Particle.FLAME, 1);
                }
            }.runTaskTimer(0, 2);
        });

        registerDebug("maxChaosStacks", (player, args) ->

        {
            final NyxData data = HeroRegistry.NYX.getPlayerData(player);
            data.incrementChaosStacks(NyxData.MAX_CHAOS_STACKS);

            player.sendMessage("&aDone!");
        });

        register("debugScript", (player, args) ->

        {
            final String id = args.getString(0);
            final Script script = Scripts.byId(id);

            if (script == null) {
                Chat.sendMessage(player, "&cInvalid script!");
                return;
            }

            final LinkedList<ScriptAction> actions = script.copyActions();

            int index = 1;
            ChatColor color = ChatColor.AQUA;

            for (ScriptAction action : actions) {
                Chat.sendMessage(player, "%s [%s] %s: %s".formatted(color, index++, action.getClass().getSimpleName(), action.toString()));

                color = color == ChatColor.AQUA ? ChatColor.DARK_AQUA : ChatColor.AQUA;
            }
        });

        register("clearGarbageEntities", (player, args) ->

        {
            final int cleared = SynchronizedGarbageEntityCollector.clearInAllWorlds();

            Chat.sendMessage(player, "Removed %s entities.".formatted(cleared));
        });

        register("launchIcicles", (player, args) ->

        {
            final GamePlayer gamePlayer = CF.getPlayer(player);

            if (gamePlayer == null) {
                Chat.sendMessage(player, "&cNo handle!");
                return;
            }

            TalentRegistry.ICY_SHARDS.launchIcicles(gamePlayer);
        });

        register("toStringRarity", (player, args) ->

        {
            final Rarity rarity = args.get(0).toEnum(Rarity.class);
            final String string = args.makeStringArray(1);

            if (rarity == null) {
                Chat.sendMessage(player, "&cInvalid rarity!");
                return;
            }

            final String value = rarity.toString(string);

            Chat.sendMessage(player, "&aOutput:");
            Chat.sendMessage(player, value);
        });

        register("adminSkin", (player, args) ->

        {
            // adminSkin equip SKIN
            // adminSkin set HERO SKIN
            // adminSkin remove HERO SKIN
            // adminSkin give HERO SKIN

            if (args.length == 2 && args.getString(0).equalsIgnoreCase("equip")) {
                final Skins skin = args.get(1).toEnum(Skins.class);

                if (skin == null) {
                    Notifier.error(player, "Invalid skin!");
                    return;
                }

                skin.getSkin().equip(player);
                Notifier.success(player, "Equipped {%s} skin!".formatted(skin.getSkin().getName()));
                return;
            }

            final String string = args.get(0).toString().toLowerCase();
            final Hero hero = HeroRegistry.ofString(args.get(1).toString());
            final Skins skin = args.get(2).toEnum(Skins.class);

            final PlayerDatabase database = CF.getDatabase(player);
            final SkinEntry skinEntry = database.skinEntry;

            switch (string) {
                case "set" -> {
                    skinEntry.setSelected(hero, skin);

                    Chat.sendMessage(player, "&aSet selected skin to %s.".formatted(skin));
                }
                case "remove" -> {
                    if (skin == null) {
                        Chat.sendMessage(player, "&cCannot remove null skin!");
                        return;
                    }

                    skinEntry.setOwned(skin, false);
                    Chat.sendMessage(player, "&aRemove %s skin!".formatted(skin));
                }

                case "give" -> {
                    if (skin == null) {
                        Chat.sendMessage(player, "&cCannnot give null skin!");
                        return;
                    }

                    skinEntry.setOwned(skin, true);
                    Chat.sendMessage(player, "&aGave %s skin!".formatted(skin));
                }
            }
        });

        register("awardMe", (player, args) ->

        {
            final Award award = args.get(0).toEnum(Award.class);

            if (award == null) {
                Chat.sendMessage(player, "&cInvalid award!");
                return;
            }

            award.award(CF.getProfile(player));
        });

        register("allowMeToGoldenGg", (player, args) -> {
            Manager.current().allowGoldenGg(player);
            Chat.sendMessage(player, "&6Done!");
        });

        register("resetBonds", (player, args) ->

        {
            CF.getProfile(player).getChallengeList().resetBonds();
        });

        register("progressChallenge", (player, args) ->

        {
            final ChallengeType type = args.get(0).toEnum(ChallengeType.class);

            if (type == null) {
                Chat.sendMessage(player, "&cInvalid type!");
                return;
            }

            final PlayerProfile profile = CF.getProfile(player);
            final PlayerChallengeList challengeList = profile.getChallengeList();

            if (!challengeList.hasOfType(type)) {
                Chat.sendMessage(player, "&cYou don't have %s challenge!".formatted(type));
                return;
            }

            type.progress(profile);
            Chat.sendMessage(player, "&aDone!");
        });

        register("stopGuessWho", (player, args) ->

        {
            Manager.current().stopGuessWhoGame();
        });

        register("spawnBlastPackWallEntity", (player, args) ->

        {
            final float yaw = args.get(0).toFloat();
            final float pitch = args.get(1).toFloat();

            final Location location = player.getLocation();
            location.setYaw(yaw);
            location.setPitch(pitch);

            final DisplayEntity entity = BlastPackEntity.data.spawn(location);

            Chat.sendMessage(player, "&aSpawned with %s yaw and %s pitch!".formatted(yaw, pitch));
        });

        register("debugCosmetic", (player, args) ->

        {
            final Cosmetic cosmetic = Registries.getCosmetics().get(args.getString(0));

            if (cosmetic == null) {
                Notifier.error(player, "Unknown cosmetic: {%s}".formatted(args.getString(0)));
                return;
            }

            Chat.sendMessage(player, "&bKey: " + cosmetic.getKey());
            Chat.sendMessage(player, "&3Class: " + cosmetic.getClass().getSimpleName());
            Chat.sendMessage(player, "&bIs Valid: " + cosmetic.isNotDisabledNorExclusive());
            Chat.sendMessage(player, "&3Name: " + cosmetic.getName());
            Chat.sendMessage(player, "&bRarity: " + cosmetic.getRarity());
            Chat.sendMessage(player, "&3Type: " + cosmetic.getType());
            Chat.sendMessage(player, "&bIs Exclusive: " + cosmetic.isExclusive());
            Chat.sendMessage(player, "&3Is Disabled: " + (cosmetic instanceof Disabled));
        });

        register("later", (player, args) ->

        {
            final int delay = args.get(0).toInt();
            final String command = Chat.arrayToString(args.array, 1);

            if (command.contains("later")) {
                Chat.sendMessage(player, "&cThis command is not allowed to be scheduled.");
                return;
            }

            if (delay <= 0) {
                Chat.sendMessage(player, "&cDelay cannot be negative or 0.");
                return;
            }

            Chat.sendMessage(player, "&aRunning '%s' in %s ticks.".formatted(command, delay));

            GameTask.runLater(() -> {
                player.performCommand(command);

                Chat.sendMessage(player, "&aRan '%s'!".formatted(command));
            }, delay);
        });

        register("stunMe", (player, args) ->

        {
            final GamePlayer gamePlayer = CF.getPlayer(player);
            final int duration = args.get(0).toInt(30);

            if (gamePlayer == null) {
                Chat.sendMessage(player, "&cNo handle.");
                return;
            }

            TalentRegistry.AKCIY.stun(gamePlayer, gamePlayer, duration);
            Chat.sendMessage(player, "&aStunned for %ss!".formatted(duration));
        });

        register("anchorMe", (player, args) ->

        {
            final Location location = BukkitUtils.anchorLocation(player.getLocation());

            player.teleport(location);

            PlayerLib.playSound(Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f);
        });

        register(new SimplePlayerCommand("teammsg") {
            @Override
            protected void execute(Player player, String[] args) {
                Notifier.error(player, "To send a team message, prefix your message with a '" + ChatChannel.TEAM + "'!");
                Notifier.error(player, "Like this: " + ChatChannel.TEAM + " " + Chat.arrayToString(args, 0).trim());
            }
        });

        register("scaleAttribute", (player, args) ->

        {
            final AttributeType attribute = args.get(0).toEnum(AttributeType.class);
            final double value = args.get(1).toDouble();
            final String operation = args.get(2).toString();

            if (attribute == null) {
                Chat.sendMessage(player, "&cInvalid attribute!");
                return;
            }

            switch (operation.toLowerCase()) {
                case "up" -> {
                    final double scaled = attribute.scaleUp(value);

                    Chat.sendMessage(player, "&aScaled value: " + scaled);
                }
                case "down" -> {
                    final double scaled = attribute.scaleDown(value);

                    Chat.sendMessage(player, "&aScaled value: " + scaled);
                }
                default -> {
                    Chat.sendMessage(player, "&cInvalid operation! Must be either 'up' or 'down', not " + operation + "!");
                }
            }
        });

        register("damageMeDaddy", (player, args) ->

        {
            final double damage = args.get(0).toDouble();
            final EnumDamageCause cause = args.get(1).toEnum(EnumDamageCause.class, EnumDamageCause.ENTITY_ATTACK);

            final GamePlayer gamePlayer = CF.getPlayer(player);

            if (gamePlayer == null) {
                Chat.sendMessage(player, "&c⊬↸⚍⚐⚙ \uD801\uDC69⚍ \uD801\uDC69↸⚐⍀!");
                return;
            }

            gamePlayer.damage(damage, cause);
            gamePlayer.sendMessage("&a⚑↸⚑⍀⚐⚐⚍⚑⚙!");
        });

        register("debugDamageCause", (player, args) ->

        {
            final EnumDamageCause cause = args.get(0).toEnum(EnumDamageCause.class);

            if (cause == null) {
                Chat.sendMessage(player, "&cInvalid cause!");
                return;
            }

            Chat.sendMessage(player, "Name: " + cause.name());
            Chat.sendMessage(player, "Flags:");

            cause.getFlags().forEach(flag -> {
                Chat.sendMessage(player, "+ " + flag.name());
            });

        });

        register("calculateGlobalStats", (player, args) ->

        {
            HeroRegistry.calculateGlobalStats();

            Chat.sendMessage(player, "&aDone!");
        });

        register("promptHeroRate", (player, args) ->

        {
            RateHeroCommand.allowRatingHeroIfHasNotRatedAlready(player, Manager.current().getSelectedLobbyHero(player));
        });

        register(new SimplePlayerAdminCommand("adminRateHero") {
            @Override
            protected void execute(Player player, String[] args) {
                final Hero hero = HeroRegistry.ofStringOrNull(getArgument(args, 0).toString());
                final PlayerRating rating = PlayerRating.fromInt(getArgument(args, 1).toInt());

                if (hero == null || rating == null) {
                    Chat.sendMessage(player, "&cOne or many arguments are null, not telling which one because fuck you!");
                    return;
                }

                hero.getStats().setPlayerRating(player.getUniqueId(), rating);
                Chat.sendMessage(player, "Set rating!");
            }
        });

        register(new SimplePlayerAdminCommand("adminCalcAvgHeroRating") {
            @Override
            protected void execute(Player player, String[] args) {
                final Hero hero = HeroRegistry.ofStringOrNull(getArgument(args, 0).toString());

                if (hero == null) {
                    Chat.sendMessage(player, "&cInvalid hero!");
                    return;
                }

                final HeroStatsCollection stats = hero.getStats();
                final PlayerRating averageRating = stats.getAverageRating();

                Chat.sendMessage(player, "%s's average rating is: %s".formatted(hero.getName(), averageRating));
            }
        });

        register("testLosBlocks", (player, args) ->

        {
            final RayTraceResult result = player.rayTraceBlocks(50);
            final org.bukkit.entity.Entity hitEntity = result.getHitEntity();
            final LivingGameEntity entity = CF.getEntity(hitEntity);

            if (hitEntity == null) {
                Chat.sendMessage(player, "&cNo entity hit!");
                return;
            }

            Chat.sendMessage(player, "&aHit: " + entity.toString());
        });

        register("testContributors", (player, args) ->

        {
            final List<Contributor> contributors = Contributors.getContributors();

            if (contributors.isEmpty()) {
                Chat.sendMessage(player, "&cStill loading!");
                return;
            }

            Chat.sendMessage(player, "&aContributors:");

            for (Contributor contributor : contributors) {
                Chat.sendMessage(player, contributor.toString());
            }
        });

        register(new SimplePlayerCommand("delivery") {
            @Override
            protected void execute(Player player, String[] args) {
                new DeliveryGUI(player);
            }
        });

        register("spawnEntityWithGameEffects", (player, args) ->

        {
            LivingGameEntity entity = CF.createEntity(player.getLocation(), Entities.PIG);

            entity.addEffect(Effects.IMMOVABLE, 10000, true);
        });

        register("whenLastMoved", (player, args) ->

        {
            final GamePlayer gamePlayer = CF.getPlayer(player);

            if (gamePlayer == null) {
                Chat.sendMessage(player, "&cNot in a game.");
                return;
            }

            gamePlayer.sendMessage("Mouse=" + MoveType.MOUSE.getLastMoved(gamePlayer));
            gamePlayer.sendMessage("Keyboard=" + MoveType.KEYBOARD.getLastMoved(gamePlayer));
        });

        register(new SimplePlayerAdminCommand("resetMetadata") {
            @Override
            protected void execute(Player player, String[] strings) {
                final PlayerProfile profile = CF.getProfile(player);

                if (profile == null) {
                    Chat.sendMessage(player, "&cNo profile somehow!");
                    return;
                }

                final String stringKey = getArgument(strings, 0).toString();

                if (stringKey.isBlank() || stringKey.isEmpty()) {
                    Chat.sendMessage(player, "&cKey cannot be blank or empty!");
                    return;
                }

                final MetadataEntry entry = profile.getDatabase().metadataEntry;
                final Key key = Key.ofStringOrNull(stringKey);

                if (key == null) {
                    Notifier.error(player, "Invalid key: " + stringKey);
                    return;
                }

                if (!entry.NULL.has(key)) {
                    Chat.sendMessage(player, "&cMetadata value is already null!");
                    return;
                }

                entry.NULL.set(key, null);
                Chat.sendMessage(player, "&aDone!");
            }
        });

        register(new SimplePlayerAdminCommand("testWindLev") {

            private Riptide riptide;

            @Override
            protected void execute(Player player, String[] args) {
                if (riptide != null) {
                    riptide.remove();
                    riptide = null;
                    player.removePotionEffect(PotionEffectType.LEVITATION);

                    Chat.sendMessage(player, "&cRemoved!");
                    return;
                }

                final Location location = player.getLocation();
                riptide = new Riptide(location);

                final int level = getArgument(args, 0).toInt();
                final int tick = getArgument(args, 1).toInt();

                player.addPotionEffect(PotionEffectType.LEVITATION.createEffect(1000, level));

                new GameTask() {
                    @Override
                    public void run() {
                        player.addPotionEffect(PotionEffectType.LEVITATION.createEffect(1000, 255));
                    }
                }.runTaskLater(tick);

                Chat.sendMessage(player, "&aCreated with level %s and %s delay.".formatted(level, tick));
            }
        });

        register(new SimplePlayerAdminCommand("testAbsorption") {
            @Override
            protected void execute(Player player, String[] args) {
                final double amount = getArgument(args, 0).toDouble();

                player.setAbsorptionAmount(amount);
                Chat.sendMessage(player, "&aSet absorption amount to %s!".formatted(amount));
            }
        });

        register(new SimplePlayerAdminCommand("ph") {
            @Override
            protected void execute(Player player, String[] args) {
                final PlayerProfile profile = CF.getProfile(player);
                final Hero hero = profile.getHero();
                final Hero exactHero = HeroRegistry.ofStringOrNull(getArgument(args, 0).toString());

                if (exactHero != null) {
                    new HeroPreviewGUI(player, exactHero, 1);
                    return;
                }

                new HeroPreviewGUI(player, hero, 1);
            }
        });

        register("testAnchor", (player, args) ->

        {
            final Location location = BukkitUtils.findRandomLocationAround(player.getLocation(), 5.0d);

            player.teleport(location);
            Debug.info(BukkitUtils.locationToString(location));
        });

        register("spawnMoonwalkerBlob", (player, args) ->

        {
            HeroRegistry.MOONWALKER.getUltimate().createBlob(player.getLocation(), false);
        });

        register("whoami", (player, args) ->

        {
            final GamePlayer gamePlayer = CF.getPlayer(player);

            if (gamePlayer == null) {
                Chat.sendMessage(player, "&cYou are not in a game!");
                return;
            }

            gamePlayer.sendMessage("Your state: " + gamePlayer.getState());
        });

        register("forceAutoSyncDatabase", (player, args) ->

        {
            Manager.current().getAutoSave().save();
        });

        register(new SimplePlayerAdminCommand("hex") {
            @Override
            protected void execute(Player player, String[] args) {
                final String color = getArgument(args, 0).toString();
                final String string = Chat.arrayToString(args, 1);

                final me.hapyl.fight.game.color.Color theColor = new me.hapyl.fight.game.color.Color(color);

                Chat.sendMessage(player, "&aOutput: ");
                Chat.sendMessage(player, theColor + string);
            }
        });

        register(new SimplePlayerAdminCommand("countTalentTypes") {
            @Override
            protected void execute(Player player, String[] args) {
                final Map<TalentType, Integer> typeCount = Maps.newHashMap();

                for (TalentType type : TalentType.values()) {
                    typeCount.compute(type, Compute.intAdd());
                }

                Chat.sendMessage(player, "&aHere's a total of talent counts:");
                typeCount.forEach((type, count) -> Chat.sendMessage(player, " &2%s: %s".formatted(type.getName(), count)));
            }
        });

        register(new SimplePlayerAdminCommand("testShield") {
            @Override
            protected void execute(Player player, String[] args) {
                final GamePlayer gamePlayer = CF.getPlayer(player);

                if (gamePlayer == null) {
                    Chat.sendMessage(player, "&cMust be in game.");
                    return;
                }

                final double capacity = getArgument(args, 0).toDouble(20);

                gamePlayer.setShield(new Shield(gamePlayer, capacity));
                Chat.sendMessage(player, "&aApplied shield with %s capacity.".formatted(capacity));
            }
        });

        register(new SimpleAdminCommand("loadDatabase") {
            @Override
            protected void execute(CommandSender sender, String[] args) {
                final UUID uuid = UUID.fromString(args[0]);

                CF.getDatabase(uuid);
            }
        });

        register(new SimplePlayerAdminCommand("spawnNpcThatWillThrowError") {
            @Override
            protected void execute(Player player, String[] args) {
                new HumanNPC(player.getLocation(), "name", "hypixel").showAll();
            }
        });

        register(new SimplePlayerCommand("canConvertCrate") {
            @Override
            protected void execute(Player player, String[] args) {
                final CrateConverts enumConvert = getArgument(args, 0).toEnum(CrateConverts.class);

                if (enumConvert == null) {
                    Chat.sendMessage(player, "&cInvalid convert!");
                    return;
                }

                final CrateConvert convert = enumConvert.getWrapped();
                final int canConvertTimes = convert.canConvertTimes(player);

                if (canConvertTimes > 0) {
                    Chat.sendMessage(player, "&aYou can convert %s times!".formatted(canConvertTimes));
                }
                else {
                    Chat.sendMessage(player, "&cYou don't have enough items to convert!");
                }

                if (player.isOp()) {
                    Chat.sendMessage(player, "&7Convert Data:");
                    Chat.sendMessage(player, convert.toString());
                }
            }
        });

        register(new SimpleAdminCommand("lastHapylGUI") {
            @Override
            protected void execute(CommandSender sender, String[] args) {
                final Player hapyl = Bukkit.getPlayer("hapyl");

                if (hapyl == null) {
                    Chat.sendMessage(sender, "&chapyl is not online!");
                    return;
                }

                final GUI lastGUI = GUI.getPlayerLastGUI(hapyl);
                Debug.info("lastGUI=" + (lastGUI == null ? "NONE!" : lastGUI.getName()));
            }
        });

        register(new SimplePlayerAdminCommand("testBlocksCirclingAround") {
            GameTask task;
            List<ArmorStand> blocks = Lists.newArrayList();

            @Override
            protected void execute(Player player, String[] args) {
                if (task != null) {
                    task.cancel();
                    task = null;

                    blocks.forEach(ArmorStand::remove);
                    blocks.clear();

                    Chat.sendMessage(player, "&cCancelled!");
                    return;
                }

                final Location location = player.getLocation();

                final int blockCount = getArgument(args, 0).toInt(10);
                final double distance = getArgument(args, 1).toDouble(5);

                for (int i = 0; i < blockCount; i++) {
                    boolean glow = i % 2 == 0;
                    blocks.add(Entities.ARMOR_STAND_MARKER.spawn(location, self -> {
                        self.setSilent(true);
                        self.setInvisible(true);
                        self.setGravity(false);
                        self.setHelmet(new ItemStack(Material.STONE));
                        if (glow) {
                            self.setGlowing(true);
                        }
                    }));
                }

                final double spread = Math.PI * 2 / Math.max(blockCount, 1);

                task = new TickingGameTask() {
                    private double theta = 0.0d;

                    @Override
                    public void run(int tick) {
                        int index = 1;

                        for (ArmorStand block : blocks) {
                            final double x = Math.sin(theta + spread * index) * distance;
                            final double y = Math.sin(theta + spread * index) * 0.5d;
                            final double z = Math.cos(theta + spread * index) * distance;

                            location.add(x, y, z);
                            block.teleport(location);
                            location.subtract(x, y, z);
                            index++;
                        }

                        if (theta >= Math.PI * 2) {
                            theta = 0.0d;
                        }

                        theta += Math.PI / 16;
                    }
                }.runTaskTimer(0, 2);

                Chat.sendMessage(player, "&aStarted!");
            }
        });

        register("clearCachedTalentItems", (player, args) ->

        {
            TalentRegistry.values().forEach(talent -> {
                talent.nullifyItem();
            });

            Chat.sendMessage(player, "&aDone!");
        });

        register(new SimplePlayerAdminCommand("testTalentLock") {
            @Override
            protected void execute(Player player, String[] args) {
                final int i = getArgument(args, 0).toInt();
                final int lock = getArgument(args, 1).toInt();
                final GamePlayer gamePlayer = CF.getPlayer(player);

                if (gamePlayer == null) {
                    Chat.sendMessage(player, "&cCannot lock talent outside a game!");
                    return;
                }

                final TalentLock talentLock = gamePlayer.getTalentLock();
                final HotBarSlot slot = gamePlayer.getProfile().getHotbarLoadout().bySlot(i);

                if (slot == null) {
                    Chat.sendMessage(player, "&cInvalid talent!");
                    return;
                }

                final boolean isLock = talentLock.setLock(slot, lock);
                if (!isLock) {
                    Chat.sendMessage(player, "&cCannot lock '%s'!".formatted(slot.getName()));
                    return;
                }

                Chat.sendMessage(player, "&aLocked '%s' for %s!".formatted(slot.getName(), CFUtils.formatTick(lock)));
            }
        });

        register(new SimplePlayerAdminCommand("testDisplayEntity") {

            private Display display;

            @Override
            protected void execute(Player player, String[] args) {
                if (display != null) {
                    display.remove();
                    display = null;
                    Chat.sendMessage(player, "&cRemoved!");
                    return;
                }

                display = Entities.BLOCK_DISPLAY.spawn(player.getLocation(), self -> {
                    self.setBlock(Material.STONE.createBlockData());
                    //self.setItemStack(ItemBuilder.playerHeadUrl("f4cabec18394f48191526d9059e458e0116fe84b79c645ca54649377b68f543b")
                    //        .asIcon());
                });

                display.setInterpolationDuration(100);
                display.setInterpolationDelay(0);

                final Transformation transformation = display.getTransformation();
                transformation.getScale().set(2.0d);

                display.setTransformation(transformation);

                Chat.sendMessage(player, "&aSpawned!");
            }
        });

        register(new SimplePlayerAdminCommand("testGiantItem") {

            private GiantItem item;

            @Override
            protected void execute(Player player, String[] args) {
                if (item == null) {
                    item = new GiantItem(
                            player.getLocation(),
                            ItemBuilder.playerHeadUrl("d81fcffb53acbc7c00c53bc7121ca259371b5b76c001dc52139e1804c287e54").asIcon()
                    );
                    Chat.sendMessage(player, "&aSpawned!");
                    return;
                }

                final TypeConverter argument = getArgument(args, 0);
                final float degree = argument.toFloat(-1);

                if (degree != -1) {
                    item.rotate(degree);
                    Chat.sendMessage(player, "&aRotated %s degrees.".formatted(degree));
                    return;
                }

                final String string = argument.toString();

                if (string.equalsIgnoreCase("remove")) {
                    item.remove();
                    item = null;
                    Chat.sendMessage(player, "&cRemoved!");
                    return;
                }

                if (string.equalsIgnoreCase("dance")) {
                    new TickingGameTask() {

                        float d;

                        @Override
                        public void run(int tick) {
                            if (d >= 360) {
                                cancel();
                                Chat.sendMessage(player, "&aDone!");
                                return;
                            }

                            item.rotate(d);

                            d += (float) 360 / GVar.get("deg", 30);
                            Debug.info("degree = " + d);
                        }
                    }.runTaskTimer(2, 1);
                }

            }
        });

        register("viewLegacyAchievementGUI", (player, args) ->

        {
            new LegacyAchievementGUI(player);
        });

        register("getLobbyItems", (player, args) ->

        {
            LobbyItems.giveAll(player);
            Chat.sendMessage(player, "&aThere you go!");
        });

        register("startAndCancelCountdown", (player, args) ->

        {
            final Manager manager = Manager.current();
            manager.createStartCountdown(DebugData.FORCE);
            manager.stopStartCountdown(player);

            Chat.sendMessage(player, "&aDone!");
        });

        register(new SimplePlayerAdminCommand("entity") {
            @Override
            protected void execute(Player player, String[] args) {
                final String stringArg = getArgument(args, 0).toString();
                final NamedEntityType type = Registries.getEntities().get(stringArg);

                if (type == null) {
                    Notifier.error(player, "Invalid entity type: {%s}!".formatted(stringArg));
                    return;
                }

                type.create(player.getLocation());
                Notifier.success(player, "Spawned {%s}!".formatted(type.getName()));
            }

            @Nullable
            @Override
            protected List<String> tabComplete(CommandSender sender, String[] args) {
                return completerSort(Registries.getEntities().keys(), args);
            }
        });

        register("testKnockback360", (player, args) -> {
            final TickingGameTask task = new TickingGameTask() {
                @Override
                public void run(int tick) {
                    if (tick >= 360) {
                        Chat.sendMessage(player, "&aFinished!");
                        cancel();
                        return;
                    }

                    player.sendHurtAnimation(tick);
                }
            };
            task.setIncrement(15);
            task.runTaskTimer(0, 1);
        });

        register(new SimplePlayerAdminCommand("testGuardianBeams") {

            private Quadrant quadrant;

            @Override
            protected void execute(Player player, String[] args) {
                if (quadrant != null) {
                    quadrant.remove();
                    quadrant = null;

                    Chat.sendMessage(player, "&cRemoved!");
                    return;
                }

                final Location location = player.getLocation();
                quadrant = new Quadrant(location) {
                    @Override
                    public void onTouch(@Nonnull LivingGameEntity entity) {
                        entity.damage(10);
                    }
                };

                quadrant.setHeight(3);
                quadrant.runTaskTimer(0, 1);
                Chat.sendMessage(player, "&aStarted!");
            }
        });

        register(new SimplePlayerAdminCommand("ef") {
            @Override
            protected void execute(Player player, String[] args) {
                final EffectType effectType = getArgument(args, 0).toEnum(EffectType.class);
                final int effectDuration = getArgument(args, 1).toInt(20);

                if (effectType == null) {
                    Chat.sendMessage(player, "&cUnknown effect!");
                    return;
                }

                if (effectDuration < 0) {
                    Chat.sendMessage(player, "&cDuration cannot be negative!");
                    return;
                }

                player.addPotionEffect(effectType.getType().createEffect(effectDuration, 1));
                Chat.sendMessage(player, "&aApplied %s for %s!".formatted(effectType, effectDuration));
            }
        });

        register("damageSelf", (player, args) ->

        {
            final GamePlayer gamePlayer = CF.getPlayer(player);

            if (gamePlayer == null) {
                Chat.sendMessage(player, "&cNo handle!");
                return;
            }

            final double damage = args.get(0).toDouble(1.0d);

            gamePlayer.setLastDamager(gamePlayer);
            gamePlayer.damage(damage, EnumDamageCause.ENTITY_ATTACK);
            Chat.sendMessage(player, "&aDamaged!");
        });

        register(new SimplePlayerAdminCommand("testBatCloud") {

            private BatCloud batCloud;

            @Override
            protected void execute(Player player, String[] args) {
                if (batCloud != null) {
                    batCloud.remove();
                    batCloud = null;

                    Chat.sendMessage(player, "&cRemoved!");
                    return;
                }

                batCloud = new BatCloud(player);
                Chat.sendMessage(player, "&aSpawned!");
            }
        });

        register("deleteGamePlayer", (player, args) ->

        {
            final PlayerProfile profile = CF.getProfile(player);
            if (profile == null) {
                return;
            }

            profile.resetGamePlayer();
            Chat.sendMessage(player, "&aDone!");
        });

        register("dumpColor", (player, args) ->

        {
            final ItemStack item = player.getInventory().getItemInMainHand();
            final ItemMeta meta = item.getItemMeta();

            final StringBuilder commandCopy = new StringBuilder();
            final net.kyori.adventure.text.TextComponent.Builder component = Component.text();

            org.bukkit.Color color = null;
            ArmorTrim trim = null;

            if (meta instanceof LeatherArmorMeta colorMeta) {
                color = colorMeta.getColor();
                final int red = color.getRed();
                final int green = color.getGreen();
                final int blue = color.getBlue();

                commandCopy.append(red).append(", ").append(green).append(", ").append(blue);
                component.append(
                        Component.text("Color: ").color(NamedTextColor.GREEN),
                        Component.text("⬛⬛⬛").color(TextColor.color(color.asRGB()))
                );
            }

            if (meta instanceof ArmorMeta armorMeta) {
                trim = armorMeta.getTrim();

                if (trim != null) {
                    final TrimPattern pattern = trim.getPattern();
                    final TrimMaterial material = trim.getMaterial();

                    final String patternKey = BukkitUtils.getKey(pattern).getKey().toUpperCase();
                    final String materialKey = BukkitUtils.getKey(material).getKey().toUpperCase();

                    if (color != null) { // add comma YEP
                        commandCopy.append(", ");
                        component.append(Component.text(", ").color(NamedTextColor.GRAY));
                    }

                    commandCopy.append("TrimPattern.").append(patternKey).append(", TrimMaterial.").append(materialKey);
                    component.append(
                            Component.text("Trim: ").color(NamedTextColor.AQUA),
                            Component.text("%s, %s".formatted(
                                            KeyedToString.of(trim.getPattern())
                                                    .stripMinecraft()
                                                    .capitalize()
                                                    .toString(),
                                            KeyedToString.of(trim.getMaterial())
                                                    .stripMinecraft()
                                                    .capitalize()
                                                    .toString()
                                    ))
                                    .color(NamedTextColor.DARK_AQUA)
                    );
                }
            }

            if (commandCopy.isEmpty()) {
                Chat.sendMessage(player, "&cNo color nor trim applied to this item!");
                return;
            }

            component.append(Component.text(" "))
                    .append(Component.text("COPY")
                            .color(NamedTextColor.GOLD)
                            .decorate(TextDecoration.BOLD, TextDecoration.UNDERLINED)
                            .hoverEvent(Component.text("Click to copy!").color(NamedTextColor.YELLOW))
                            .clickEvent(ClickEvent.suggestCommand(commandCopy.toString()))
                    );

            player.sendMessage(component);
        });

        register(new SwiftTeleportCommand());

        register(new SimplePlayerCommand("cancelCountdown") {
            @Override
            protected void execute(Player player, String[] args) {
                final Manager manager = Manager.current();
                final StartCountdown countdown = manager.getStartCountdown();

                if (countdown == null) {
                    Chat.sendMessage(player, "&cNothing to cancel!");
                    return;
                }

                countdown.cancelByPlayer(player);
            }
        });

        register("debugCollection", (player, args) ->

        {
            final CosmeticCollection collection = args.get(0).toEnum(CosmeticCollection.class);

            if (collection == null) {
                Notifier.error(player, "Invalid collection.");
                return;
            }

            Notifier.info(player, collection.getItems().toString());
        });

        register("debugExpTreeMap", (player, args) ->

        {
            final Experience experience = Main.getPlugin().getExperience();

            experience.getLevelEnoughExp(10000);
        });

        register(new SkinCommand());

        register(new NickCommand());

        register(new SimplePlayerAdminCommand("isProfanity") {
            @Override
            protected void execute(Player player, String[] args) {
                final String string = getArgument(args, 0).toString();

                if (string.isEmpty()) {
                    Chat.sendMessage(player, "&cWell how the hell am I supposed to know?");
                    return;
                }

                final boolean isProfane = ProfanityFilter.isProfane(string);
                Chat.sendMessage(player, "&a'%s' %s".formatted(string, isProfane ? "&cis profane!" : "&ais not profane."));
            }
        });

        register("cstr", (player, args) ->

        {
            final String string = Chat.arrayToString(args.array, 0);

            Chat.sendCenterMessage(player, string);
        });

        register("debugCrate", (player, args) ->

        {
            final Crates crate = args.get(0).toEnum(Crates.class);

            if (crate == null) {
                Notifier.error(player, "Cannot find crate named {%s}.".formatted(args.getString(0)));
                return;
            }

            Notifier.info(player, crate.getCrate().toString());
        });

        register(new SimplePlayerAdminCommand("testOrbiting") {
            private Orbiting orbiting;

            @Override
            protected void execute(Player player, String[] args) {
                if (orbiting == null) {
                    orbiting = new Orbiting(3, Material.ARROW) {
                        @Override
                        public @Nonnull Location getAnchorLocation() {
                            return player.getLocation();
                        }
                    };

                    orbiting.setMatrix(
                            0.5000f,
                            0.5000f,
                            0.7071f,
                            0.0000f,
                            -0.7071f,
                            0.7071f,
                            -0.0000f,
                            0.0000f,
                            -0.5000f,
                            -0.5000f,
                            0.7071f,
                            0.0000f,
                            0.0000f,
                            0.0000f,
                            0.0000f,
                            1.0000f
                    );
                    orbiting.addMissing(player.getLocation());
                    orbiting.runTaskTimer(0, 1);

                    Chat.sendMessage(player, "&aSpawned!");
                    return;
                }

                switch (args[0].toLowerCase()) {
                    case "delete" -> {
                        orbiting.removeAll();
                        orbiting = null;

                        Chat.sendMessage(player, "&cDeleted!");
                    }
                    case "remove" -> {
                        orbiting.remove();

                        Chat.sendMessage(player, "&aRemoved!");
                    }
                    case "add" -> {
                        orbiting.add(player.getLocation());

                        Chat.sendMessage(player, "&aAdded!");
                    }
                    case "tp" -> {
                        orbiting.forEach(stand -> {
                            stand.teleport(stand.getLocation().add(0, 1, 0));
                        });

                        Chat.sendMessage(player, "&aTeleported!");
                    }
                }
            }
        });

        register("dumpEntities", (player, args) ->

        {
            final Set<GameEntity> entities = CF.getEntities();

            Chat.sendMessage(player, "&aEntity Dump (%s)".formatted(entities.size()));

            boolean lighterColor = true;
            for (GameEntity entity : entities) {
                final String className = entity.getClass().getSimpleName();
                final String name = entity.getName();

                Chat.sendMessage(player, (lighterColor ? "&b " : "&3 ") + className + ":" + name);
                lighterColor = !lighterColor;

                // Glow duh
                if (entity instanceof LivingGameEntity livingGameEntity) {
                    livingGameEntity.addEffect(Effects.GLOWING, 60);
                }
            }
        });

        register("rechargeIron", (player, args) ->

        {
            final GamePlayer gamePlayer = CF.getPlayer(player);

            if (gamePlayer == null) {
                Chat.sendMessage(player, "&cNot in a game!");
                return;
            }

            HeroRegistry.ENGINEER.getPlayerData(gamePlayer).setIron(100);
            Chat.sendMessage(player, "&aRecharged!");
        });

        register("setHeroSkin", (player, args) ->

        {
            if (args.length == 0) {
                CF.getProfile(player).resetSkin();
                Chat.sendMessage(player, "&eReset!");
                return;
            }

            final Hero hero = HeroRegistry.ofStringOrNull(args.getString(0));

            if (hero == null) {
                Chat.sendMessage(player, "&cInvalid skin!");
                return;
            }

            final PlayerSkin skin = hero.getSkin();

            if (skin == null) {
                Chat.sendMessage(player, "&cThis hero doesn't have a skin!");
                return;
            }

            skin.apply(player);
            Chat.sendMessage(player, "&aDone!");
        });

        register(new SimplePlayerAdminCommand("testLaunchProjectile") {
            @Override
            protected void execute(Player player, String[] args) {
                final GamePlayer gamePlayer = CF.getPlayer(player);

                if (gamePlayer == null) {
                    return;
                }

                gamePlayer.launchProjectile(Arrow.class);
            }
        });

        register(new SimplePlayerAdminCommand("testLine") {
            @Override
            protected void execute(Player player, String[] strings) {
                final Location start = player.getLocation();
                final Location end = player.getTargetBlockExact(100).getLocation().add(0.5d, 0.5d, 0.5d);

                final double step = 0.25d;
                final double distance = start.distance(end);
                final Vector vector = end.clone().subtract(start).toVector().normalize().multiply(step);

                final Location location = start.clone();

                for (double d = 0.0d; d < distance; d += step) {
                    final double traveled = d / distance;
                    final double y = Math.sin(traveled * Math.PI * 3);

                    location.add(vector);
                    location.add(0, y, 0);

                    PlayerLib.spawnParticle(location, Particle.HAPPY_VILLAGER, 1);
                    location.subtract(0, y, 0);
                }

                Chat.sendMessage(player, "&aDrawn!");
            }
        });

        register("testAddSucculence", (player, args) ->

        {
            final GamePlayer gamePlayer = CF.getPlayer(player);

            if (gamePlayer == null) {
                Chat.sendMessage(player, "&cNo handle.");
                return;
            }

            final BloodfiendData data = HeroRegistry.BLOODFIEND.getData(gamePlayer);

            data.addSucculence(gamePlayer);
            Chat.sendMessage(player, "&aDone!");
        });

        register(new SimplePlayerAdminCommand("debugCooldown") {
            @Override
            protected void execute(Player player, String[] args) {
                final Cooldown cooldown = getArgument(args, 0).toEnum(Cooldown.class);
                final long duration = getArgument(args, 1).toLong();

                if (cooldown == null) {
                    Notifier.Error.INVALID_ENUMERABLE_ARGUMENT.send(player, Arrays.toString(Cooldown.values()));
                    return;
                }

                final GamePlayer gamePlayer = CF.getPlayer(player);

                if (gamePlayer == null) {
                    Notifier.error(player, "Cannot use outside a game.");
                    return;
                }

                if (duration > 0) {
                    gamePlayer.startCooldown(cooldown, duration);
                    Notifier.success(player, "Started cooldown!");
                    return;
                }

                final CooldownData data = gamePlayer.getCooldown().getData(cooldown);

                if (data == null) {
                    Notifier.error(player, "&cYou don't have this cooldown!");
                    return;
                }

                player.sendMessage(data.toString());
            }
        });

        register(new SimplePlayerAdminCommand("velocity") {
            @Override
            protected void execute(Player player, String[] args) {
                final double x = getArgument(args, 0).toDouble();
                final double y = getArgument(args, 1).toDouble();
                final double z = getArgument(args, 2).toDouble();

                if (x == 0 && y == 0 && z == 0) {
                    Notifier.error(player, "At least one vector must be positive!");
                    return;
                }

                player.setVelocity(new Vector(x, y, z));
                player.sendMessage(ChatColor.GREEN + "Whoosh!");
            }
        });

        register("testTransformationRotation", (player, args) ->

        {
            if (args.length != 8) {
                Chat.sendMessage(
                        player,
                        "&cInvalid usage! /testTransformationRotation (double0) (double1) (double2) (double3) (double4) (double5) (double6) (double7)"
                );
                return;
            }

            final double a = Numbers.getDouble(args.array[0]);
            final double b = Numbers.getDouble(args.array[1]);
            final double c = Numbers.getDouble(args.array[2]);
            final double d = Numbers.getDouble(args.array[3]);

            final double e = Numbers.getDouble(args.array[4]);
            final double f = Numbers.getDouble(args.array[5]);
            final double g = Numbers.getDouble(args.array[6]);
            final double h = Numbers.getDouble(args.array[7]);

            Entities.ITEM_DISPLAY.spawn(player.getLocation(), self -> {
                self.setItemStack(ItemBuilder.playerHeadUrl("2c8c8f382667bf59f164106849c00e6dfd9ad00a72670b9de99589e4dcd00900").asIcon());
                self.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.HEAD);
                self.setTransformation(new Transformation
                        (
                                new Vector3f(0, 0, 0),
                                new Quaternionf(a, b, c, d),
                                new Vector3f(1, 1, 1),
                                new Quaternionf(e, f, g, h)
                        ));

                self.setCustomName("(%s, %s, %s, %s) (%s, %s, %s, %s)".formatted(a, b, c, d, e, f, g, h));
                self.setCustomNameVisible(true);
            });

            Chat.sendMessage(player, "&aSpawned!");
        });

        register("testHoverText", (player, args) ->

        {
            final TextComponent text = new TextComponent("Hover me");

            text.setHoverEvent(ChatUtils.showText(args.array));
            player.spigot().sendMessage(text);
        });

        register("spawnHuskWithCCResist", (player, args) ->

        {
            final LivingGameEntity entity = CF.createEntity(player.getLocation(), Entities.HUSK);

            entity.getAttributes().set(AttributeType.EFFECT_RESISTANCE, 1);

            Chat.sendMessage(player, "&dDone!");
        });

        register("spawnEntityWithMaxDodgeToTestTheDodgeAttributeBecauseIHaveNoFriendsToTestItWith", (player, args) ->

        {
            final Pig pig = Entities.PIG.spawn(player.getLocation());
            final LivingGameEntity entity = CF.getEntity(pig);

            if (entity == null) {
                Chat.sendMessage(player, "&1Entity handle is null!");
                return;
            }

            entity.getAttributes().set(AttributeType.DODGE, AttributeType.DODGE.maxValue());

            Chat.sendMessage(player, "&1There you go!");
        });

        register(new SimplePlayerCommand("viewAchievementGUI") {
            @Override
            protected void execute(Player player, String[] args) {
                new AchievementGUI(player);
            }
        });

        register(new SimplePlayerAdminCommand("simulateDeathMessage") {
            @Override
            protected void execute(Player player, String[] strings) {
                final EnumDamageCause cause = getArgument(strings, 0).toEnum(EnumDamageCause.class);
                final double distance = getArgument(strings, 1).toDouble();
                final GamePlayer gamePlayer = CF.getPlayer(player);

                final String format = cause.getDeathMessage().format(gamePlayer, gamePlayer, distance);
                Chat.sendMessage(player, ChatColor.RED + format);
            }

            @Nullable
            @Override
            protected List<String> tabComplete(CommandSender sender, String[] args) {
                return completerSort(EnumDamageCause.values(), args);
            }
        });

        register("recipes", (player, args) ->

        {
            if (args.length == 0) {
                Chat.sendMessage(player, "&cMissing argument, either 'reset' or 'clear'.");
                return;
            }

            final String arg = args.getString(0);

            if (arg.equalsIgnoreCase("reset")) {
                Bukkit.resetRecipes();
                Chat.broadcast("&aReset recipes!");
            }
            else if (arg.equalsIgnoreCase("clear")) {
                Bukkit.clearRecipes();
                Chat.broadcast("&aCleared recipes!");
            }
            else {
                Chat.sendMessage(player, "&cInvalid usage!");
            }
        });

        register("dumpBlockNamesCSV", (player, args) ->

        {
            Runnables.runAsync(() -> {
                final File path = new File(Main.getPlugin().getDataFolder(), "element_types.csv");

                try (FileWriter writer = new FileWriter(path)) {
                    for (Material material : Material.values()) {
                        if (material.isBlock()) {
                            writer.append(material.name().toUpperCase()).append(",").append("NULL");
                            writer.append("\n");
                        }
                    }

                    Runnables.runSync(() -> {
                        Chat.sendMessage(player, "&aDumped into &e%s&a!".formatted(path.getAbsolutePath()));
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });

        register("testEternaItemBuilderAddTrim", (player, args) ->

        {
            player.getInventory()
                    .addItem(ItemBuilder.of(Material.DIAMOND_CHESTPLATE).setArmorTrim(TrimPattern.EYE, TrimMaterial.DIAMOND).asIcon());
        });

        register("getUuidName", (player, args) ->

        {
            try {
                final UUID uuid = UUID.fromString(args.getString(0));

                Chat.sendMessage(player, "&a%s belongs to %s".formatted(uuid.toString(), Bukkit.getOfflinePlayer(uuid).getName()));
            } catch (Exception e) {
                Chat.sendMessage(player, "&cProvide a valid uuid!");
            }
        });

        register(new SimplePlayerAdminCommand("nextTrim") {

            // bro just make this a fucking enum
            private final TrimPattern[] PATTERNS =
                    {
                            TrimPattern.SENTRY,
                            TrimPattern.DUNE,
                            TrimPattern.COAST,
                            TrimPattern.WILD,
                            TrimPattern.WARD,
                            TrimPattern.EYE,
                            TrimPattern.VEX,
                            TrimPattern.TIDE,
                            TrimPattern.SNOUT,
                            TrimPattern.RIB,
                            TrimPattern.SPIRE,
                            TrimPattern.WAYFINDER,
                            TrimPattern.SHAPER,
                            TrimPattern.SILENCE,
                            TrimPattern.RAISER,
                            TrimPattern.HOST
                    };

            @Override
            protected void execute(Player player, String[] strings) {
                final String string = getArgument(strings, 0).toString().toLowerCase();

                switch (string) {
                    case "helmet" -> nextTrim(player, EquipmentSlot.HEAD);
                    case "chestplate", "chest" -> nextTrim(player, EquipmentSlot.CHEST);
                    case "leggings", "legs" -> nextTrim(player, EquipmentSlot.LEGS);
                    case "boots" -> nextTrim(player, EquipmentSlot.FEET);
                    default -> {
                        Chat.sendMessage(player, "&cInvalid argument, accepting: [helmet, chestplate, chest, leggings, legs, boots]");
                    }
                }
            }

            private void nextTrim(Player player, EquipmentSlot slot) {
                final PlayerInventory inventory = player.getInventory();
                final ItemStack item = inventory.getItem(slot);

                if (item == null) {
                    Chat.sendMessage(player, "&cNot a valid item.");
                    return;
                }

                if (!(item.getItemMeta() instanceof ArmorMeta meta)) {
                    Chat.sendMessage(player, "&cCannot apply trim to this piece!");
                    return;
                }

                final ArmorTrim trim = meta.getTrim();
                TrimPattern pattern = trim == null ? PATTERNS[0] : trim.getPattern();

                for (int i = 0; i < PATTERNS.length; i++) {
                    if (PATTERNS[i] == pattern) {
                        pattern = i + 1 >= PATTERNS.length ? PATTERNS[0] : PATTERNS[i + 1];
                        break;
                    }
                }

                meta.setTrim(new ArmorTrim(trim == null ? TrimMaterial.QUARTZ : trim.getMaterial(), pattern));
                item.setItemMeta(meta);

                Chat.sendMessage(player, "&aSet %s pattern.".formatted(BukkitUtils.getKey(pattern).getKey()));
            }
        });

        register("readElementTypesCSV", (player, args) ->

        {
            Runnables.runAsync(() -> {
                final File file = new File(Main.getPlugin().getDataFolder(), "element_types.csv");

                if (!file.exists()) {
                    Chat.sendMessage(player, "&cFile 'element_types.csv' doesn't exists!");
                    return;
                }

                final Map<ElementType, Set<Material>> mapped = Maps.newHashMap();

                try (var reader = new Scanner(file)) {
                    int index = 1;

                    while (reader.hasNextLine()) {
                        final String line = reader.nextLine();
                        final String[] split = line.split(",");

                        final Material material = Enums.byName(Material.class, split[0]);
                        final ElementType elementType = Enums.byName(ElementType.class, split[1]);

                        if (material == null) {
                            Chat.sendMessage(player, "&4ERROR @ %s! &cMaterial %s is invalid!".formatted(index, split[0]));
                            reader.close();
                            return;
                        }

                        if (elementType == null) {
                            Chat.sendMessage(player, "&4ERROR @ %s! &cType %s is invalid!".formatted(index, split[1]));
                            reader.close();
                            return;
                        }

                        // Don't care about null
                        if (elementType == ElementType.NULL) {
                            continue;
                        }

                        mapped.compute(elementType, (t, set) -> {
                            if (set == null) {
                                set = Sets.newHashSet();
                            }

                            set.add(material);

                            return set;
                        });

                        index++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Write to file
                final File fileOut = new File(Main.getPlugin().getDataFolder(), "element_types.mapped");

                try (var writer = new FileWriter(fileOut)) {
                    for (ElementType type : ElementType.values()) {
                        final Set<Material> materials = mapped.getOrDefault(type, Sets.newHashSet());

                        writer.append("//").append(String.valueOf(type)).append("\n");

                        int index = 0;
                        for (Material material : materials) {
                            if (index++ != 0) {
                                writer.append(",\n");
                            }
                            writer.append("     Material.").append(material.name());
                        }

                        writer.append("\n");
                    }

                    Runnables.runSync(() -> Chat.sendMessage(player, "&aDumped into &e%s&a!".formatted(fileOut.getAbsolutePath())));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
        });

        register("debugLevelUpConstruct", (player, args) ->

        {
            final Construct construct = HeroRegistry.ENGINEER.getConstruct(CF.getPlayer(player));

            if (construct == null) {
                Chat.sendMessage(player, "&cNo construct!");
                return;
            }

            construct.levelUp();
            Chat.sendMessage(player, "&aLevelled up!");
        });

        register(new SimplePlayerCommand("resourcePack") {
            @Override
            protected void execute(Player player, String[] args) {
                final PlayerProfile profile = CF.getProfile(player);

                if (profile == null) {
                    Chat.sendMessage(player, "&cCouldn't find your profile! That's a bug you should report.");
                    return;
                }

                profile.promptResourcePack();
            }
        });

        register("testAchievementToast", (player, args) ->

        {
        });

        register(new SimplePlayerAdminCommand("testSplashText") {

            private SplashText splash;

            @Override
            protected void execute(Player player, String[] args) {
                if (splash == null) {
                    splash = new SplashText(player);
                }
                else if (args.length == 0) {
                    splash.remove();
                    Chat.sendMessage(player, "&aRemoved!");
                    return;
                }

                final int startLine = getArgument(args, 0).toInt();

                splash.create(startLine, Arrays.copyOfRange(args, 1, args.length));

                Chat.sendMessage(player, "&aDone!");
            }
        });

        register(new SimpleAdminCommand("colorMeThis") {
            @Override
            protected void execute(CommandSender sender, String[] args) {
                if (args.length == 0) {
                    Chat.sendMessage(sender, "&cColor you what?");
                    return;
                }

                Chat.sendMessage(sender, "&aThere you go!");
                Chat.sendMessage(sender, Chat.arrayToString(args, 0));
            }
        });

        register(new SimplePlayerAdminCommand("debugSnakeBlock") {

            private BlockDisplay display;

            @Override
            protected void execute(Player player, String[] args) {
                if (display != null) {
                    display.remove();
                }

                final float transformation = getArgument(args, 0).toFloat();

                display = Entities.BLOCK_DISPLAY.spawn(player.getLocation(), self -> {
                    self.setBlock(Material.STONE.createBlockData());
                    self.setTransformation(new Transformation(
                            new Vector3f(transformation, transformation, transformation),
                            new AxisAngle4f(0.0f, 0.0f, 0.0f, 0.0f),
                            new Vector3f(0.25f, 0.25f, 0.25f),
                            new AxisAngle4f(0.0f, 0.0f, 0.0f, 0.0f)
                    ));
                });

                Chat.sendMessage(player, "&aSpawned with %s.".formatted(transformation));
            }
        });

        register(new SimplePlayerAdminCommand("debugTextDisplayOpaque") {

            private final Set<TextDisplay> set = Sets.newHashSet();

            @Override
            protected void execute(Player player, String[] args) {
                set.forEach(TextDisplay::remove);
                set.clear();

                final Location location = player.getLocation();

                for (var ref = new Object() {
                    byte i = Byte.MIN_VALUE;
                }; ref.i < Byte.MAX_VALUE; ref.i++) {
                    Entities.TEXT_DISPLAY.spawn(location, self -> {
                        self.setBillboard(Display.Billboard.CENTER);
                        self.setSeeThrough(true);
                        self.setTextOpacity(ref.i);
                        self.setText("&a" + ref.i);
                    });

                    location.add(0.0d, 0.25d, 0.0d);
                }

                Chat.sendMessage(player, "&aDone!");
            }
        });

        register("testRotatingCircle", (player, args) ->

        {
            final Location location = player.getLocation();

            new TickingGameTask() {
                @Override
                public void run(int tick) {
                    if (tick > 360) {
                        cancel();
                        Chat.sendMessage(player, "&aDone!");
                        return;
                    }

                    final double rad = Math.toRadians(tick);
                    final Vector vector = new Vector(Math.sin(rad) * 3, 0, Math.cos(rad) * 3);

                    vector.rotateAroundX(tick);
                    vector.rotateAroundY(tick);
                    vector.rotateAroundZ(tick);

                    location.add(vector);
                    PlayerLib.spawnParticle(location, Particle.ENCHANTED_HIT, 1);
                    location.subtract(vector);
                }
            }.runTaskTimer(0, 1);
        });

        register(new SimplePlayerAdminCommand("testParticleRot") {
            @Override
            protected void execute(Player player, String[] args) {
                final Location location = player.getLocation();

                final double x = Math.toRadians(getArgument(args, 0).toDouble());
                final double y = Math.toRadians(getArgument(args, 1).toDouble());
                final double z = Math.toRadians(getArgument(args, 2).toDouble());

                final double radius = getArgument(args, 3).toDouble(3);

                for (double d = 0; d < Math.PI * 2; d += Math.PI / 16) {
                    final Vector vector = new Vector(Math.sin(d) * radius, 0, Math.cos(d) * radius);

                    if (x != 0) {
                        vector.rotateAroundX(x);
                    }
                    if (y != 0) {
                        vector.rotateAroundY(y);
                    }
                    if (z != 0) {
                        vector.rotateAroundZ(z);
                    }

                    location.add(vector);
                    PlayerLib.spawnParticle(location, Particle.HAPPY_VILLAGER, 1);
                    location.subtract(vector);
                }

                Chat.sendMessage(player, "&aRotated with %s, %s, %s!".formatted(x, y, z));
            }
        });

        register(new SimplePlayerAdminCommand("temperStat") {
            @Override
            protected void execute(Player player, String[] args) {
                // $ <stat> <amount> <duration>
                // $ temper [value] [duration]
                final GamePlayer gamePlayer = GamePlayer.getExistingPlayer(player);

                if (gamePlayer == null) {
                    Chat.sendMessage(player, "&cCannot use outside a game.");
                    return;
                }

                final Temper temper = getArgument(args, 0).toEnum(Temper.class);
                final AttributeType attributeType = getArgument(args, 1).toEnum(AttributeType.class);
                final double value = getArgument(args, 2).toDouble(0.2d);
                final int duration = getArgument(args, 3).toInt(100);

                if (attributeType == null) {
                    Notifier.error(player, "Invalid type!");
                    return;
                }

                if (temper == null) {
                    Notifier.error(player, "Invalid temper!");
                    return;
                }

                temper.temper(gamePlayer, attributeType, value, duration);
                Chat.sendMessage(player, "&aDone!");
            }

            @Nullable
            @Override
            protected List<String> tabComplete(CommandSender sender, String[] args) {
                if (args.length == 1) {
                    return completerSort(Temper.values(), args);
                }

                return null;
            }
        });

        register(new SimplePlayerAdminCommand("simulateSelfDeath") {
            @Override
            protected void execute(Player player, String[] args) {
                final GamePlayer gamePlayer = GamePlayer.getExistingPlayer(player);

                if (gamePlayer == null) {
                    Chat.sendMessage(player, "&cMust be in game to use this.");
                    return;
                }

                gamePlayer.setLastDamager(CF.getPlayer(player));
                gamePlayer.die(true);
            }
        });

        register(new SimplePlayerAdminCommand("testEternaClone") {
            @Override
            protected void execute(Player player, String[] strings) {
                new Cuboid(
                        BukkitUtils.defLocation(-9, 64, -271),
                        BukkitUtils.defLocation(-5, 66, -269)
                ).cloneBlocksTo(BukkitUtils.defLocation(
                        -2,
                        64,
                        -270
                ), true);

                Chat.sendMessage(player, "<color=#359751>Done!</>");
            }
        });

        register(new SimplePlayerAdminCommand("drawCircleAlong") {
            @Override
            protected void execute(Player player, String[] args) {
                final String string = getArgument(args, 0).toString();

                if (!string.equalsIgnoreCase("x") && !string.equalsIgnoreCase("z")) {
                    Chat.sendMessage(player, "&cShould be either along X or Z, not " + string);
                    return;
                }

                final double radius = 3.0d;
                final boolean isX = string.equalsIgnoreCase("x");
                final Location location = player.getLocation();

                for (double d = 0.0d; d < Math.PI * 2; d += Math.PI / 16) {
                    final double x = isX ? Math.sin(d) * radius : 0.0d;
                    final double y = Math.cos(d) * radius;
                    final double z = !isX ? Math.sin(d) * radius : 0.0d;

                    location.add(x, y, z);

                    // Do something
                    player.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, location, 1);

                    location.subtract(x, y, z);
                }
            }
        });

        register(new SimpleAdminCommand("ram") {

            private final long GIGABYTE = 1_048_576L;

            @Override
            protected void execute(CommandSender sender, String[] args) {
                Chat.sendMessage(
                        sender,
                        "&6Current Memory Usage: &a%s/%s mb (Max: %s mb)".formatted(
                                (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / GIGABYTE,
                                Runtime.getRuntime().totalMemory() / GIGABYTE,
                                Runtime.getRuntime().maxMemory() / GIGABYTE
                        )
                );

                final OperatingSystemMXBean mx = ManagementFactory.getOperatingSystemMXBean();

                if (mx instanceof com.sun.management.OperatingSystemMXBean sunOsBean) {
                    double cpuLoad = sunOsBean.getCpuLoad();
                    Chat.sendMessage(
                            sender,
                            ("&6CPU Load: &a%.1f%%" + (cpuLoad <= 0.0d ? " &cNot supported?" : "")).formatted(cpuLoad * 100)
                    );
                }
            }
        });

        register(new SimplePlayerAdminCommand("gradient") {
            @Override
            protected void execute(Player player, String[] args) {
                // gradient #FROM #TO BOLD<bool> ...Text

                final Color from = Color.decode(getArgument(args, 0).toString());
                final Color to = Color.decode(getArgument(args, 1).toString());
                final boolean isBold = getArgument(args, 2).toString().equalsIgnoreCase("true");

                final String string = Chat.arrayToString(args, 3);
                final Gradient gradient = new Gradient(string);

                if (isBold) {
                    gradient.makeBold();
                }

                Chat.sendMessage(player, "&a&lOutput:");

                Chat.sendMessage(player, "&aLinear:");
                Chat.sendMessage(player, "");
                Chat.sendMessage(player, gradient.rgb(from, to, Interpolators.LINEAR));
                Chat.sendMessage(player, "");
                copyCommand(
                        player,
                        "new Gradient(%s).rgb(%s, %s, Interpolators.LINEAR)",
                        isBold,
                        string,
                        colorToString(from),
                        colorToString(to)
                );

                Chat.sendMessage(player, "&aQuadratic Fast -> Slow:");
                Chat.sendMessage(player, "");
                Chat.sendMessage(player, gradient.rgb(from, to, Interpolators.QUADRATIC_FAST_TO_SLOW));
                Chat.sendMessage(player, "");
                copyCommand(
                        player,
                        "new Gradient(%s).rgb(%s, %s, Interpolators.QUADRATIC_FAST_TO_SLOW)",
                        isBold,
                        string,
                        colorToString(from),
                        colorToString(to)
                );

                Chat.sendMessage(player, "&aQuadratic Slow -> Fast:");
                Chat.sendMessage(player, "");
                Chat.sendMessage(player, gradient.rgb(from, to, Interpolators.QUADRATIC_SLOW_TO_FAST));
                Chat.sendMessage(player, "");
                copyCommand(
                        player,
                        "new Gradient(%s).rgb(%s, %s, Interpolators.QUADRATIC_SLOW_TO_FAST)",
                        isBold,
                        string,
                        colorToString(from),
                        colorToString(to)
                );

            }

            private String colorToString(Color color) {
                return "%s, %s, %s".formatted(color.getRed(), color.getGreen(), color.getBlue());
            }

            private void copyCommand(Player player, String command, boolean bold, Object... format) {
                if (bold) {
                    command = command.replaceFirst("\\.", ".makeBold().");
                }

                command = command.formatted(format);

                Chat.sendClickableHoverableMessage(
                        player,
                        LazyEvent.copyToClipboard(command),
                        LazyEvent.showText("&eClick to copy code"),
                        "&e&lCLICK TO COPY"
                );
            }
        });


        register(new SimplePlayerAdminCommand("debugDamageData") {
            @Override
            protected void execute(Player player, String[] strings) {
                final GamePlayer gamePlayer = CF.getPlayer(player);

                if (gamePlayer == null) {
                    Chat.sendMessage(player, "&cNo handle.");
                    return;
                }

                final LivingGameEntity targetEntity = Collect.targetEntityDot(gamePlayer, 20.0d, 0.9d, e -> e.isNot(player));

                if (targetEntity == null) {
                    Chat.sendMessage(player, gamePlayer.getEntityData());
                    return;
                }

                Chat.sendMessage(player, targetEntity.getEntityData());
            }
        });

        register(new SimplePlayerAdminCommand("readSigns") {

            private Queue<Sign> queue;

            @Override
            protected void execute(Player player, String[] strings) {
                final NamedSignReader reader = new NamedSignReader(player.getWorld());

                if (queue == null) {
                    queue = reader.readAsQueue();
                    Chat.sendMessage(player, "&aFound %s signs. Use the command again to teleport to the next one.".formatted(queue.size()));
                    return;
                }

                if (queue.peek() == null) {
                    Chat.sendMessage(player, "&cNo more signs!");
                    queue = null;
                    return;
                }

                final Sign sign = queue.poll();
                final String line = sign.getLine(0).replace("[", "").replace("]", "").toUpperCase();
                final Location location = sign.getLocation().add(0.5d, 0.0d, 0.5d); // center it
                final String locationString = BukkitUtils.locationToString(location);

                Chat.sendMessage(player, "");
                Chat.sendMessage(player, "&aNext: &l" + line.toUpperCase());

                Chat.sendClickableHoverableMessage(
                        player,
                        LazyEvent.runCommand("/tp %s %s %s".formatted(location.getX(), location.getY(), location.getZ())),
                        LazyEvent.showText("&eClick to teleport!"),
                        "&6&lCLICK TO TELEPORT"
                );

                Chat.sendClickableHoverableMessage(
                        player,
                        LazyEvent.copyToClipboard("%s %s %s".formatted(location.getX(), location.getY(), location.getZ())),
                        LazyEvent.showText("&eClick to copy coordinates!"),
                        "&6&lCLICK TO COPY COORDINATES"
                );

                Chat.sendClickableHoverableMessage(
                        player,
                        LazyEvent.copyToClipboard(line.equalsIgnoreCase("spawn")
                                ? "addLocation(%s, 0, 0)".formatted(locationString)
                                : "addPackLocation(PackType.%s, %s)".formatted(line, locationString)),
                        LazyEvent.showText("&eClick to copy code!"),
                        "&6&lCLICK TO COPY CODE"
                );

                Chat.sendClickableHoverableMessage(
                        player,
                        LazyEvent.runCommand(getUsage().toLowerCase()),
                        LazyEvent.showText("&aClick to show the next sign!"),
                        "&a&lNEXT"
                );
            }

        });

        register(new SimpleAdminCommand("listProfiles") {
            @Override
            protected void execute(CommandSender commandSender, String[] strings) {
                Manager.current().listProfiles();
            }
        });

        register(new CFCommand("testHologramHeights", PlayerRank.ADMIN) {

            private List<Hologram> holograms = Lists.newArrayList();
            private GameTask task;

            @Override
            protected void execute(@Nonnull Player player, @Nonnull ArgumentList args, @Nonnull PlayerRank rank) {
                final double offsetY = args.get(0).toDouble(0.0d);
                final Location absoluteLocation = player.getLocation();
                final Location location = player.getLocation().subtract(0, offsetY, 0);

                if (task != null) {
                    task.cancel();
                    task = null;
                }

                holograms.forEach(Hologram::destroy);
                holograms.clear();

                task = new GameTask() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 10; i++) {
                            absoluteLocation.add(i, 0, 0);
                            PlayerLib.spawnParticle(absoluteLocation, Particle.HAPPY_VILLAGER, 1);
                            absoluteLocation.subtract(i, 0, 0);
                        }
                    }
                }.runTaskTimer(0, 1);

                for (int i = 0; i < 10; i++) {
                    location.add(1, 0, 0);

                    holograms.add(new Hologram()
                            .setLines(createArray(i + 1))
                            .create(location)
                            .showAll());
                }

                Chat.sendMessage(player, "&aDone!");
            }

            private String[] createArray(int size) {
                final String[] array = new String[size];

                for (int i = 0; i < size; i++) {
                    array[i] = "test" + i;
                }

                return array;
            }

        });

        register(new CFCommand("testChestAnimation", PlayerRank.ADMIN) {
            @Override
            protected void execute(@Nonnull Player player, @Nonnull ArgumentList args, @Nonnull PlayerRank rank) {
                // <command> (open, close)
                final String argument = args.get(0).toString().toLowerCase();

                final Block targetBlock = player.getTargetBlockExact(10);

                if (targetBlock == null) {
                    Notifier.error(player, "Not looking at a block.");
                    return;
                }

                final Material type = targetBlock.getType();

                if (type != Material.CHEST && type != Material.TRAPPED_CHEST && type != Material.ENDER_CHEST) {
                    Notifier.error(player, "Not looking at a chest!");
                    return;
                }

                switch (argument) {
                    case "open" -> {
                        CFUtils.playChestAnimation(targetBlock, true);
                        Notifier.success(player, "Playing open animation.");
                    }

                    case "close" -> {
                        CFUtils.playChestAnimation(targetBlock, false);
                        Notifier.success(player, "Playing close animation.");
                    }

                    default -> Notifier.error(player, "Invalid argument! Must be either 'open' or 'close'!");
                }
            }
        });

        register(new SimplePlayerAdminCommand("respawnGamePacks") {
            @Override
            protected void execute(Player player, String[] args) {
                final IGameInstance instance = Manager.current().getGameInstance();

                if (instance == null) {
                    Chat.sendMessage(player, "&cNo game instance.");
                    return;
                }

                final IntInt i = new IntInt();

                instance.getEnumMap().getLevel().getGamePacks().forEach(pack -> {
                    pack.getActivePacks().forEach(activePack -> {
                        i.increment();
                        activePack.createEntity();
                    });
                });

                Chat.sendMessage(player, "&aRespawned %s packs.".formatted(i));
            }
        });

        register("accelerateGamePackSpawn", (player, args) ->

        {
            final GameInstance gameInstance = Manager.current().getGameInstance();

            if (gameInstance == null) {
                Chat.sendMessage(player, "&cCannot accelerate outside a game!");
                return;
            }

            gameInstance.getEnumMap().getLevel().getGamePacks().forEach(GamePack::accelerate);

            Chat.sendMessage(player, "&aDone!");
        });

        register(new SimplePlayerAdminCommand("testAttackSpeed") {
            @Override
            protected void execute(Player player, String[] args) {
                final double newValue = getArgument(args, 0).toDouble(-999);
                final AttributeInstance attribute = player.getAttribute(Attribute.ATTACK_SPEED);

                if (newValue == -999) {
                    Chat.sendMessage(
                            player,
                            "&aCurrent attack speed: %s".formatted(attribute.getBaseValue())
                    );
                    return;
                }

                attribute.setBaseValue(newValue);
                Chat.sendMessage(player, "&aSet value to %s!".formatted(newValue));
            }
        });

        register("testTradeGUi", ((player, strings) ->

        {
            final PlayerGUI gui = new PlayerGUI(player, 6);

            gui.fillColumn(4, ItemBuilder.of(Material.BLACK_WOOL).asIcon());
            gui.setEventListener(new EventListener() {
                @Override
                public void listen(Player player, GUI gui, InventoryClickEvent event) {
                    final int clickedSlot = event.getRawSlot();
                    final int module = clickedSlot % 9;

                    boolean leftSide = module < 4;
                    boolean rightSide = module > 4;

                    Chat.sendMessage(player, "&aSide: " + (leftSide ? "LEFT" : rightSide ? "RIGHT" : "MIDDLE"));
                }
            });

            gui.openInventory();
        }));

        register(new PluginsCommandOverride());

        register(new SimplePlayerAdminCommand("riptide") {

            private HumanNPC npc;
            private org.bukkit.entity.Entity entity;

            @Override
            protected void execute(Player player, String[] args) {
                if (npc != null) {
                    entity.remove();
                    entity = null;
                    npc.remove();
                    npc = null;
                    Chat.sendMessage(player, "&aRemoved!");
                    return;
                }

                final Location location = player.getLocation();
                location.setYaw(90f);
                location.setPitch(90f);

                final Entities<? extends org.bukkit.entity.Entity> toSpawn = Entities.byName(args[0]);

                if (toSpawn == null) {
                    Chat.sendMessage(player, "&cInvalid entity = " + args[0]);
                    return;
                }

                final HumanNPC npc = new HumanNPC(location.clone().subtract(0.0, GVar.get("riptide", 1d), 0.0), "", player.getName());
                entity = toSpawn.spawn(location.clone().subtract(0.0, GVar.get("riptide2", 0.0d), 0.0d), self -> {
                    self.setGravity(false);
                });

                npc.bukkitEntity().setInvisible(true);
                npc.setCollision(false);
                npc.showAll();

                npc.setDataWatcherByteValue(8, (byte) 0x04);
                npc.updateDataWatcher();

                this.npc = npc;
                Chat.sendMessage(player, "&aSpawned!");

                new GameTask() {
                    @Override
                    public void run() {
                        if (entity == null) {
                            this.cancel();
                            return;
                        }

                        final Location entityLocation = entity.getLocation();
                        entityLocation.add(0.0, GVar.get("riptide3", 0.0d), 0.0);
                        entityLocation.setYaw(90f);
                        entityLocation.setPitch(90f);

                        npc.teleport(entityLocation);
                    }
                }.runTaskTimer(0, 1);
            }
        });

        register(new SimplePlayerAdminCommand("stopglow") {
            @Override
            protected void execute(Player player, String[] strings) {
                final Pig spawn = Entities.PIG.spawn(player.getLocation());

                Glowing.glowInfinitely(spawn, ChatColor.GOLD, player);

                Runnables.runLater(() -> {
                    Glowing.stopGlowing(player, spawn);
                    Chat.sendMessage(player, "Stop glowing");
                }, 60);
            }
        });

        register(new SimplePlayerAdminCommand("testTitleAnimation") {
            @Override
            protected void execute(Player player, String[] args) {
                new TitleAnimation();
            }
        });

        register("commitSuicideButDontTriggerModeWinCondition", (player, args) ->

        {
            final GamePlayer gamePlayer = CF.getPlayer(player);

            if (gamePlayer == null) {
                Chat.sendMessage(player, "&cI'm actually tired of typing this, but you must be in THE FUCKING GAME TO USE THIS!");
                return;
            }

            gamePlayer.setState(EntityState.DEAD);
        });

        register(new SimpleAdminCommand("debugTeams") {
            @Override
            protected void execute(CommandSender sender, String[] args) {
                for (GameTeam team : GameTeam.values()) {
                    Debug.info(team.toString());
                }
            }
        });

        register("leaveTeam", (player, args) ->

        {
            final Entry entry = Entry.of(player);
            final GameTeam team = GameTeam.getEntryTeam(entry);

            if (team == null) {
                Chat.sendMessage(player, "&cNot in a team!!!");
                GameTeam.getSmallestTeam();
                return;
            }

            team.removeEntry(entry);
            Chat.sendMessage(player, "&aRemoved you from the team!");
            Chat.sendMessage(
                    player,
                    "&e&lKeep in mind that having no team is not intentional and errors might appear! Use this for testing only!!!"
            );
        });

        register("spawnAlliedEntity", (player, args) ->

        {
            final GamePlayer gamePlayer = CF.getPlayer(player);

            if (gamePlayer == null) {
                Chat.sendMessage(player, "&cCannot spawn outside a game!");
                return;
            }

            final LivingGameEntity entity = gamePlayer.spawnAlliedLivingEntity(player.getLocation(), Entities.HUSK, self -> {
                self.setGlowing(gamePlayer, ChatColor.GREEN, 60);
            });

            entity.getEntity().setSilent(true);

            gamePlayer.sendMessage("&aSpawned!");
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

                    final LinkedHashMap<String, Float> sorted = hardness.entrySet()
                            .stream()
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
                            Chat.sendMessage(player, "&aDumped into &e%s&a!".formatted(path.getAbsolutePath()));
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });

        register(new SimplePlayerAdminCommand("resetDaily") {
            @Override
            protected void execute(Player player, String[] strings) {
                final PlayerDatabase database = CF.getDatabase(player);

                for (DailyRewardEntry.Type type : DailyRewardEntry.Type.values()) {
                    database.dailyRewardEntry.setLastDaily(type, System.currentTimeMillis() - DailyReward.MILLIS_WHOLE_DAY);
                }

                Chat.sendMessage(player, "&aReset all daily rewards!");
            }
        });

        register(new SimplePlayerAdminCommand("setAttackSpeed") {
            @Override
            protected void execute(Player player, String[] args) {
                final double value = getArgument(args, 0).toDouble();

                final ItemStack item = player.getInventory().getItemInMainHand();
                final ItemMeta meta = item.getItemMeta();

                if (meta == null) {
                    Chat.sendMessage(player, "&cItem has no meta.");
                    return;
                }

                final Collection<AttributeModifier> modifiers = meta.getAttributeModifiers(Attribute.ATTACK_SPEED);

                meta.removeAttributeModifier(Attribute.ATTACK_SPEED);
                meta.addAttributeModifier(
                        Attribute.ATTACK_SPEED,
                        new AttributeModifier(
                                BukkitUtils.createKey(UUID.randomUUID()),
                                value,
                                AttributeModifier.Operation.ADD_NUMBER
                        )
                );

                item.setItemMeta(meta);

                Chat.sendMessage(player, "&aSuccess! Set attack speed to " + value);
            }
        });

        register(new SimplePlayerAdminCommand("skullBlockFace") {
            @Override
            protected void execute(Player player, String[] strings) {
                final Block block = player.getTargetBlockExact(100);

                if (block == null) {
                    Chat.sendMessage(player, "&cNo block in sight.");
                    return;
                }

                final BlockState state = block.getState();
                if (!(state instanceof Skull skull)) {
                    Chat.sendMessage(player, "&cTarget block is not a skull.");
                    return;
                }

                Chat.sendMessage(player, "&aBlock Face: &l" + skull.getRotation().name());
            }
        });

        register(new SimplePlayerAdminCommand("getTextBlockTestItem") {
            @Override
            protected void execute(Player player, String[] strings) {
                player.getInventory().addItem(ItemBuilder.of(Material.FEATHER).addTextBlockLore("""
                        This is a text block lore test, and this should be the first paragraph.
                        
                        &a;;Where this is the second one, and it's also all green!
                        
                        
                        Two paragraphs, wow!
                        
                        &c;;And I know your name, %s!
                        """.formatted(player.getName())).asIcon());
            }
        });

        register(new SimplePlayerAdminCommand("spawnWither") {

            @Override
            protected void execute(Player player, String[] args) {
                if (args.length != 3) {
                    Chat.sendMessage(player, "&cForgot (from:Int), (to:Int) and (speed:Long).");
                    return;
                }

                final int from = Numbers.getInt(args[0]);
                final int to = Numbers.getInt(args[1]);
                final long speed = Numbers.getLong(args[2]);

                new AnimatedWither(LocationHelper.getInFront(player.getLocation(), 6)) {

                    @Override
                    public void onInit(@Nonnull Wither wither) {
                        wither.setSilent(true);
                    }

                    @Override
                    public void onStart() {
                        Chat.sendMessage(player, "&aStarted %s->%s with speed %s".formatted(from, to, speed));
                    }

                    @Override
                    public void onStop() {
                        Chat.sendMessage(player, "&aStopped");
                        doLater(wither::remove, 60);
                    }

                    @Override
                    public void onTick(int tick) {
                        Chat.sendMessage(player, "&a>>" + getInvul());
                    }
                }.startAnimation(from, to, speed);
            }

        });

        register(new SimplePlayerAdminCommand("asyncDbTest") {

            private final Document FILTER = new Document("_dev", "hapyl");
            Document document;

            @Override
            protected void execute(Player player, String[] args) {
                final MongoCollection<Document> collection = getPlugin().getDatabase().getPlayers();
                document = collection.find(FILTER).first();

                if (document == null) {
                    document = new Document(FILTER);
                    collection.insertOne(document);
                }

                if (args.length >= 1) {
                    final String arg0 = args[0];

                    if (arg0.equalsIgnoreCase("dump")) {
                        document.forEach((k, v) -> {
                            Debug.info("%s = %s".formatted(k, v));
                        });
                        return;
                    }

                    if (args.length >= 2) {
                        final String arg1 = args[1];

                        if (arg0.equalsIgnoreCase("get")) {
                            final String get = document.get(arg1, "null");

                            Chat.sendMessage(player, "&e%s = &6%s".formatted(arg1, get));
                        }
                        else if (arg0.equalsIgnoreCase("set")) {
                            if (args.length < 3) {
                                Chat.sendMessage(player, "Forgot the value, stupid.");
                                return;
                            }

                            final String toSet = args[2];

                            Runnables.runAsync(() -> {
                                document = collection.findOneAndUpdate(document, Updates.set(arg1, toSet));
                                Chat.sendMessage(player, "&aSet and update %s.".formatted(toSet));
                            });
                        }
                    }

                    return;
                }

                Chat.sendMessage(player, "&cInvalid usage, idiot.");
            }
        });

        register(new SimplePlayerAdminCommand("spawnme") {

            HumanNPC npc;

            @Override
            protected void execute(Player player, String[] strings) {
                if (npc != null) {

                    if (strings.length > 0) {
                        Chat.sendMessage(player, "&aApplying skin...");
                        npc.setSkin(strings[0]);
                        return;
                    }

                    npc.remove();
                    npc = null;
                    Chat.sendMessage(player, "&aRemoved!");
                    return;
                }

                npc = new HumanNPC(player.getLocation(), player.getName(), player.getName());
                npc.setLookAtCloseDist(5);
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
                        Reflect.updateMetadata(minecraftEntity, player);
                    }
                }.runTaskTimer(main, 0, 1);
            }
        });

        // these are small shortcuts not feeling creating a class D:

        register("start", (player, args) ->

        {
            player.performCommand("cf start " + Chat.arrayToString(args.array, 0));
        });

        register(new TestNpcDeathAnimationCommand("testNpcDeathAnimation"));

        register("testNpcTeleport", (player, args) ->

        {
            final Human human = HumanNPC.create(player.getLocation());
            human.showAll();

            new TickingGameTask() {
                @Override
                public void run(int tick) {
                    if (tick >= 100) {
                        human.remove();
                        cancel();
                        Chat.sendMessage(player, "&cRemoved!");
                        return;
                    }

                    human.teleport(player.getLocation());
                }
            }.runTaskTimer(0, 1);
        });

        register(new SimpleAdminCommand("stop") {
            // true -> stop server, false -> stop game instance

            @Override
            protected void execute(CommandSender sender, String[] args) {
                if (sender instanceof ConsoleCommandSender) {
                    Bukkit.dispatchCommand(sender, "minecraft:stop");
                    return;
                }

                final String arg = getArgument(args, 0).toString();
                Bukkit.dispatchCommand(sender, (arg.equalsIgnoreCase("server") || arg.equalsIgnoreCase("s")) ? "minecraft:stop" : "cf stop");
            }
        });

        register(new SimplePlayerAdminCommand("calcDef") {
            @Override
            protected void execute(Player player, String[] args) {
                final double damage = getArgument(args, 0).toDouble();
                final double defense = getArgument(args, 1).toDouble();

                if (damage == 0 && defense == 0) {
                    Notifier.Error.INVALID_USAGE.send(player, "/calcDef (Damage) (Defense)");
                    return;
                }

                final double calcDamage = damage / (defense * Attributes.DEFENSE_SCALING + (1 - Attributes.DEFENSE_SCALING));

                Notifier.success(player, "Done!");
                Chat.sendMessage(player, "&a%.1f &8= %s DMG & %s DEF".formatted(calcDamage, damage, defense));
            }
        });

        register("showCommands", (player, args) ->

        {
            Debug.info("Here is a list of commands:");

            ChatColor color = ChatColor.AQUA;

            for
            (String command : commands) {
                Debug.info(" " + color + command);
                color = color == ChatColor.AQUA ? ChatColor.DARK_AQUA : ChatColor.AQUA;
            }
        });

    }

    private void registerDebug(String name, BiConsumer<GamePlayer, ArgumentList> consumer) {
        register(name, (player, args) -> {
            final GamePlayer gamePlayer = CF.getPlayer(player);

            if (gamePlayer == null) {
                Chat.sendMessage(player, "&4You must be in game to use this command!");
                return;
            }

            if (!Manager.current().isDebug()) {
                Chat.sendMessage(player, "&4You must be in debug mode to use this command!");
                return;
            }

            consumer.accept(gamePlayer, args);
        });
    }

    private void register(String name, BiConsumer<Player, ArgumentList> consumer) {
        processor.registerCommand(new CFCommand(name, PlayerRank.ADMIN) {
            @Override
            protected void execute(@Nonnull Player player, @Nonnull ArgumentList args, @Nonnull PlayerRank rank) {
                consumer.accept(player, args);
            }
        });
    }

    private void register(SimpleCommand command) {
        processor.registerCommand(command);
    }

}

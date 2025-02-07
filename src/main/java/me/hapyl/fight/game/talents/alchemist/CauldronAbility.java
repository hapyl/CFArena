package me.hapyl.fight.game.talents.alchemist;

import me.hapyl.eterna.module.block.display.BDEngine;
import me.hapyl.eterna.module.block.display.DisplayData;
import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.loadout.HotBarSlot;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.garbage.SynchronizedGarbageEntityCollector;
import me.hapyl.fight.util.collection.player.PlayerMap;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class CauldronAbility extends Talent implements Listener {

    public final Key cooldownKey = Key.ofString("stick_cooldown");

    private final PlayerMap<AlchemicalCauldron> cauldrons = PlayerMap.newMap();
    private final ItemStack missingStickItem = new ItemBuilder(Material.CLAY_BALL)
            .setName("&cStick is Missing!")
            .setSmartLore("Your stick is currently brewing a potion! Click the cauldron to get it back.")
            .asIcon();
    private final DisplayData cauldron;

    public CauldronAbility(@Nonnull Key key) {
        super(key, "Brewing Pot");

        setDescription("""
                Place a Brewing Cauldron to brew a Magic Potion. Put your Brewing Stick in it and wait!
                
                Once ready, claim you potion and enhance yourself with the following effects:
                
                &a- &7Drinking a potion will grant double effects. &8(5 charges)
                
                &a- &7Hitting an enemy will apply random effect. &8(10 charges)
                """
        );

        setTexture("e712289a6dac7c79c6effcd39db399c6ab6cb9d07f545eb72781678ec9ca013c");

        setType(TalentType.ENHANCE);
        setCooldownSec(120);

        this.cauldron = BDEngine.parse(
                "/summon block_display ~-0.5 ~-0.5 ~-0.5 {Passengers:[{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone\",Properties:{}},transformation:[0.6098f,0f,-0.4457f,-0.5835f,0f,0.4666f,0f,0.1956f,0.4366f,0f,0.6225f,-0.0369f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone\",Properties:{}},transformation:[0f,-0.5057f,-0.4941f,0.0025f,0.4038f,0f,0f,0.2366f,0f,-0.3621f,0.6902f,0.3257f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone\",Properties:{}},transformation:[0.3582f,0f,-0.6785f,-0.3352f,0f,-0.4082f,0f,0.637f,-0.5003f,0f,-0.4858f,0.9863f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone_slab\",Properties:{type:\"bottom\"}},transformation:[0f,0.7398f,0.5112f,-0.9415f,-0.6503f,0f,0f,0.7444f,0f,-1.0333f,0.366f,0.5692f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;1427971962,1402095689,1051476848,-278496764],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTVkOTJlODllNDczN2RjZmFlNWU1NzFjNzZmNzYwNGE3YWI3YzllMDNmNzhjMTgyMTcxZDEwOTU3MTQxNTZkMSJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.4879f,0f,-0.1091f,-0.2697f,0f,0.6188f,0f,0.5906f,0.3493f,0f,0.1525f,0.1777f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-501146188,-577774258,631443764,-1963955739],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTVkOTJlODllNDczN2RjZmFlNWU1NzFjNzZmNzYwNGE3YWI3YzllMDNmNzhjMTgyMTcxZDEwOTU3MTQxNTZkMSJ9fX0=\"}]}}},item_display:\"none\",transformation:[-0.3493f,0f,-0.1525f,-0.1882f,0f,0.6188f,0f,0.5831f,0.4879f,0f,-0.1091f,0.708f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;1305278193,1919865314,1368971145,28701749],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTVkOTJlODllNDczN2RjZmFlNWU1NzFjNzZmNzYwNGE3YWI3YzllMDNmNzhjMTgyMTcxZDEwOTU3MTQxNTZkMSJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.3493f,0f,0.1525f,-0.8031f,0f,0.6188f,0f,0.5894f,-0.4879f,0f,0.1091f,0.2709f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;952565844,1106198683,2105650441,1240245227],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTVkOTJlODllNDczN2RjZmFlNWU1NzFjNzZmNzYwNGE3YWI3YzllMDNmNzhjMTgyMTcxZDEwOTU3MTQxNTZkMSJ9fX0=\"}]}}},item_display:\"none\",transformation:[-0.4879f,0f,0.1091f,-0.7183f,0f,0.6188f,0f,0.5875f,-0.3493f,0f,-0.1525f,0.8043f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_brick_wall\",Properties:{up:\"true\"}},transformation:[0.1936f,0.0439f,-0.1596f,-0.5966f,-0.0865f,0.3353f,-0.06f,0.0385f,0.1395f,0.1471f,0.1843f,-0.0949f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone_wall\",Properties:{up:\"true\"}},transformation:[0f,0.0847f,-0.742f,0.1329f,-0.2625f,0f,0f,0.9119f,0f,0.0607f,1.0364f,0.1648f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone_wall\",Properties:{up:\"true\"}},transformation:[0f,0.0607f,1.0365f,-1.2602f,-0.2625f,0f,0f,0.9119f,0f,-0.0847f,0.7421f,0.4702f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone_wall\",Properties:{up:\"true\"}},transformation:[0f,0.0847f,-0.7636f,-0.4527f,-0.2625f,0f,0f,0.9119f,0f,0.0607f,1.0666f,-0.2874f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone_wall\",Properties:{up:\"true\"}},transformation:[0f,0.0607f,1.0437f,-0.8292f,-0.2625f,0f,0f,0.9119f,0f,-0.0847f,0.7472f,-0.1291f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:sculk\",Properties:{}},transformation:[0.5137f,0f,-0.4027f,-0.5419f,0f,0.0688f,0f,0.7244f,0.3678f,0f,0.5624f,0.0197f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:frogspawn\",Properties:{}},transformation:[0.1056f,0f,-0.2547f,-0.4322f,0f,1f,0f,0.7838f,0.2513f,0f,0.1071f,0.2251f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:fire_coral\",Properties:{}},transformation:[0.1619f,0f,-0.4362f,-0.3485f,0f,0.4063f,0f,0.4838f,0.3364f,0f,0.2099f,0.2792f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_brick_wall\",Properties:{up:\"true\"}},transformation:[0.1395f,0.1471f,0.1843f,-1.0826f,-0.0865f,0.3353f,-0.06f,0.0374f,-0.1936f,-0.0439f,0.1596f,0.5931f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_brick_wall\",Properties:{up:\"true\"}},transformation:[-0.1936f,-0.0439f,0.1596f,-0.4059f,-0.0865f,0.3353f,-0.06f,0.0374f,-0.1395f,-0.1471f,-0.1843f,1.0843f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_brick_wall\",Properties:{up:\"true\"}},transformation:[-0.1395f,-0.1471f,-0.1843f,0.0874f,-0.0865f,0.3353f,-0.06f,0.0374f,0.1936f,0.0439f,-0.1596f,0.3908f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_brick_fence\",Properties:{}},transformation:[0.2385f,0.0858f,0.1754f,-0.5535f,0f,-0.1721f,0.2583f,0.3917f,0.1707f,-0.1199f,-0.245f,0.1658f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_brick_fence\",Properties:{}},transformation:[0.2377f,0.0858f,0.1754f,-0.3949f,0f,-0.1721f,0.2583f,0.3917f,0.1702f,-0.1199f,-0.245f,0.2793f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:stripped_dark_oak_log\",Properties:{axis:\"x\"}},transformation:[0.1246f,0.0308f,0.0304f,-0.2998f,0f,-0.0617f,0.0448f,0.4056f,0.0892f,-0.043f,-0.0425f,0.096f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:chain\",Properties:{axis:\"x\"}},transformation:[0f,0.2394f,-0.3049f,-0.1646f,0.1469f,0f,0f,0.22f,0f,-0.3344f,-0.2183f,0.3667f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_brick_fence\",Properties:{}},transformation:[-0.2385f,-0.0858f,-0.1754f,-0.4376f,0f,-0.1721f,0.2583f,0.3917f,-0.1707f,0.1199f,0.245f,0.8221f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_brick_fence\",Properties:{}},transformation:[-0.2377f,-0.0858f,-0.1754f,-0.5962f,0f,-0.1721f,0.2583f,0.3917f,-0.1702f,0.1199f,0.245f,0.7086f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:stripped_dark_oak_log\",Properties:{axis:\"x\"}},transformation:[-0.1246f,-0.0308f,-0.0304f,-0.6913f,0f,-0.0617f,0.0448f,0.4056f,-0.0892f,0.043f,0.0425f,0.8919f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:chain\",Properties:{axis:\"x\"}},transformation:[0f,-0.2394f,0.3049f,-0.8265f,0.1469f,0f,0f,0.22f,0f,0.3344f,0.2183f,0.6212f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone_stairs\",Properties:{facing:\"east\",half:\"bottom\",shape:\"inner_left\"}},transformation:[0.1455f,0f,-0.1044f,-0.1213f,0f,0.1006f,0f,0.7313f,0.1042f,0f,0.1458f,0.3094f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone_stairs\",Properties:{facing:\"east\",half:\"bottom\",shape:\"inner_left\"}},transformation:[-0.1042f,0f,-0.1458f,-0.3056f,0f,0.1006f,0f,0.7294f,0.1455f,0f,-0.1044f,0.8738f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone_stairs\",Properties:{facing:\"east\",half:\"bottom\",shape:\"inner_left\"}},transformation:[-0.1455f,0f,0.1044f,-0.8705f,0f,0.1006f,0f,0.7294f,-0.1042f,0f,-0.1458f,0.6892f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone_stairs\",Properties:{facing:\"east\",half:\"bottom\",shape:\"inner_left\"}},transformation:[0.1042f,0f,0.1458f,-0.6895f,0f,0.1006f,0f,0.7294f,-0.1455f,0f,0.1044f,0.1239f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:sculk\",Properties:{}},transformation:[-0.0364f,0f,-0.0009f,-0.2206f,0f,0.1574f,0f,0.6888f,0.0508f,0f,-0.0006f,0.8378f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:sculk\",Properties:{}},transformation:[-0.05f,0f,-0.0855f,-0.1842f,0f,0.1141f,0f,0.7319f,0.0698f,0f,-0.0612f,0.787f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:sculk\",Properties:{}},transformation:[-0.0364f,-0.0851f,0f,-0.2206f,0f,0f,-0.0011f,0.8469f,0.0508f,-0.0609f,0f,0.8378f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:sculk\",Properties:{}},transformation:[-0.0364f,-0.0851f,0f,-0.1849f,0f,0f,-0.0011f,0.8469f,0.0508f,-0.0609f,0f,0.788f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:sculk\",Properties:{}},transformation:[-0.0164f,-0.0475f,0f,-0.2687f,0f,0f,-0.0011f,0.8469f,0.0228f,-0.034f,0f,0.8803f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:sculk\",Properties:{}},transformation:[-0.0196f,-0.0475f,0f,-0.2019f,0f,0f,-0.0011f,0.8469f,0.0273f,-0.034f,0f,0.7505f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:sculk\",Properties:{}},transformation:[-0.0123f,0f,-0.0009f,-0.2521f,0f,0.0666f,0f,0.7594f,0.0172f,0f,-0.0006f,0.8829f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:cyan_candle\",Properties:{candles:\"1\",lit:\"false\"}},transformation:[0.4159f,0f,-0.1548f,-0.7403f,0f,0.2563f,0f,0.8281f,0.1563f,0f,0.4119f,-0.2099f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:cyan_candle\",Properties:{candles:\"1\",lit:\"false\"}},transformation:[0.4159f,0f,-0.1548f,-0.6919f,0f,0.378f,0f,0.8281f,0.1563f,0f,0.4119f,-0.2537f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:cyan_candle\",Properties:{candles:\"1\",lit:\"false\"}},transformation:[0.4159f,0f,-0.1548f,-0.6758f,0f,0.3236f,0f,0.8281f,0.1563f,0f,0.4119f,-0.2004f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:nautilus_shell\",Count:1},item_display:\"none\",transformation:[-0.0431f,-0.1924f,0.1227f,-0.7571f,-0.005f,0.048f,0.5312f,0.7775f,-0.1829f,0.044f,-0.0435f,0.5083f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:armadillo_scute\",Count:1},item_display:\"none\",transformation:[0.2864f,0.1195f,-0.0368f,-0.3377f,-0.0172f,0.1288f,0.2842f,0.7963f,0.1238f,-0.2584f,0.1246f,0.4903f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[0.1533f,0.0171f,-0.2662f,-0.3463f,0.0247f,0.2168f,0.0341f,0.685f,0.3345f,-0.0238f,0.1195f,0.6141f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:purple_candle\",Properties:{candles:\"1\",lit:\"false\"}},transformation:[0.4159f,0f,-0.1548f,-0.2214f,0f,0.2563f,0f,0.1056f,0.1563f,0f,0.4119f,0.6044f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:purple_candle\",Properties:{candles:\"1\",lit:\"false\"}},transformation:[0.0108f,0.0016f,0.0221f,-0.0537f,-0.4609f,0f,0.0018f,0.3113f,0.0046f,-0.0003f,0.1274f,0.7717f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:purple_candle\",Properties:{candles:\"1\",lit:\"false\"}},transformation:[0.4609f,0f,-0.0061f,-0.3335f,0.0099f,0.0016f,0.0057f,0.1031f,0.0063f,0f,0.4399f,0.6268f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:purple_candle\",Properties:{candles:\"1\",lit:\"false\"}},transformation:[0.4482f,0f,-0.1027f,-0.2451f,0.0099f,0.0016f,0.0057f,0.1031f,0.1074f,0f,0.4278f,0.5939f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:purple_candle\",Properties:{candles:\"1\",lit:\"false\"}},transformation:[0.4482f,0f,-0.1027f,-0.2452f,0.0099f,0.0016f,0.0057f,0.1031f,0.1074f,0f,0.4278f,0.623f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:purple_candle\",Properties:{candles:\"1\",lit:\"false\"}},transformation:[0.4601f,0f,0.0254f,-0.3063f,0.0099f,0.0016f,0.0057f,0.1031f,-0.0267f,0f,0.4392f,0.6521f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:purple_candle\",Properties:{candles:\"1\",lit:\"false\"}},transformation:[0.3984f,0f,-0.2213f,-0.1862f,0.0099f,0.0016f,0.0057f,0.1031f,0.2317f,0f,0.3803f,0.603f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:purple_candle\",Properties:{candles:\"1\",lit:\"false\"}},transformation:[0.0108f,0.0016f,0.0221f,-0.0507f,-0.4609f,0f,0.0018f,0.3113f,0.0046f,-0.0003f,0.1274f,0.7868f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:light_gray_stained_glass\",Properties:{}},transformation:[0.0765f,0f,-0.0471f,-0.1949f,0f,0.1852f,0f,0f,0.0469f,0f,0.0769f,0.7429f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:light_gray_stained_glass\",Properties:{}},transformation:[0.0797f,0f,-0.0756f,-0.112f,0f,0.1753f,0f,0f,0.0726f,0f,0.0831f,0.6264f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:light_gray_stained_glass\",Properties:{}},transformation:[0f,0.031f,-0.1097f,-0.044f,-0.115f,0f,0f,0.1106f,0f,0.1725f,0.0197f,0.8024f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:fire_coral_block\",Properties:{}},transformation:[0.0711f,0f,-0.0428f,-0.1948f,0f,0.1289f,0f,0f,0.0436f,0f,0.0698f,0.7488f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:bubble_coral_block\",Properties:{}},transformation:[0.0709f,0f,-0.0651f,-0.1134f,0f,0.0997f,0f,0f,0.0645f,0f,0.0716f,0.6353f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_slab\",Properties:{type:\"bottom\"}},transformation:[0.0605f,0f,-0.0376f,-0.1923f,0f,0.0494f,0f,0.1762f,0.0371f,0f,0.0613f,0.7554f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_slab\",Properties:{type:\"bottom\"}},transformation:[0.0672f,0f,-0.0639f,-0.1123f,0f,0.0494f,0f,0.1675f,0.0612f,0f,0.0702f,0.6379f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:tube_coral_block\",Properties:{}},transformation:[0f,0.0289f,-0.1097f,-0.0432f,-0.006f,0f,0f,0.0113f,0f,0.1606f,0.0197f,0.8067f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_slab\",Properties:{type:\"bottom\"}},transformation:[0.0672f,0f,-0.0639f,-0.2519f,0f,0.0494f,0f,0f,0.0612f,0f,0.0702f,0.8475f,0f,0f,0f,1f]}]}"
        );
    }

    @EventHandler()
    public void handleInteraction(PlayerInteractEvent ev) {
        if (!Manager.current().isGameInProgress()) {
            return;
        }

        final GamePlayer player = CF.getPlayer(ev.getPlayer());

        if (player == null) {
            return;
        }

        final Block clickedBlock = ev.getClickedBlock();

        if (ev.getHand() == EquipmentSlot.OFF_HAND
                || ev.getAction() != Action.RIGHT_CLICK_BLOCK
                || clickedBlock == null
                || clickedBlock.getType() != Material.WATER_CAULDRON) {
            return;
        }

        if (!HeroRegistry.ALCHEMIST.isSelected(player)) {
            return;
        }

        // Prevent wrong clicks by adding a tiny cooldown
        if (player.cooldownManager.hasCooldown(cooldownKey)) {
            return;
        }

        final AlchemicalCauldron cauldron = cauldrons.get(player);
        if (cauldron == null || !cauldron.compareBlock(clickedBlock)) {
            return;
        }

        ev.setCancelled(true);
        switch (cauldron.getStatus()) {

            case NEUTRAL, PAUSED -> {
                cauldron.setStatus(AlchemicalCauldron.Status.BREWING);
                changeItem(player, false);
            }

            case BREWING -> {
                cauldron.setStatus(AlchemicalCauldron.Status.PAUSED);
                changeItem(player, true);
            }

            case FINISHED -> {
                cauldron.finish();
                cauldron.clear();
                changeItem(player, true);
            }

        }
    }

    @Override
    public void onStop(@Nonnull GameInstance instance) {
        cauldrons.values().forEach(AlchemicalCauldron::clear);
        cauldrons.clear();
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        final AlchemicalCauldron cauldron = cauldrons.remove(player);

        if (cauldron != null) {
            cauldron.clear();
        }
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        if (true) {
            final DisplayEntity display = cauldron.spawn(player.getLocationWithoutRotation());

            SynchronizedGarbageEntityCollector.add(display);
            return Response.OK;
        }

        final Block targetBlock = getTargetBlock(player);

        if (targetBlock == null) {
            return Response.error("Invalid target block!");
        }

        if (!targetBlock.getType().isAir()) {
            return Response.error("Target block is occupied!");
        }

        if (cauldrons.containsKey(player)) {
            return Response.error("You already have a cauldron!");
        }

        cauldrons.put(player, new AlchemicalCauldron(player, targetBlock.getLocation().clone()));
        return Response.OK;

    }

    private void changeItem(GamePlayer player, boolean flag) {
        GameTask.runLater(() -> {
            if (flag) {
                final Weapon weapon = HeroRegistry.ALCHEMIST.getWeapon();

                player.setItem(HotBarSlot.WEAPON, weapon.getItem());
            }
            else {
                player.setItem(HotBarSlot.WEAPON, missingStickItem);
            }

            player.cooldownManager.setCooldownIgnoreCooldownModifier(cooldownKey, 10);
        }, 1);
    }

    private Block getTargetBlock(GamePlayer player) {
        final Block targetBlock = player.getTargetBlockExact(5);

        if (targetBlock == null) {
            return null;
        }
        return targetBlock.getRelative(BlockFace.UP);
    }

}

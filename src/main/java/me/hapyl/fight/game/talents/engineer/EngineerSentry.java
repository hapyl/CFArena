package me.hapyl.fight.game.talents.engineer;

import me.hapyl.eterna.module.block.display.BDEngine;
import me.hapyl.eterna.module.block.display.DisplayData;
import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.eterna.module.reflect.glowing.Glowing;
import me.hapyl.eterna.module.reflect.glowing.GlowingColor;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.RaycastTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class EngineerSentry extends EngineerConstructTalent {
    
    @DisplayField private final double damage = 3;
    @DisplayField private final double radius = 32;
    
    @DisplayField private final int delayBetweenShots = 20;
    @DisplayField private final double damageIncreasePerLevel = 1.5;
    
    // This is the base of the turret (the bottom side)
    // It will be spawned with the main block display.
    private final DisplayData turretBase = BDEngine.parse(
            "{Passengers:[{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:vault\",Properties:{facing:\"east\",ominous:\"false\",vault_state:\"active\"}},transformation:[0f,0f,0.78f,-0.3840497612f,0f,0.2035f,0f,0.1150195313f,-0.795f,0f,0f,0.4021747612f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;1298603976,-1200420779,-159794942,-2024765368],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTY2YTViN2JkZmFhOTNkNDRjNTBjNjQ0Y2QwNDVjMzk4YTUwMTBhNzk3NzkwMDcyOTBjOWE3OGNmODk4NjIzYSJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.3535533906f,0f,-0.3535533906f,-0.0021747612f,0f,-0.70175f,0f,-0.0465429687f,-0.3535533906f,0f,-0.3535533906f,-0.3897002388f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;736675776,1825975079,-570176915,1585245295],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTY2YTViN2JkZmFhOTNkNDRjNTBjNjQ0Y2QwNDVjMzk4YTUwMTBhNzk3NzkwMDcyOTBjOWE3OGNmODk4NjIzYSJ9fX0=\"}]}}},item_display:\"none\",transformation:[-0.3535533906f,0f,-0.3535533906f,0.0034502388f,0f,-0.65625f,0f,-0.0277929687f,-0.3535533906f,0f,0.3535533906f,0.4021747612f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;1334175846,1936148131,1737930074,618157005],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTY2YTViN2JkZmFhOTNkNDRjNTBjNjQ0Y2QwNDVjMzk4YTUwMTBhNzk3NzkwMDcyOTBjOWE3OGNmODk4NjIzYSJ9fX0=\"}]}}},item_display:\"none\",transformation:[-0.3535533906f,0f,0.3535533906f,-0.3840497612f,0f,-0.65625f,0f,-0.0240429687f,0.3535533906f,0f,0.3535533906f,0.0027997612f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-1266156079,-1012010166,2033014693,-1917174023],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTY2YTViN2JkZmFhOTNkNDRjNTBjNjQ0Y2QwNDVjMzk4YTUwMTBhNzk3NzkwMDcyOTBjOWE3OGNmODk4NjIzYSJ9fX0=\"}]}}},item_display:\"head\",transformation:[0.3535533906f,0f,0.3535533906f,0.3968877388f,0f,-0.65625f,0f,-0.0290429687f,0.3535533906f,0f,-0.3535533906f,0.0052997612f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:cut_copper_slab\",Properties:{type:\"bottom\"}},transformation:[0f,0f,0.8325f,-0.4187372612f,0f,0.28625f,0f,0.0268945313f,-0.866f,0f,0f,0.4331122612f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-1097395719,837060578,-235656754,842796942],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTlmMzdmMWNiZDQ3YzM1MDQ1MTFiZjMzYzU4YzNhMjUyYjYwNzEzZWM1ZmM1NDMzZDg4N2Q0YTBkOTk2MjEwIn19fQ==\"}]}}},item_display:\"none\",transformation:[0f,-0.5f,0f,0.1575127388f,0f,0f,0.78125f,0.1050195313f,-0.5f,0f,0f,-0.2997002388f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;109196555,1462901181,949229202,19996988],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTlmMzdmMWNiZDQ3YzM1MDQ1MTFiZjMzYzU4YzNhMjUyYjYwNzEzZWM1ZmM1NDMzZDg4N2Q0YTBkOTk2MjEwIn19fQ==\"}]}}},item_display:\"none\",transformation:[0f,-0.5f,0f,0.1575127388f,0f,0f,0.78125f,0.1050195313f,-0.5f,0f,0f,0.3065497612f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-2060438939,1411042050,-1612064712,1211236332],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTlmMzdmMWNiZDQ3YzM1MDQ1MTFiZjMzYzU4YzNhMjUyYjYwNzEzZWM1ZmM1NDMzZDg4N2Q0YTBkOTk2MjEwIn19fQ==\"}]}}},item_display:\"none\",transformation:[0f,-0.5f,0f,-0.4131122612f,0f,0f,0.78125f,0.1050195313f,-0.5f,0f,0f,-0.3003252388f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-1834331126,-1279597427,-1662637721,-1556877009],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTlmMzdmMWNiZDQ3YzM1MDQ1MTFiZjMzYzU4YzNhMjUyYjYwNzEzZWM1ZmM1NDMzZDg4N2Q0YTBkOTk2MjEwIn19fQ==\"}]}}},item_display:\"none\",transformation:[0f,-0.5f,0f,-0.4131122612f,0f,0f,0.78125f,0.1050195313f,-0.5f,0f,0f,0.3052997612f,0f,0f,0f,1f]}]}"
    );
    
    public EngineerSentry(@Nonnull Key key) {
        super(
                key,
                "Sentry",
                5,
                5,
                "{Passengers:[{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:lightning_rod\",Properties:{facing:\"down\",powered:\"false\"}},transformation:[0f,0f,0.3465f,-0.1637695312f,0.0491800005f,0.3389053099f,0f,0.548125f,-0.269550232f,0.0618339787f,0f,0.0398241286f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:lightning_rod\",Properties:{facing:\"down\",powered:\"false\"}},transformation:[0f,0f,0.3465f,-0.1637695312f,-0.0491800005f,-0.3389053099f,0f,1.2328125f,0.269550232f,-0.0618339787f,0f,-0.1139258714f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:black_candle\",Properties:{candles:\"1\",lit:\"false\"}},transformation:[0f,0f,-0.7f,0.3593554688f,-0.11316359f,0.6907923001f,0f,0.713125f,0.6907923001f,0.11316359f,0f,-0.4151758714f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_brick_wall\",Properties:{up:\"true\"}},transformation:[0f,0f,0.336f,-0.0712695312f,0.299667661f,0.0838654096f,0f,1.275625f,-0.0331895006f,0.7572199255f,0f,0.4770116286f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_brick_wall\",Properties:{up:\"true\"}},transformation:[0f,0f,0.336f,-0.2603320312f,0.299667661f,0.0838654096f,0f,1.275625f,-0.0331895006f,0.7572199255f,0f,0.4770116286f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_brick_wall\",Properties:{up:\"true\"}},transformation:[0f,0f,0.336f,-0.1697070312f,0.299667661f,0.0838654096f,0f,1.4440625f,-0.0331895006f,0.7572199255f,0f,0.4585741286f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:lodestone\",Properties:{}},transformation:[0.0003168368f,-0.1166318323f,0.1235803286f,-0.0081445312f,-0.0283591829f,0.1162388818f,0.1225782746f,1.5596875f,-0.2634779998f,-0.012651504f,-0.0130449788f,1.2310741286f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:lodestone\",Properties:{}},transformation:[0f,-0.1739f,0f,0.0834179688f,-0.0291715345f,0f,0.1638978352f,1.591875f,-0.2633894865f,0f,-0.0181524001f,1.2276366286f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:lodestone\",Properties:{}},transformation:[0f,-0.1739f,0f,-0.0050195312f,-0.0291715345f,0f,0.1638978352f,1.4353125f,-0.2633894865f,0f,-0.0181524001f,1.2448241286f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:lodestone\",Properties:{}},transformation:[0.0003168368f,-0.1166318323f,0.1235803286f,0.0940429688f,-0.0283591829f,0.1162388818f,0.1225782746f,1.4034375f,-0.2634779998f,-0.012651504f,-0.0130449788f,1.2485741286f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:lodestone\",Properties:{}},transformation:[0f,-0.1739f,0f,0.1856054688f,-0.0291715345f,0f,0.1638978352f,1.4353125f,-0.2633894865f,0f,-0.0181524001f,1.2448241286f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:blast_furnace\",Properties:{facing:\"east\",lit:\"false\"}},transformation:[0f,0f,0.399f,-0.2012695312f,0.3745597282f,0.0360516134f,0f,1.3025f,-0.0414841237f,0.3255096484f,0f,0.2348241286f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:copper_bulb\",Properties:{lit:\"false\",powered:\"false\"}},transformation:[0f,0f,0.5f,-0.2450195312f,-0.0468946177f,0.4969612952f,0f,1.21375f,-0.4234110235f,-0.0550406311f,0f,0.0641991286f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:netherite_block\",Properties:{}},transformation:[0f,0f,0.46875f,-0.2259570312f,-0.012824467f,0.4659012142f,0f,1.2346875f,-0.1157919818f,-0.0516005917f,0f,0.1248241286f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-131585876,315553644,604795268,1493887581],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTEyM2I4ODg0NmQ2NmUxY2ZlMmY2NjRhMzZhZDRhMjJiMWE0YzJmMmU0ZDI5NWY0MWZlNWU5MjliOWU3ZDgifX19\"}]}}},item_display:\"none\",transformation:[0f,-0.71875f,0f,-0.3387695312f,-0.0447205128f,0f,0.7143818618f,1.6521875f,-0.4037810523f,0f,-0.0791209072f,0.3310741286f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:copper_block\",Properties:{}},transformation:[0f,0f,0.297f,-0.2475195312f,-0.0275203156f,0.2169236054f,0f,1.5209375f,-0.2484806476f,-0.0240252355f,0f,0.4710741286f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:waxed_copper_trapdoor\",Properties:{facing:\"east\",half:\"bottom\",open:\"false\"}},transformation:[0f,0f,0.3615f,-0.1768945312f,0.3727209714f,0.0550406311f,0f,1.3590625f,-0.0412804733f,0.4969612952f,0f,0.7713866286f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;1804507257,-405048183,-1078937531,-1829141175],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmIyYWJkNjY5MzlmNGNiNzI1N2E4OGNmNTJmYmM2ZmRjZWVjMTQzM2VjMmE2ZWYxNmQ2MmUzNGY2MjM4NzgxIn19fQ==\"}]}}},item_display:\"none\",transformation:[-0.3125f,0f,0f,0.0002929688f,0f,0.3106008095f,-0.0103201183f,1.750625f,0f,-0.0344003944f,-0.0931802428f,1.2045116286f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;1062022626,-1396684179,-88445147,-2076992564],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmIyYWJkNjY5MzlmNGNiNzI1N2E4OGNmNTJmYmM2ZmRjZWVjMTQzM2VjMmE2ZWYxNmQ2MmUzNGY2MjM4NzgxIn19fQ==\"}]}}},item_display:\"none\",transformation:[-0.3125f,0f,0f,-0.0887695312f,0f,0.3106008095f,-0.0068800789f,1.5934375f,0f,-0.0344003944f,-0.0621201619f,1.2220116286f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;1872317673,-1847252742,229168656,1755023611],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmIyYWJkNjY5MzlmNGNiNzI1N2E4OGNmNTJmYmM2ZmRjZWVjMTQzM2VjMmE2ZWYxNmQ2MmUzNGY2MjM4NzgxIn19fQ==\"}]}}},item_display:\"none\",transformation:[-0.3125f,0f,0f,0.0987304688f,0f,0.3106008095f,-0.0068800789f,1.5934375f,0f,-0.0344003944f,-0.0621201619f,1.2220116286f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-363341458,-350116237,-959391478,-1869767047],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODRmYzU2ZDRiOGI3ZGNkZTlhNzQ4NzQ3MzQ1NDhiZDYyNzY4ZTFmY2E0ZTYzYjRlM2E1YmJjYTViYjMwYWE3MCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0f,0f,-0.5f,0.1549804688f,0.0554742906f,0.496913074f,0f,1.5553125f,0.496913074f,-0.0554742906f,0f,-0.1961133714f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:blackstone_wall\",Properties:{up:\"true\"}},transformation:[0f,0f,-0.379f,0.0634179688f,0.261992953f,0.471430858f,0f,0.623125f,0.4258634671f,-0.290026199f,0f,0.1251366286f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:blackstone_wall\",Properties:{up:\"true\"}},transformation:[0f,0f,-0.379f,0.3240429688f,0.261992953f,0.4624877252f,0f,0.6278125f,0.4258634671f,-0.2845243469f,0f,0.1223241286f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:blackstone_wall\",Properties:{up:\"true\"}},transformation:[0f,0f,-0.379f,0.1984179688f,-0.3118715931f,0.390814674f,0f,0.654375f,0.390814674f,0.3118715931f,0f,-0.2279883714f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-1734223358,319696572,-60158793,192345122],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTI5NjI3MTNhZGE1NGM0MWQ1NDcyNDE0NWNhZTJkNmFiYzhhM2NhZWFmM2E3YzJiZDcxOTk1ODYwYWE4Mjc4OCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0f,0f,0.96875f,0.0049804688f,0f,0.21875f,0f,0.92125f,-0.21875f,0f,0f,0.2654491286f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-88612688,599915844,-7846141,-1226464512],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGRjN2FiNGU3ZTRlZjI4ZmNiZTUxMzI3MTliNGEzNmY0ZjA1NmRkNzk0MWJjZTQwMGRmMzBjODU2NmI1ZGRlZCJ9fX0=\"}]}}},item_display:\"none\",transformation:[0f,0f,0.5f,0.0049804688f,0f,0.5f,0f,0.5703125f,-0.5475f,0f,0f,0.0045116286f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:smooth_stone_slab\",Properties:{type:\"bottom\"}},transformation:[0f,0f,0.45575f,-0.2234570312f,-0.046220655f,0.1240468778f,0f,1.19625f,-0.3721406334f,-0.015406885f,0f,0.2538866286f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-989560290,-871560345,-765369408,1539259685],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjYwNGFkY2JkN2Y3NmE4Yjg0Y2IzMDY2ZWRmMTUxNTQ4ZWEzMmJlMWRjNzA3ODFiZTU2M2Q0M2NiMThhMzk5YiJ9fX0=\"}]}}},item_display:\"none\",transformation:[0f,0f,0.5f,0.0065429688f,-0.4957451816f,-0.0650900524f,0f,1.3946875f,0.0650900524f,-0.4957451816f,0f,-0.4754883714f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:lodestone\",Properties:{}},transformation:[0.0003168368f,-0.1166318323f,0.1235803286f,-0.0965820312f,-0.0283591829f,0.1162388818f,0.1225782746f,1.4034375f,-0.2634779998f,-0.012651504f,-0.0130449788f,1.2485741286f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-2067580853,127998346,-379723862,-1444134454],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTk1ZDdjYjdlMTg3MzkyMTEwMmJkYmEzMDlhYTM4NTUzZjVmZDU4MGQxZWQ5ZDQ0Mzg5ZjUzZTA5ZWNkNWEwMiJ9fX0=\"}]}}},item_display:\"none\",transformation:[0f,0f,0.5f,0.1218554688f,-0.0521774424f,0.4972700619f,0f,1.479375f,-0.4972700619f,-0.0521774424f,0f,0.1920116286f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-418620663,811857531,1142613706,1314029998],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTk1ZDdjYjdlMTg3MzkyMTEwMmJkYmEzMDlhYTM4NTUzZjVmZDU4MGQxZWQ5ZDQ0Mzg5ZjUzZTA5ZWNkNWEwMiJ9fX0=\"}]}}},item_display:\"none\",transformation:[0f,0f,0.5f,0.1218554688f,-0.0521774424f,0.4972700619f,0f,1.7246875f,-0.4972700619f,-0.0521774424f,0f,0.1654491286f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;1897691452,-961388826,1986030280,-1168198844],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTk1ZDdjYjdlMTg3MzkyMTEwMmJkYmEzMDlhYTM4NTUzZjVmZDU4MGQxZWQ5ZDQ0Mzg5ZjUzZTA5ZWNkNWEwMiJ9fX0=\"}]}}},item_display:\"none\",transformation:[0f,0f,0.5f,-0.1140820312f,-0.0521774424f,0.4972700619f,0f,1.479375f,-0.4972700619f,-0.0521774424f,0f,0.1920116286f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-664103263,-442844804,1558035813,-1226016478],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTk1ZDdjYjdlMTg3MzkyMTEwMmJkYmEzMDlhYTM4NTUzZjVmZDU4MGQxZWQ5ZDQ0Mzg5ZjUzZTA5ZWNkNWEwMiJ9fX0=\"}]}}},item_display:\"none\",transformation:[0f,0f,0.5f,-0.1140820312f,-0.0521774424f,0.4972700619f,0f,1.7246875f,-0.4972700619f,-0.0521774424f,0f,0.1654491286f,0f,0f,0f,1f]}]}"
        );
        
        setDescription("""
                       Create a &fSentry&7 that scans for &cenemies&7 and automatically shoots them.
                       """);
        
        setType(TalentType.DAMAGE);
        setMaterial(Material.NETHERITE_SCRAP);
        
        setCooldownSec(35);
        
        yOffset = 2.25d;
    }
    
    @Nonnull
    @Override
    public Construct create(@Nonnull GamePlayer player, @Nonnull Location location) {
        return new SentryConstruct(player, location);
    }
    
    private class SentryConstruct extends Construct {
        private final DisplayEntity turretBase;
        
        SentryConstruct(@Nonnull GamePlayer player, @Nonnull Location location) {
            super(player, location, EngineerSentry.this);
            
            this.turretBase = EngineerSentry.this.turretBase.spawn(
                    location, self -> {
                        self.setTeleportDuration(3);
                        
                        Glowing.setGlowing(player.getEntity(), self, GlowingColor.GREEN);
                    }
            );
        }
        
        @Override
        public void onCreate() {
        }
        
        @Nonnull
        @Override
        public ImmutableInt3Array healthScaled() {
            return new ImmutableInt3Array(40, 60, 80);
        }
        
        @Nonnull
        @Override
        public ImmutableInt3Array chargesScaled() {
            return new ImmutableInt3Array(12, 20, 28);
        }
        
        @Override
        public void onDestroy() {
            turretBase.remove();
        }
        
        @Override
        public boolean onTick() {
            final LivingGameEntity nearestEntity = Collect.nearestEntity(
                    location, radius, entity -> {
                        if (player.isSelfOrTeammate(entity)) {
                            return false;
                        }
                        
                        return entity.hasLineOfSight(this.constructEntity);
                    }
            );
            
            final DisplayEntity displayEntity = constructEntity.getDisplayEntity();
            
            // Rotate if there are no entities
            if (nearestEntity == null) {
                final BlockDisplay head = displayEntity.getHead();
                final Location location = head.getLocation();
                
                location.setPitch(0.0f);
                location.setYaw(location.getYaw() + 5);
                displayEntity.teleport(location);
                return false;
            }
            
            // Look at target
            final Location entityLocation = constructEntity.getLocation();
            final Vector vector = nearestEntity.getMidpointLocation().toVector().subtract(location.toVector());
            
            vector.subtract(new Vector(0, 1, 0));
            entityLocation.setDirection(vector);
            
            // Clamp pitch so it isn't upside down
            entityLocation.setPitch(
                    Math.clamp(entityLocation.getPitch(), -30, 30)
            );
            
            displayEntity.teleport(entityLocation);
            
            if (tick == 0 || tick % delayBetweenShots != 0) {
                return false;
            }
            
            new RaycastTask(constructEntity.getLocation().add(0, 1.5, 0.0)) {
                @Override
                public boolean predicate(@Nonnull Location location) {
                    final Block block = location.getBlock();
                    final Material type = block.getType();
                    
                    return !type.isOccluding();
                }
                
                @Override
                public boolean step(@Nonnull Location location) {
                    player.spawnWorldParticle(location, Particle.ENCHANTED_HIT, 1);
                    
                    // Hit detection
                    final LivingGameEntity targetEntity = Collect.nearestEntity(
                            location, 1, player::isNotSelfOrTeammate
                    );
                    
                    if (targetEntity == null) {
                        return false;
                    }
                    
                    targetEntity.damageNoKnockback(damage + (getLevel() * damageIncreasePerLevel), player, DamageCause.SENTRY_SHOT);
                    return true;
                }
            }.setStep(0.5)
             .setIterations(3)
             .runTaskTimer(0, 1);
            
            // Fx
            player.playWorldSound(location, Sound.ENTITY_BLAZE_SHOOT, 1.25f);
            player.playWorldSound(location, Sound.ENTITY_BLAZE_SHOOT, 2.0f);
            
            return true;
        }
    }
}

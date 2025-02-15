package me.hapyl.fight.game.talents.alchemist;

import me.hapyl.eterna.module.block.display.BDEngine;
import me.hapyl.eterna.module.block.display.DisplayData;
import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.hologram.Hologram;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.alchemist.Alchemist;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.hitbox.Hitbox;
import me.hapyl.fight.util.hitbox.HitboxEntity;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class AlchemicalCauldron extends TickingGameTask {

    private static final DisplayData DISPLAY_CAULDRON;

    private static final ItemStack ITEM_STICK;
    private static final ItemStack ITEM_AIR;

    private static final BlockData[] FX_DATA;

    static {
        DISPLAY_CAULDRON = BDEngine.parse(
                "/summon block_display ~-0.5 ~ ~-0.5 {Passengers:[{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone_slab\",Properties:{type:\"bottom\"}},transformation:[0f,0.7397618705f,0.5111964485f,-0.4748314149f,-0.6503f,0f,0f,0.744375f,0f,-1.0332884471f,0.3659807113f,0.1124484427f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;2118707063,1136582460,317521905,-1313837331],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTVkOTJlODllNDczN2RjZmFlNWU1NzFjNzZmNzYwNGE3YWI3YzllMDNmNzhjMTgyMTcxZDEwOTU3MTQxNTZkMSJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.4878604566f,0f,-0.1091480569f,0.1968813843f,0f,0.6188f,0f,0.590625f,0.3492737821f,0f,0.1524563927f,-0.2790842744f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;1331465730,348953882,1161992372,845440215],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTVkOTJlODllNDczN2RjZmFlNWU1NzFjNzZmNzYwNGE3YWI3YzllMDNmNzhjMTgyMTcxZDEwOTU3MTQxNTZkMSJ9fX0=\"}]}}},item_display:\"none\",transformation:[-0.3492737821f,0f,-0.1524563927f,0.2784044799f,0f,0.6188f,0f,0.583125f,0.4878604566f,0f,-0.1091480569f,0.2512392565f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;822859524,1361126390,39883680,749513230],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTVkOTJlODllNDczN2RjZmFlNWU1NzFjNzZmNzYwNGE3YWI3YzllMDNmNzhjMTgyMTcxZDEwOTU3MTQxNTZkMSJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.3492737821f,0f,0.1524563927f,-0.3364337142f,0f,0.6188f,0f,0.589375f,-0.4878604566f,0f,0.1091480569f,-0.1858670072f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;685371203,-1039806260,-1144213835,-1878570043],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTVkOTJlODllNDczN2RjZmFlNWU1NzFjNzZmNzYwNGE3YWI3YzllMDNmNzhjMTgyMTcxZDEwOTU3MTQxNTZkMSJ9fX0=\"}]}}},item_display:\"none\",transformation:[-0.4878604566f,0f,0.1091480569f,-0.2517171296f,0f,0.6188f,0f,0.5875f,-0.3492737821f,0f,-0.1524563927f,0.3475114996f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_brick_wall\",Properties:{up:\"true\"}},transformation:[0.1935829974f,0.0438836974f,-0.1595522017f,-0.1299979426f,-0.0865267126f,0.3353258097f,-0.0599581618f,0.0384825047f,0.139474697f,0.1471198913f,0.1842528528f,-0.551658481f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone_wall\",Properties:{up:\"true\"}},transformation:[0f,0.0847250993f,-0.7419709457f,0.5995331576f,-0.2625f,0f,0f,0.911875f,0f,0.0606572135f,1.0363740507f,-0.2920050209f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone_wall\",Properties:{up:\"true\"}},transformation:[0f,0.0606572135f,1.0365113915f,-0.7935576107f,-0.2625f,0f,0f,0.911875f,0f,-0.0847250993f,0.742069272f,0.0133981852f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone_wall\",Properties:{up:\"true\"}},transformation:[0f,0.0847250993f,-0.7636027516f,0.0139418859f,-0.2625f,0f,0f,0.911875f,0f,0.0606572135f,1.0665890376f,-0.744195202f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone_wall\",Properties:{up:\"true\"}},transformation:[0f,0.0606572135f,1.0436531157f,-0.3626143965f,-0.2625f,0f,0f,0.911875f,0f,-0.0847250993f,0.7471822444f,-0.585867096f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:sculk\",Properties:{}},transformation:[0.5137170608f,0f,-0.4026544585f,-0.0752861182f,0f,0.0688f,0f,0.724375f,0.3677852925f,0f,0.5624217964f,-0.437041272f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:frogspawn\",Properties:{}},transformation:[0.1056367489f,0f,-0.2547107888f,0.0344172344f,0f,1f,0f,0.78375f,0.2512998951f,0f,0.1070705565f,-0.231672086f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:fire_coral\",Properties:{}},transformation:[0.1618849363f,0f,-0.436211386f,0.1180831717f,0f,0.4063f,0f,0.48375f,0.3363720521f,0f,0.2099343628f,-0.1775668508f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_brick_wall\",Properties:{up:\"true\"}},transformation:[0.139474697f,0.1471198913f,0.1842528528f,-0.6159456836f,-0.0865267126f,0.3353258097f,-0.0599581618f,0.0373636558f,-0.1935829974f,-0.0438836974f,0.1595522017f,0.1363039985f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_brick_wall\",Properties:{up:\"true\"}},transformation:[-0.1935829974f,-0.0438836974f,0.1595522017f,0.0606884797f,-0.0865267126f,0.3353258097f,-0.0599581618f,0.0373636558f,-0.139474697f,-0.1471198913f,-0.1842528528f,0.6275650236f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_brick_wall\",Properties:{up:\"true\"}},transformation:[-0.139474697f,-0.1471198913f,-0.1842528528f,0.5540749056f,-0.0865267126f,0.3353258097f,-0.0599581618f,0.0373636558f,0.1935829974f,0.0438836974f,-0.1595522017f,-0.0659954003f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_brick_fence\",Properties:{}},transformation:[0.2384824532f,0.0858430186f,0.1754255407f,-0.0868496315f,0f,-0.1720508782f,0.2582922868f,0.3916593824f,0.1707366671f,-0.1199042596f,-0.2450318025f,-0.2909771141f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_brick_fence\",Properties:{}},transformation:[0.2376693525f,0.0858430186f,0.1754255407f,0.0717050169f,0f,-0.1720508782f,0.2582922868f,0.3916593824f,0.1701545442f,-0.1199042596f,-0.2450318025f,-0.1774631349f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:stripped_dark_oak_log\",Properties:{axis:\"x\"}},transformation:[0.1245670366f,0.0307989294f,0.0304088617f,0.166824139f,0f,-0.0617287573f,0.0447732661f,0.4056269001f,0.089181239f,-0.0430194894f,-0.0424746486f,-0.3607859653f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:chain\",Properties:{axis:\"x\"}},transformation:[7.1e-9f,0.2394271776f,-0.3049127854f,0.3019962171f,0.1469f,0f,2.24e-8f,0.2200000097f,5.1e-9f,-0.334428343f,-0.2182961138f,-0.0900674071f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_brick_fence\",Properties:{}},transformation:[-0.2384824532f,-0.0858430186f,-0.1754255407f,0.0289986272f,0f,-0.1720508782f,0.2582922868f,0.3916593824f,-0.1707366671f,0.1199042596f,0.2450318025f,0.3653123854f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:nether_brick_fence\",Properties:{}},transformation:[-0.2376693525f,-0.0858430186f,-0.1754255407f,-0.1295560212f,0f,-0.1720508782f,0.2582922868f,0.3916593824f,-0.1701545442f,0.1199042596f,0.2450318025f,0.2517984062f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:stripped_dark_oak_log\",Properties:{axis:\"x\"}},transformation:[-0.1245670366f,-0.0307989294f,-0.0304088617f,-0.2246751433f,0f,-0.0617287573f,0.0447732661f,0.4056269001f,-0.089181239f,0.0430194894f,0.0424746486f,0.4351212366f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:chain\",Properties:{axis:\"x\"}},transformation:[-7.1e-9f,-0.2394271776f,0.3049127854f,-0.3598472214f,0.1469f,0f,2.24e-8f,0.2200000097f,-5.1e-9f,0.334428343f,0.2182961138f,0.1644026784f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone_stairs\",Properties:{facing:\"east\",half:\"bottom\",shape:\"inner_left\"}},transformation:[0.1455450362f,0f,-0.1043746485f,0.3453608607f,0f,0.1006f,0f,0.73125f,0.1042000117f,0f,0.1457889665f,-0.14741755f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone_stairs\",Properties:{facing:\"east\",half:\"bottom\",shape:\"inner_left\"}},transformation:[-0.1042000117f,0f,-0.1457889665f,0.1610754899f,0f,0.1006f,0f,0.729375f,0.1455450362f,0f,-0.1043746485f,0.4170552369f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone_stairs\",Properties:{facing:\"east\",half:\"bottom\",shape:\"inner_left\"}},transformation:[-0.1455450362f,0f,0.1043746485f,-0.4039054849f,0f,0.1006f,0f,0.729375f,-0.1042000117f,0f,-0.1457889665f,0.2324060392f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone_stairs\",Properties:{facing:\"east\",half:\"bottom\",shape:\"inner_left\"}},transformation:[0.1042000117f,0f,0.1457889665f,-0.2228887077f,0f,0.1006f,0f,0.729375f,-0.1455450362f,0f,0.1043746485f,-0.332869506f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:sculk\",Properties:{}},transformation:[-0.0363826856f,0f,-0.0008944108f,0.2460481479f,0f,0.1574f,0f,0.68875f,0.0508187976f,0f,-0.0006403353f,0.3810382199f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:sculk\",Properties:{}},transformation:[-0.0500043631f,0f,-0.0855382001f,0.2824308336f,0f,0.1141f,0f,0.731875f,0.0698453554f,0f,-0.0612393365f,0.3302194223f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:sculk\",Properties:{}},transformation:[-0.0363826856f,-0.0851316497f,0f,0.2460481479f,0f,0f,-0.0011f,0.846875f,0.0508187976f,-0.060948275f,0f,0.3810382199f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:sculk\",Properties:{}},transformation:[-0.0363826856f,-0.0851316497f,0f,0.2817031799f,0f,0f,-0.0011f,0.846875f,0.0508187976f,-0.060948275f,0f,0.3312357983f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:sculk\",Properties:{}},transformation:[-0.0163576555f,-0.0474850844f,0f,0.1979771389f,0f,0f,-0.0011f,0.846875f,0.0228481314f,-0.0339959815f,0f,0.4234889998f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:sculk\",Properties:{}},transformation:[-0.0195593318f,-0.0474850844f,0f,0.2647427515f,0f,0f,-0.0011f,0.846875f,0.0273201856f,-0.0339959815f,0f,0.2937274643f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:sculk\",Properties:{}},transformation:[-0.0122827947f,0f,-0.0008944108f,0.2145395726f,0f,0.0666f,0f,0.759375f,0.0171564261f,0f,-0.0006403353f,0.4261225886f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:cyan_candle\",Properties:{candles:\"1\",lit:\"false\"}},transformation:[0.4158912517f,0f,-0.1548103253f,-0.2736745811f,0f,0.2563f,0f,0.828125f,0.1563232444f,0f,0.4118661957f,-0.6667009774f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:cyan_candle\",Properties:{candles:\"1\",lit:\"false\"}},transformation:[0.4158912517f,0f,-0.1548103253f,-0.2252698056f,0f,0.378f,0f,0.828125f,0.1563232444f,0f,0.4118661957f,-0.7104501288f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:cyan_candle\",Properties:{candles:\"1\",lit:\"false\"}},transformation:[0.4158912517f,0f,-0.1548103253f,-0.209162963f,0f,0.3236f,0f,0.828125f,0.1563232444f,0f,0.4118661957f,-0.6571418909f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:nautilus_shell\",Count:1},item_display:\"none\",transformation:[-0.0431412641f,-0.1923607614f,0.1226551584f,-0.2905124744f,-0.0050316746f,0.0480281841f,0.531186881f,0.7775f,-0.1829139513f,0.0440481667f,-0.0435410098f,0.0514735755f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:armadillo_scute\",Count:1},item_display:\"none\",transformation:[0.286404027f,0.1194639104f,-0.0368423316f,0.1289432833f,-0.0171817081f,0.1288343436f,0.2841878794f,0.79625f,0.1238296095f,-0.2584305245f,0.1246440604f,0.0335481606f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:big_dripleaf_stem\",Properties:{facing:\"east\"}},transformation:[0.1533208084f,0.0170656202f,-0.266223342f,0.120308094f,0.0247070526f,0.2168271241f,0.0340912124f,0.685f,0.3345081931f,-0.0238370061f,0.1195046503f,0.1572699216f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:purple_candle\",Properties:{candles:\"1\",lit:\"false\"}},transformation:[0.4158912517f,0f,-0.1548103253f,0.2451881877f,0f,0.2563f,0f,0.105625f,0.1563232444f,0f,0.4118661957f,0.1476589167f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:purple_candle\",Properties:{candles:\"1\",lit:\"false\"}},transformation:[0.0107899483f,0.0015760094f,0.0221006785f,0.412893483f,-0.4608505598f,0.0000341537f,0.0017942927f,0.31125f,0.004619363f,-0.000273912f,0.1273845773f,0.3149580988f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:purple_candle\",Properties:{candles:\"1\",lit:\"false\"}},transformation:[0.4608505598f,-0.0000341537f,-0.0061058683f,0.133083986f,0.0099235544f,0.0015994949f,0.0057020207f,0.103125f,0.0062677402f,-0.0000212058f,0.4399206807f,0.17003737f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:purple_candle\",Properties:{candles:\"1\",lit:\"false\"}},transformation:[0.4481977018f,-0.0000286561f,-0.1026713773f,0.2215685729f,0.0099235544f,0.0015994949f,0.0057020207f,0.103125f,0.1074306434f,-0.0000281956f,0.4278154687f,0.1371372865f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:purple_candle\",Properties:{candles:\"1\",lit:\"false\"}},transformation:[0.4481977018f,-0.0000286561f,-0.1026713773f,0.2213999865f,0.0099235544f,0.0015994949f,0.0057020207f,0.103125f,0.1074306434f,-0.0000281956f,0.4278154687f,0.1662257625f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:purple_candle\",Properties:{candles:\"1\",lit:\"false\"}},transformation:[0.4601192706f,-0.0000355825f,0.0253629623f,0.1603449469f,0.0099235544f,0.0015994949f,0.0057020207f,0.103125f,-0.0266979376f,-0.0000187097f,0.4392313822f,0.1953053851f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:purple_candle\",Properties:{candles:\"1\",lit:\"false\"}},transformation:[0.3984225154f,-0.0000193452f,-0.2212782303f,0.2803986719f,0.0099235544f,0.0015994949f,0.0057020207f,0.103125f,0.2316938115f,-0.000035241f,0.3802675792f,0.1462030165f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:purple_candle\",Properties:{candles:\"1\",lit:\"false\"}},transformation:[0.0107899483f,0.0015760094f,0.0221006785f,0.4159279732f,-0.4608505598f,0.0000341537f,0.0017942927f,0.31125f,0.004619363f,-0.000273912f,0.1273845773f,0.3300453812f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone\",Properties:{}},transformation:[0.6098255708f,0f,-0.445673346f,-0.116826654f,0f,0.46656367f,0f,0.195625f,0.4365922276f,0f,0.6225099427f,-0.4936845594f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone\",Properties:{}},transformation:[0f,-0.5057486734f,-0.4941059771f,0.4691033065f,0.40382139f,0f,0f,0.23664375f,0f,-0.3620804874f,0.690159926f,-0.1310809276f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone\",Properties:{}},transformation:[0.3581802635f,0f,-0.6785325851f,0.1314116609f,0f,-0.4082186f,0f,0.63698675f,-0.5003008983f,0f,-0.4857816186f,0.5295652825f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:light_gray_stained_glass\",Properties:{}},transformation:[0.0764818227f,0f,-0.0471293705f,0.2717753647f,0f,0.1852f,0f,0f,0.0468681213f,0f,0.0769081428f,0.2860693573f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:light_gray_stained_glass\",Properties:{}},transformation:[0.079732232f,0f,-0.0755793053f,0.354625735f,0f,0.1753f,0f,0f,0.072550749f,0f,0.083060572f,0.1696304639f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:light_gray_stained_glass\",Properties:{}},transformation:[0f,0.031042955f,-0.1097378103f,0.4226530515f,-0.115f,0f,0f,0.110625f,0f,0.1725294901f,0.0197449485f,0.3456076992f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:fire_coral_block\",Properties:{}},transformation:[0.0711101897f,0f,-0.0427926325f,0.2718273724f,0f,0.1289f,0f,0f,0.0435763803f,0f,0.0698312295f,0.2919653662f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:bubble_coral_block\",Properties:{}},transformation:[0.0708566589f,0f,-0.0651476113f,0.3532762967f,0f,0.0997f,0f,0f,0.0644745988f,0f,0.07159629f,0.1785427597f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_slab\",Properties:{type:\"bottom\"}},transformation:[0.0605374517f,0f,-0.0375676468f,0.2743375421f,0f,0.0494f,0f,0.17625f,0.0370973981f,0f,0.0613048278f,0.2986347213f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_slab\",Properties:{type:\"bottom\"}},transformation:[0.0672324665f,0f,-0.0638688875f,0.3542841088f,0f,0.0494f,0f,0.1675f,0.0611768375f,0f,0.0701909909f,0.1811498299f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:tube_coral_block\",Properties:{}},transformation:[0f,0.0289002296f,-0.1097378103f,0.4234277972f,-0.006f,0f,0f,0.01125f,0f,0.1606207232f,0.0197449485f,0.349913555f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_slab\",Properties:{type:\"bottom\"}},transformation:[0.0672324665f,0f,-0.0638688875f,0.2147560878f,0f,0.0494f,0f,0f,0.0611768375f,0f,0.0701909909f,0.3907153662f,0f,0f,0f,1f]}]}"
        );

        ITEM_STICK = new ItemStack(Material.STICK);
        ITEM_AIR = new ItemStack(Material.AIR);

        FX_DATA = new BlockData[] { Material.POLISHED_BLACKSTONE.createBlockData(), Material.SCULK_VEIN.createBlockData() };
    }

    private final GamePlayer player;
    private final Location location;
    private final CauldronAbility ability;
    private final DisplayEntity entity;
    private final LivingGameEntity hitBoxEntity;
    private final Hologram hologram;
    private final ArmorStand animation;

    private boolean status;
    private int timeLeft;

    public AlchemicalCauldron(@Nonnull GamePlayer player, @Nonnull Location location, @Nonnull CauldronAbility ability) {
        this.player = player;
        this.location = location;
        this.ability = ability;

        this.entity = DISPLAY_CAULDRON.spawnInterpolated(location);
        this.entity.setTeleportDuration(5);

        this.hitBoxEntity = Hitbox.create(
                location, "Cauldron", ability.health, new Hitbox() {
                    @Override
                    public void onSpawn(@Nonnull HitboxEntity entity) {
                        player.getTeam().addEntry(entity.getEntry());
                    }

                    @Override
                    public void onDamageTaken(@Nonnull DamageInstance instance) {
                        player.playWorldSound(location, Sound.ENTITY_IRON_GOLEM_STEP, 0.0f);
                        player.playWorldSound(location, Sound.BLOCK_METAL_BREAK, 0.75f);
                    }

                    @Override
                    public void onDeath() {
                        cancel();

                        // Fx
                        final Location fxLocation = location.add(0, 1, 0);

                        player.playWorldSound(fxLocation, Sound.ENTITY_IRON_GOLEM_DAMAGE, 0.75f);
                        player.spawnWorldParticle(fxLocation, Particle.BLOCK, 10, 0.3d, 0.3d, 0.3d, 1f, FX_DATA[0]);
                        player.spawnWorldParticle(fxLocation, Particle.BLOCK, 10, 0.3d, 0.3d, 0.3d, 1f, FX_DATA[1]);
                    }

                    @Override
                    public void onInteract(@Nonnull GamePlayer player) {
                        if (!AlchemicalCauldron.this.player.equals(player)) {
                            player.playSound(Sound.BLOCK_LAVA_POP, 0.0f);
                            return;
                        }

                        setStatus(!status);
                    }
                }, 2
        );

        this.hologram = new Hologram().create(location.clone().add(0, 0.75d, 0));

        for (GamePlayer teamMates : player.getTeam().getPlayers()) {
            this.hologram.show(teamMates.getPlayer());
        }

        this.animation = Entities.ARMOR_STAND.spawn(
                location.clone().add(0, -0.75d, 0), self -> {
                    self.setMarker(true);
                    self.setSmall(true);
                    self.setInvisible(true);
                    self.setCustomNameVisible(true);

                    final EntityEquipment equipment = self.getEquipment();

                    equipment.setItemInMainHand(ItemBuilder.of(Material.AIR).toItemStack());
                    self.setRightArmPose(new EulerAngle(Math.toRadians(-85.0d), Math.toRadians(-90), 0));

                    self.setSmall(false);
                    self.setCustomNameVisible(false);
                }
        );

        this.status = false;
        this.timeLeft = ability.getDuration();

        // Spawn fx
        player.spawnWorldParticle(location, Particle.EXPLOSION_EMITTER, 1);

        player.playWorldSound(location, Sound.ENTITY_WITCH_HURT, 0.75f);
        player.playWorldSound(location, Sound.ENTITY_BREEZE_SHOOT, 0.5f);

        runTaskTimer(0, 1);
    }

    @Override
    public void onTaskStop() {
        this.entity.remove();
        this.hologram.destroy();
        this.animation.remove();
        this.hitBoxEntity.forceRemove();

        // Return the weapon
        if (status) {
            HeroRegistry.ALCHEMIST.getWeapon().give(player);
        }
    }

    @Override
    public void run(final int tick) {
        animate();

        // Update hologram
        final boolean isDarkRed = tick % 40 <= 20;

        this.hologram.setLinesAndUpdate(
                "&a%s's Cauldron &8| &c%.0f ❤".formatted(player.getName(), hitBoxEntity.getHealth()),
                "&b%ss".formatted(Tick.round(timeLeft)),
                "",
                !status ? (isDarkRed ? "&4&l" : "&c&l") + "THE STICK IS MISSING!" : ""
        );

        // Don't count down if not active
        if (!status) {
            return;
        }

        if (timeLeft-- < 0) {
            cancel();
            return;
        }

        // Affect
        if (timeLeft == 0) {
            throwAlchemicalMadness();
        }
        else if (timeLeft != ability.getDuration() && timeLeft % ability.interval == 0) {
            throwPotion();
        }

        // Animate cauldron
        if (modulo(5)) {
            final Location location = entity.getLocation();

            location.setYaw(player.random.nextFloat(0, 3));
            location.setPitch(player.random.nextFloat(0, 1));

            entity.teleport(location);
        }

        if (modulo(100)) {
            player.getWorld().playSound(location, Sound.BLOCK_LAVA_AMBIENT, SoundCategory.RECORDS, 100, 2.0f);
        }
    }

    public void setStatus(boolean status) {
        final Alchemist alchemist = HeroRegistry.ALCHEMIST;

        if (status) {
            this.status = true;
            this.animation.getEquipment().setItemInMainHand(ITEM_STICK);

            alchemist.stickMissing.give(player);
        }
        else {
            this.status = false;
            this.animation.getEquipment().setItemInMainHand(ITEM_AIR);

            alchemist.getWeapon().give(player);
        }

        player.playSound(Sound.ENTITY_CHICKEN_EGG, 1.0f);
        player.playSound(Sound.ENTITY_CHICKEN_EGG, 0.75f);
    }

    private void throwAlchemicalMadness() {
        final Alchemist alchemist = HeroRegistry.ALCHEMIST;
        final AlchemistEffect selfEffect = alchemist.randomAlchemicalEffect(true);

        selfEffect.applyMadness(player, player);

        Collect.enemyPlayers(player).forEach(enemy -> {
            alchemist.randomAlchemicalEffect(false).applyMadness(enemy, player);
        });

        // Fx
        player.spawnWorldParticle(location, Particle.EXPLOSION_EMITTER, 1);
        player.playWorldSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1.25f);
        player.playWorldSound(location, Sound.ENTITY_WITCH_CELEBRATE, 0.75f);
    }

    private void throwPotion() {
        // Pick random potion
        final CauldronPotion potion = ability.pickRandomPotion();

        final Location location = entity.getLocation().add(0, 1, 0);
        final Location destination = LocationHelper.anchor(location.clone().add(player.random.nextDoubleBool(ability.cauldronRadius), 0, player.random.nextDoubleBool(ability.cauldronRadius)));

        final double distance = location.distance(destination);
        final double speed = 0.5d * (distance * 0.05d);
        final Vector vector = destination.toVector().subtract(location.toVector()).normalize().multiply(speed);

        new TickingGameTask() {
            private double d = 0.0d;

            @Override
            public void run(int tick) {
                for (double i = 0; i < distance * 0.1d; i += speed) {
                    if (next()) {
                        cancel();
                        return;
                    }
                }
            }

            private boolean next() {
                if (d >= distance) {
                    Collect.nearbyEntities(destination, ability.splashRadius, player::isNotSelfOrTeammate).forEach(entity -> potion.onHit(entity, player));
                    AlchemistEffect.splashPotionFx(destination.add(0, 0.5, 0), potion.color());
                    return true;
                }

                final double y = Math.sin(d * Math.PI / distance) * (Math.PI / 2);
                location.add(vector);

                LocationHelper.offset(location, 0, y, 0, () -> potion.onTick(location));

                d += speed;
                return false;
            }
        }.runTaskTimer(0, 1);

        // Fx
        player.playWorldSound(location, Sound.ENTITY_WITCH_THROW, 0.75f);
        player.playWorldSound(location, Sound.ENTITY_SPLASH_POTION_THROW, 0.75f);
    }

    private void animate() {
        final Location standLocation = animation.getLocation();
        standLocation.setYaw(standLocation.getYaw() + 10);

        animation.teleport(standLocation);
    }

}

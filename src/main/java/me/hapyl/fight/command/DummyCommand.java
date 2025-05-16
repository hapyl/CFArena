package me.hapyl.fight.command;

import me.hapyl.eterna.module.block.display.BDEngine;
import me.hapyl.eterna.module.block.display.DisplayData;
import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.eterna.module.block.display.animation.AnimationFrame;
import me.hapyl.eterna.module.block.display.animation.DisplayEntityAnimation;
import me.hapyl.eterna.module.command.SimplePlayerAdminCommand;
import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.util.collection.Cache;
import me.hapyl.fight.CF;
import me.hapyl.fight.Message;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.BaseAttributes;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.garbage.SynchronizedGarbageEntityCollector;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;

import javax.annotation.Nonnull;

public class DummyCommand extends SimplePlayerAdminCommand {
    
    private final DisplayData data;
    
    public DummyCommand(@Nonnull String name) {
        super(name);
        
        this.data = BDEngine.parse(
                "/summon block_display ~-0.5 ~ ~-0.5 {Passengers:[{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-1775407524,-1114428291,-151176402,2052143247],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTMzOWQ2OTViYjM3YjU5MzdhOTU5NjNkYWIyYmJjMmE2ZTFjZDhhMmEyNTY4MWUyOTkxNTQ5YzYxYzFkMzNkYyJ9fX0=\"}]}}},item_display:\"none\",transformation:[-0.9987771633f,0.0467293991f,0.0161412957f,0.0298f,0.0478690534f,0.9956878642f,0.0794621339f,2.1254f,-0.0123584744f,0.0801376333f,-0.9967071926f,0.0544f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:hay_block\",Properties:{axis:\"x\"}},transformation:[0.0387137003f,0.7242334136f,0.0040637537f,-0.3959f,0.7944327712f,-0.0353986103f,0.0466966376f,0.7848f,0.0492689708f,0.0017061596f,-0.7561485766f,0.3526f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{}},transformation:[-1f,0f,0f,0.5f,0f,1f,0f,0.1408f,0f,0f,-1f,0.5f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{}},transformation:[-0.7254302363f,0.3658903202f,-0.0056121114f,-0.2724f,0.3659324561f,0.7253154707f,-0.012928865f,0.594f,-0.000812302f,-0.0140709456f,-0.8123777438f,0.4449f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{}},transformation:[-0.7687185629f,-0.2630953145f,0.0029793501f,0.935f,-0.2631054702f,0.7685839438f,-0.0145080275f,0.7246f,0.0018795242f,-0.0146910441f,-0.8123649984f,0.4576f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:stone_sword\",Count:1},item_display:\"none\",transformation:[0.2806031252f,-0.1365363855f,-0.915528421f,-0.733f,0.2558329752f,-0.6866401974f,0.3033480618f,0.5198f,-0.6786963281f,-0.3152775765f,-0.2641736178f,0.1822f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:horn_coral_wall_fan\",Properties:{facing:\"east\"}},transformation:[0.005766184f,0.0200671472f,-0.6249662065f,0.2938f,-0.8748394493f,0.0186022899f,-0.0038803604f,0.9458f,0.0161722391f,0.9996343853f,0.0126237385f,-0.5336f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:horn_coral_wall_fan\",Properties:{facing:\"east\"}},transformation:[-0.0161824318f,-0.9996249573f,-0.0126229741f,0.5085f,-0.8748313388f,0.0186231904f,-0.0038856176f,0.9458f,0.0057669365f,0.0200778872f,-0.6248604344f,0.2801f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:horn_coral_wall_fan\",Properties:{facing:\"east\"}},transformation:[-0.0072943497f,-0.6926588813f,-0.4507620456f,0.5697f,-0.8748329427f,0.0185819312f,-0.0039305467f,0.9458f,0.0155389303f,0.7210280268f,-0.4329243579f,-0.1712f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:horn_coral_wall_fan\",Properties:{facing:\"east\"}},transformation:[-0.0155607545f,-0.7210103423f,0.4329344779f,0.1461f,-0.8748302647f,0.0186392744f,-0.0039167525f,0.9458f,-0.0073434153f,-0.6926726719f,-0.450751883f,0.556f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_andesite_slab\",Properties:{type:\"bottom\"}},transformation:[-0.5625f,0f,0f,0.2819f,0f,0.375f,0f,0f,0f,0f,-0.5625f,0.2762f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:horn_coral_wall_fan\",Properties:{facing:\"east\"}},transformation:[-0.7876094041f,-0.2564412426f,0.2200965817f,0.3989f,-0.1162654835f,0.931475145f,0.2116696382f,0.4491f,-0.3630134486f,0.2580541479f,-0.5453241779f,0.3401f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:horn_coral_wall_fan\",Properties:{facing:\"east\"}},transformation:[-0.8511428391f,-0.0570336343f,-0.1404991382f,0.195f,-0.1162751328f,0.9314655573f,0.2116834147f,0.4491f,0.166315847f,0.359331435f,-0.5710298802f,0.1846f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:feather\",Count:1},item_display:\"none\",transformation:[-0.0204226488f,0.0241090906f,0.625806317f,0.045f,-0.5290826509f,-0.060829648f,-0.0207140977f,2.245f,0.0594044329f,-0.5335007835f,0.0306406545f,-0.1325f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:horn_coral_wall_fan\",Properties:{facing:\"east\"}},transformation:[-0.6951938469f,-0.4904281935f,-0.2237791338f,0.5172f,-0.504652277f,0.793987636f,0.1201179208f,0.9203f,0.1663027191f,0.3592545347f,-0.5709593632f,0.1846f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:horn_coral_wall_fan\",Properties:{facing:\"east\"}},transformation:[-0.8083852001f,-0.2494313038f,-0.1813830406f,0.6402f,-0.2906595226f,0.8992794854f,0.1777962061f,0.8937f,0.1662991589f,0.3592776529f,-0.5709542115f,0.1083f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:stripped_dark_oak_wood\",Properties:{axis:\"x\"}},transformation:[-0.5660721875f,0.0037937442f,0.0032090603f,0.2918f,0.026631477f,0.080967361f,0.0407392868f,1.509f,-0.0013903282f,0.0062915788f,-0.526215595f,0.28f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:horn_coral_wall_fan\",Properties:{facing:\"east\"}},transformation:[-0.0938601952f,0.15961696f,-0.6133331968f,0.0863f,-0.5046357688f,0.7940085173f,0.1201181811f,0.9203f,0.7086304569f,0.5865770498f,0.0043018437f,-0.7316f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:horn_coral_wall_fan\",Properties:{facing:\"east\"}},transformation:[-0.7665659158f,-0.1793640284f,-0.2797324038f,0.6635f,-0.2508224368f,0.9401322422f,0.1152116414f,0.7228f,0.3392488791f,0.2897928788f,-0.5469013256f,-0.1229f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:horn_coral_wall_fan\",Properties:{facing:\"east\"}},transformation:[0.5354254785f,0.5195041244f,0.3727409576f,-0.7857f,-0.6490098287f,0.6387146248f,0.1279142831f,1.265f,-0.2402723446f,-0.5675906033f,0.4851052617f,0.0795f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:arrow\",Count:1},item_display:\"none\",transformation:[-0.1374443694f,-0.4567128179f,-0.3432111504f,0.1544f,0.4633731181f,0.072785544f,-0.253038312f,1.4181f,0.221103251f,-0.4364452711f,0.316950164f,0.5088f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:hay_block\",Properties:{axis:\"x\"}},transformation:[-0.1586967404f,-0.2107888806f,-0.0046857566f,-0.1285f,-0.195213268f,0.1714062079f,-0.0007733314f,1.3998f,0.0031120032f,0.0029748462f,-0.2874607724f,0.165f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:hay_block\",Properties:{axis:\"x\"}},transformation:[0.1257868915f,-0.2353055599f,-0.0009505103f,0.3547f,-0.2178768927f,-0.1358057646f,-0.0046953457f,1.5668f,0.0031428558f,0.0029962956f,-0.2874600846f,0.1721f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:barrel\",Properties:{facing:\"down\",open:\"false\"}},transformation:[-0.3801960519f,0.1489173115f,-0.0364311154f,0.7891f,-0.05658204f,0.9896574488f,0.0127827231f,0.2349f,0.4523388054f,0.2489607567f,-0.0290218163f,-0.1687f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:infested_stone\",Properties:{}},transformation:[-0.4264841114f,0.1620600594f,-0.0312248736f,0.8f,-0.0631520733f,1.0810633754f,0.0109182711f,0.1803f,0.5072273734f,0.2708596602f,-0.0248949518f,-0.2188f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone_slab\",Properties:{type:\"bottom\"}},transformation:[0.0658158665f,0.237655086f,-0.1102098482f,0.5366f,0.4169306489f,0.0310909065f,0.0385656599f,0.4636f,0.1004972874f,-0.2846189677f,-0.0878147459f,0.1895f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:arrow\",Count:1},item_display:\"none\",transformation:[-0.1374391181f,0.2417307862f,-0.4718185395f,-0.3482f,0.4633746984f,0.3101527686f,-0.0239512117f,1.2136f,0.2211020484f,-0.4997397231f,-0.2430903556f,0.5003f,0f,0f,0f,1f]}]}"
        );
    }
    
    @Override
    protected void execute(Player player, String[] args) {
        final Location location = player.getLocation();
        location.setYaw(0.0f);
        location.setPitch(0.0f);
        
        CF.createEntity(
                location, Entities.SLIME, self -> {
                    self.setAI(false);
                    self.setSilent(true);
                    self.setSize(4);
                    self.setInvisible(true);
                    self.setNoPhysics(true);
                    
                    return new DummyEntity(self);
                }
        );
        
        Message.success(player, "Spawned a new dummy!");
    }
    
    private class DummyEntity extends LivingGameEntity {
        
        private static final double TEXT_OFFSET = 2.5;
        
        private final Cache<Double> damageCache;
        private final DisplayEntity display;
        private final TextDisplay text;
        
        private DisplayEntityAnimation animation;
        
        public DummyEntity(@Nonnull LivingEntity entity) {
            super(
                    entity, new BaseAttributes()
                            .put(AttributeType.MAX_HEALTH, Double.MAX_VALUE)
            );
            
            // Always valid
            setValidState(true);
            
            this.damageCache = Cache.ofList(1000);
            this.text = Entities.TEXT_DISPLAY.spawn(
                    getLocation().add(0, TEXT_OFFSET, 0), self -> {
                        self.setBillboard(Display.Billboard.VERTICAL);
                        self.setDefaultBackground(false);
                    }
            );
            
            this.display = data.spawn(
                    getLocation(), self -> {
                        self.setTeleportDuration(2);
                    }
            );
            
            // Mark as garbage entity so reload can remove them
            SynchronizedGarbageEntityCollector.add(entity);
            SynchronizedGarbageEntityCollector.add(display);
            SynchronizedGarbageEntityCollector.add(text);
        }
        
        @Nonnull
        @Override
        public String getName() {
            return "Dummy";
        }
        
        @Override
        public void tick() {
            super.tick();
            
            final double dps = damageCache.stream()
                                          .mapToDouble(Double::doubleValue)
                                          .sum();
            
            // Update text
            text.text(
                    Component.text()
                             .append(Component.text("Dummy", Color.SKY_BLUE, TextDecoration.BOLD))
                             .appendNewline()
                             .append(
                                     Component.text("ᴅᴘꜱ ", NamedTextColor.AQUA, TextDecoration.BOLD),
                                     Component.text("%.1f".formatted(dps), NamedTextColor.DARK_AQUA)
                             )
                             .build()
            );
            
            // Sync data lol
            final Location displayLocation = display.getLocation();
            final Location location = getLocation();
            
            if (location.distanceSquared(displayLocation) >= 1) {
                display.teleport(location);
                text.teleport(location.add(0, TEXT_OFFSET, 0));
            }
        }
        
        @Override
        public void onDeath() {
            super.onDeath();
            
            display.remove();
            text.remove();
        }
        
        @Override
        public void onDamageTaken(@Nonnull DamageInstance instance) {
            cancelAnimation();
            
            damageCache.add(instance.getDamage());
            
            //instance.setOverrideDamageTicks(0);
            
            animation = display.newAnimation(CF.getPlugin())
                               .addFrame(new AnimationFrame(Math.PI * 2, Math.PI / 4, 2) {
                                   @Override
                                   public void tick(@Nonnull DisplayEntity entity, double theta) {
                                       final float pitch = (float) (Math.cos(theta) * 4);
                                       
                                       entity.setRotation(0, pitch);
                                   }
                               })
                               .addFrame(new AnimationFrame() {
                                   @Override
                                   public void tick(@Nonnull DisplayEntity entity, double theta) {
                                       entity.setRotation(0, 0);
                                   }
                               }).start();
        }
        
        private void cancelAnimation() {
            if (animation != null) {
                animation.cancel();
                animation = null;
            }
        }
        
    }
    
}

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
import me.hapyl.fight.Notifier;
import me.hapyl.fight.event.DamageInstance;
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
                "/summon block_display ~-0.5 ~-0.5 ~-0.5 {Passengers:[{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-112185605,1447459445,769677829,553711541],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTMzOWQ2OTViYjM3YjU5MzdhOTU5NjNkYWIyYmJjMmE2ZTFjZDhhMmEyNTY4MWUyOTkxNTQ5YzYxYzFkMzNkYyJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.9988f,-0.0467f,-0.0161f,-0.0298f,0.0479f,0.9957f,0.0795f,2.1254f,0.0124f,-0.0801f,0.9967f,-0.0544f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:hay_block\",Properties:{axis:\"x\"}},transformation:[-0.0387f,-0.7242f,-0.004f,0.3959f,0.7944f,-0.0354f,0.0467f,0.7848f,-0.0492f,-0.0017f,0.7561f,-0.3526f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{}},transformation:[1f,0f,0f,-0.5f,0f,1f,0f,0.1408f,0f,0f,1f,-0.5f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{}},transformation:[0.7254f,-0.3659f,0.0056f,0.2724f,0.3659f,0.7253f,-0.0129f,0.594f,0.0008f,0.0141f,0.8124f,-0.4449f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:spruce_fence\",Properties:{}},transformation:[0.7687f,0.2631f,-0.003f,-0.935f,-0.2631f,0.7686f,-0.0145f,0.7246f,-0.0019f,0.0147f,0.8124f,-0.4576f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:stone_sword\",Count:1},item_display:\"none\",transformation:[-0.2806f,0.1365f,0.9155f,0.733f,0.2558f,-0.6866f,0.3033f,0.5198f,0.6787f,0.3153f,0.2642f,-0.1822f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:horn_coral_wall_fan\",Properties:{facing:\"east\"}},transformation:[-0.0058f,-0.0201f,0.6249f,-0.2938f,-0.8748f,0.0186f,-0.0039f,0.9458f,-0.0162f,-0.9996f,-0.0126f,0.5336f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:horn_coral_wall_fan\",Properties:{facing:\"east\"}},transformation:[0.0162f,0.9996f,0.0126f,-0.5085f,-0.8748f,0.0186f,-0.0039f,0.9458f,-0.0058f,-0.0201f,0.6249f,-0.2801f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:horn_coral_wall_fan\",Properties:{facing:\"east\"}},transformation:[0.0074f,0.6927f,0.4508f,-0.5697f,-0.8748f,0.0186f,-0.0039f,0.9458f,-0.0155f,-0.721f,0.4329f,0.1712f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:horn_coral_wall_fan\",Properties:{facing:\"east\"}},transformation:[0.0155f,0.721f,-0.4329f,-0.1461f,-0.8748f,0.0186f,-0.0039f,0.9458f,0.0074f,0.6927f,0.4508f,-0.556f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_andesite_slab\",Properties:{type:\"bottom\"}},transformation:[0.5625f,0f,0f,-0.2819f,0f,0.375f,0f,0f,0f,0f,0.5625f,-0.2762f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:horn_coral_wall_fan\",Properties:{facing:\"east\"}},transformation:[0.7876f,0.2564f,-0.2201f,-0.3989f,-0.1163f,0.9315f,0.2117f,0.4491f,0.363f,-0.258f,0.5453f,-0.3401f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:horn_coral_wall_fan\",Properties:{facing:\"east\"}},transformation:[0.8511f,0.057f,0.1405f,-0.195f,-0.1163f,0.9315f,0.2117f,0.4491f,-0.1663f,-0.3593f,0.571f,-0.1846f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:feather\",Count:1},item_display:\"none\",transformation:[0.0204f,-0.0241f,-0.6258f,-0.045f,-0.5291f,-0.0608f,-0.0207f,2.245f,-0.0594f,0.5335f,-0.0307f,0.1325f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:horn_coral_wall_fan\",Properties:{facing:\"east\"}},transformation:[0.6952f,0.4904f,0.2238f,-0.5172f,-0.5047f,0.794f,0.1201f,0.9203f,-0.1663f,-0.3593f,0.571f,-0.1846f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:horn_coral_wall_fan\",Properties:{facing:\"east\"}},transformation:[0.8084f,0.2494f,0.1814f,-0.6402f,-0.2907f,0.8993f,0.1778f,0.8937f,-0.1663f,-0.3593f,0.571f,-0.1083f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:stripped_dark_oak_wood\",Properties:{axis:\"x\"}},transformation:[0.5661f,-0.0038f,-0.0032f,-0.2918f,0.0266f,0.081f,0.0407f,1.509f,0.0014f,-0.0063f,0.5262f,-0.28f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:horn_coral_wall_fan\",Properties:{facing:\"east\"}},transformation:[0.0938f,-0.1597f,0.6133f,-0.0863f,-0.5047f,0.794f,0.1201f,0.9203f,-0.7086f,-0.5866f,-0.0043f,0.7316f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:horn_coral_wall_fan\",Properties:{facing:\"east\"}},transformation:[0.7666f,0.1794f,0.2797f,-0.6635f,-0.2508f,0.9401f,0.1152f,0.7228f,-0.3393f,-0.2898f,0.5469f,0.1229f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:horn_coral_wall_fan\",Properties:{facing:\"east\"}},transformation:[-0.5354f,-0.5195f,-0.3727f,0.7857f,-0.649f,0.6387f,0.1279f,1.265f,0.2403f,0.5676f,-0.4851f,-0.0795f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:arrow\",Count:1},item_display:\"none\",transformation:[0.1374f,0.4567f,0.3432f,-0.1544f,0.4634f,0.0728f,-0.253f,1.4181f,-0.2211f,0.4365f,-0.317f,-0.5088f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:hay_block\",Properties:{axis:\"x\"}},transformation:[0.1587f,0.2108f,0.0047f,0.1285f,-0.1952f,0.1714f,-0.0008f,1.3998f,-0.0031f,-0.003f,0.2875f,-0.165f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:hay_block\",Properties:{axis:\"x\"}},transformation:[-0.1258f,0.2353f,0.001f,-0.3547f,-0.2179f,-0.1358f,-0.0047f,1.5668f,-0.0031f,-0.003f,0.2875f,-0.1721f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:barrel\",Properties:{facing:\"down\",open:\"false\"}},transformation:[0.3801f,-0.1486f,0.0364f,-0.7891f,-0.0564f,0.9898f,0.0128f,0.2349f,-0.4524f,-0.2484f,0.029f,0.1687f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:infested_stone\",Properties:{}},transformation:[0.4263f,-0.1623f,0.0312f,-0.8f,-0.0633f,1.0809f,0.0109f,0.1803f,-0.5074f,-0.2712f,0.0249f,0.2188f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:polished_blackstone_slab\",Properties:{type:\"bottom\"}},transformation:[-0.0659f,-0.2376f,0.1102f,-0.5366f,0.417f,0.031f,0.0386f,0.4636f,-0.1005f,0.2847f,0.0878f,-0.1895f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:arrow\",Count:1},item_display:\"none\",transformation:[0.1374f,-0.2417f,0.4718f,0.3482f,0.4634f,0.3101f,-0.0239f,1.2136f,-0.2211f,0.4998f,0.2431f,-0.5003f,0f,0f,0f,1f]}]}"
        );
    }

    @Override
    protected void execute(Player player, String[] args) {
        final Location location = player.getLocation();
        location.setYaw(0.0f);
        location.setPitch(0.0f);

        CF.createEntity(location, Entities.SLIME, self -> {
            self.setAI(false);
            self.setSilent(true);
            self.setSize(4);
            self.setInvisible(true);
            self.setNoPhysics(true);

            return new DummyEntity(self);
        });

        Notifier.success(player, "Spawned a new dummy!");
    }

    private class DummyEntity extends LivingGameEntity {

        private static final double TEXT_OFFSET = 2.5;

        private final Cache<Double> damageCache;
        private final DisplayEntity display;
        private final TextDisplay text;

        private DisplayEntityAnimation animation;

        public DummyEntity(@Nonnull LivingEntity entity) {
            super(entity);

            this.health = Double.MAX_VALUE;
            setValidState(true);

            this.damageCache = Cache.ofList(1000);
            this.text = Entities.TEXT_DISPLAY.spawn(getLocation().add(0, TEXT_OFFSET, 0), self -> {
                self.setBillboard(Display.Billboard.VERTICAL);
                self.setDefaultBackground(false);
            });

            this.display = data.spawn(getLocation(), self -> {
                self.setTeleportDuration(2);
            });

            // Mark as garbage entity so reload can remove them
            SynchronizedGarbageEntityCollector.add(entity);
            SynchronizedGarbageEntityCollector.add(display);
            SynchronizedGarbageEntityCollector.add(text);
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

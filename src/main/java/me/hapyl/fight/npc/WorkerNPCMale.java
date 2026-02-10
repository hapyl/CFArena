package me.hapyl.fight.npc;

import me.hapyl.eterna.module.inventory.Equipment;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.npc.ClickType;
import me.hapyl.eterna.module.npc.NpcAnimation;
import me.hapyl.eterna.module.npc.NpcPose;
import me.hapyl.eterna.module.npc.NpcProperties;
import me.hapyl.eterna.module.npc.appearance.AppearanceHumanoid;
import me.hapyl.eterna.module.reflect.Skin;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.util.ComponentUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class WorkerNPCMale extends PersistentNPC {
    
    public WorkerNPCMale(@Nonnull Key key) {
        super(
                key, -1.5d, 62.0d, -12.5d, Component.text("Worker Joel", Color.DEFAULT), Skin.of(
                        "ewogICJ0aW1lc3RhbXAiIDogMTc0MDkyODMxMTE5NCwKICAicHJvZmlsZUlkIiA6ICI2NDU4Mjc0MjEyNDg0MDY0YTRkMDBlNDdjZWM4ZjcyZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaDNtMXMiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWIzMDEzMTVlNWY2OTAzZjgyOGVjYmNkNmRmNTlmNzQ2NDlmOTlkOWNhNDQ0Y2VhZTA0NWM5NTIwMDk5Yjg4ZCIKICAgIH0KICB9Cn0=",
                        "vJ5mquF/L+yCwwehkXzuyBcdkwvHLfe5uRKiJi1VeXYqBmHzGq4i2SOy4SpJy/FPC0z4QxbLG05KgIYXewXeA/qXLRl+ZybH3GfKPtT+m+MumMS3r6GO8RSbJlzO6LfOujpiSyS4G5FMmeAUrcNFpOcvDqzECzfcSCVs6AO+J+svfGB8ghU92EgKP0hCBvhdavTRknZKfehlEryojIaBeQM/3etvNA1wWYcvcJZpGuRB4DZJdSLz7vqpvwLkJ8SmBN7DSBwLmN5RzlIbcglnunS2QKPoGkkUTLrQB0/5+palEKIyADICzE7U6b9OPRQ9DTucxeBTNQZrWjnUlok6XGX2P9MMnRO9riPY2bNtBWTdLijmjVg6Kcqct8RUiA2ugEpGWsXifXs4q5yNTwy4wxLBuWM1BAXLfT0y+Obhw6GHPXVEeG0jKFglE0CkPG8XILJWpVMhz2z1d1tUUw4mVQHUdTgV2Zs2rs6udK1gsQeXBcdIdKe7JLSUFEcG8pYJFzCj7efMM9ZCiqHkw92rQnC3+nFmXFjcmslQ9QB2Yk+Im1KUFlrblEnB9T1lPGxtJiXV5i53nGQl28MfkS4A9IdOHdLymRKnYTZLX6MPVkrKV3wm7xjQ73NAahOp6C7JtwTP2G7//oAIddioQ59XtazQwGKB+1pXud30vuiVEoE="
                )
        );
        
        final NpcProperties properties = getProperties();
        properties.setInteractionDelay(30);
        properties.setLookAtClosePlayerDistance(0);
        
        getAppearance(AppearanceHumanoid.class).setEquipment(Equipment.builder()
                                                                      .helmet(Material.GOLDEN_HELMET)
                                                                      .mainHand(ItemBuilder.playerHeadUrl("f620519b74536c1f85b7c7e5e11ce5c059c2ff759cb8df254fc7f9ce781d29").asIcon())
                                                                      .build()
        );
        
        sound = new PersistentNPCSound(0.75f);
    }
    
    @Override
    public void onClick(@Nonnull Player player, @Nonnull ClickType clickType) {
        sendMessage(
                player,
                ComponentUtils.random(
                        Component.text("Work, work, work..."),
                        Component.text("Just need to move these..."),
                        Component.text("This goes here...")
                )
        );
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // Update head pose every 2s
        if (tick % 40 == 0) {
            final float newYaw = BukkitUtils.RANDOM.nextFloat(135f, 175f);
            final float newPitch = BukkitUtils.RANDOM.nextFloat(-15f, 25f);
            
            setHeadRotation(newYaw, newPitch);
            
            if (BukkitUtils.RANDOM.nextBoolean()) {
                playAnimation(NpcAnimation.SWING_MAIN_HAND);
            }
            else {
                playAnimation(NpcAnimation.SWING_OFF_HAND);
            }
        }
        
        setPose(NpcPose.CROUCHING);
    }
}

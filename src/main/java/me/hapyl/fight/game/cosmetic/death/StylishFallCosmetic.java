package me.hapyl.fight.game.cosmetic.death;

import me.hapyl.eterna.module.npc.Npc;
import me.hapyl.eterna.module.npc.NpcAnimation;
import me.hapyl.eterna.module.npc.appearance.Appearance;
import me.hapyl.eterna.module.npc.appearance.AppearanceBuilder;
import me.hapyl.eterna.module.reflect.EntityDataType;
import me.hapyl.eterna.module.reflect.Skin;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.game.task.GameTask;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class StylishFallCosmetic extends Cosmetic {
    public StylishFallCosmetic(@Nonnull Key key) {
        super(key, "Stylish Fall", Type.DEATH);
        
        setDescription("""
                       Fall with a style!
                       """
        );
        
        setRarity(Rarity.LEGENDARY);
        setIcon(Material.PLAYER_HEAD);
    }
    
    @Override
    public void onDisplay(@Nonnull Display display) {
        final Location location = display.getLocation().subtract(0.0d, 0.75d, 0.0d);
        
        location.setPitch(0.0f);
        
        final Player player = display.getPlayer();
        
        final Npc human = new Npc(location, Component.empty(), AppearanceBuilder.ofMannequin(player != null ? Skin.ofPlayer(player) : Skin.of("", "")));
        human.getProperties().setCollidable(false);
        human.showAll();
        
        human.playAnimation(NpcAnimation.TAKE_DAMAGE);
        
        final Appearance appearance = human.getAppearance();
        appearance.setEntityDataValue(EntityDataType.BYTE, 0, (byte) 0x80);
        appearance.updateEntityData();
        
        GameTask.runLater(
                () -> {
                    display.particle(Particle.CLOUD, 16, 0.5d, 0.1d, 0.5d, 0.04f);
                    display.sound(Sound.ENTITY_LLAMA_SPIT, 0.65f);
                    
                    human.destroy();
                }, 60
        ).addCancelEvent(human::destroy);
    }
}

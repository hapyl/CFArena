package me.hapyl.fight.game.cosmetic.death;

import me.hapyl.eterna.module.reflect.npc.HumanNPC;
import me.hapyl.eterna.module.reflect.npc.NPCAnimation;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.game.task.GameTask;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

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

        final HumanNPC human = new HumanNPC(location, "", display.getName());
        human.setCollision(false);
        human.showAll();

        human.playAnimation(NPCAnimation.TAKE_DAMAGE);
        human.setDataWatcherByteValue(0, (byte) 0x80);
        human.updateDataWatcher();

        GameTask.runLater(() -> {
            display.particle(Particle.CLOUD, 16, 0.5d, 0.1d, 0.5d, 0.04f);
            display.sound(Sound.ENTITY_LLAMA_SPIT, 0.65f);
            human.remove();
        }, 60).addCancelEvent(human::remove);
    }
}

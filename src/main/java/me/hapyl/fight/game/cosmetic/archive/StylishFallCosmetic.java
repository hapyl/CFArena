package me.hapyl.fight.game.cosmetic.archive;

import me.hapyl.fight.game.cosmetic.*;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.spigotutils.module.reflect.npc.HumanNPC;
import me.hapyl.spigotutils.module.reflect.npc.NPCAnimation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

public class StylishFallCosmetic extends Cosmetic {
    public StylishFallCosmetic() {
        super("Stylish Fall", "Fall in style.", Type.DEATH, Rarity.LEGENDARY, Material.PLAYER_HEAD);
    }

    @Override
    public void onDisplay(Display display) {
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

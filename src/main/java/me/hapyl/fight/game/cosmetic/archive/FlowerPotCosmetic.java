package me.hapyl.fight.game.cosmetic.archive;

import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.ShutdownAction;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.CollectionUtils;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;

public class FlowerPotCosmetic extends Cosmetic {

    private final BlockData[] flowers = {
            Material.POTTED_POPPY.createBlockData(),
            Material.POTTED_LILY_OF_THE_VALLEY.createBlockData(),
            Material.POTTED_ORANGE_TULIP.createBlockData()
    };

    public FlowerPotCosmetic() {
        super("Lonely Flower", """
                It's just a flower, nothing else but a flower.
                """, Type.DEATH, Rarity.UNCOMMON, Material.FLOWER_POT);
    }

    @Override
    protected void onDisplay(Display display) {
        final Location location = display.getLocation();

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendBlockChange(location, CollectionUtils.randomElement(flowers, flowers[0]));
        });

        // Fx
        PlayerLib.playSound(Sound.ENTITY_CHICKEN_EGG, CFUtils.random(0.5f, 1.0f));
        GameTask.runLater(() -> {
            location.getBlock().getState().update(true, false);

            PlayerLib.spawnParticle(location, Particle.EXPLOSION_NORMAL, 3, 0.15, 0.15, 0.15, 0.05f);
        }, 60).setShutdownAction(ShutdownAction.IGNORE);
    }
}

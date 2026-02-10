package me.hapyl.fight.game.cosmetic.death;

import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.ShutdownAction;
import me.hapyl.fight.util.CFUtils;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;

import javax.annotation.Nonnull;

public class FlowerPotCosmetic extends Cosmetic {

    private final BlockData[] flowers = {
            Material.POTTED_POPPY.createBlockData(),
            Material.POTTED_LILY_OF_THE_VALLEY.createBlockData(),
            Material.POTTED_ORANGE_TULIP.createBlockData()
    };

    public FlowerPotCosmetic(@Nonnull Key key) {
        super(key, "Lonely Flower", Type.DEATH);

        setDescription("""
                It's just a flower, nothing else but a flower.
                """
        );

        setRarity(Rarity.UNCOMMON);
        setIcon(Material.FLOWER_POT);
    }

    @Override
    public void onDisplay(@Nonnull Display display) {
        final Location location = display.getLocation();

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendBlockChange(location, CollectionUtils.randomElement(flowers, flowers[0]));
        });

        // Fx
        PlayerLib.playSound(Sound.ENTITY_CHICKEN_EGG, CFUtils.random(0.5f, 1.0f));
        GameTask.runLater(() -> {
            location.getBlock().getState().update(true, false);

            PlayerLib.spawnParticle(location, Particle.POOF, 3, 0.15, 0.15, 0.15, 0.05f);
        }, 60).setShutdownAction(ShutdownAction.IGNORE);
    }
}

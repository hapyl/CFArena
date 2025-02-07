package me.hapyl.fight.game.cosmetic.death;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.game.task.TickingGameTask;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class EmeraldExplosionCosmetic extends Cosmetic {

    private final Material[] materials;
    private final int itemSpawnCount = 10;

    public EmeraldExplosionCosmetic(@Nonnull Key key) {
        super(key, "Emerald Explosion", Type.DEATH);

        setDescription("""
                Hide the villagers!
                """
        );

        setRarity(Rarity.UNCOMMON);
        setIcon(Material.EMERALD);

        setExclusive(true);

        materials = new Material[] { Material.EMERALD_BLOCK, Material.EMERALD };
    }

    @Override
    public void onDisplay(@Nonnull Display display) {
        for (int i = 0; i < itemSpawnCount; i++) {
            display.dropItem(CollectionUtils.randomElement(materials, materials[0]), 60);
        }

        display.particle(Particle.HAPPY_VILLAGER, 10, 0.5d, 0.25d, 0.5d, 0.0f);
        display.particle(Particle.TOTEM_OF_UNDYING, 20, 0.0d, 0.0d, 0.0d, 0.5f);

        new TickingGameTask() {
            @Override
            public void run(int tick) {
                if (tick > itemSpawnCount) {
                    cancel();
                    return;
                }

                display.sound(Sound.ENTITY_PLAYER_LEVELUP, 1.0f - (0.5f / itemSpawnCount * tick));
            }
        }.runTaskTimer(0, 1);
    }
}

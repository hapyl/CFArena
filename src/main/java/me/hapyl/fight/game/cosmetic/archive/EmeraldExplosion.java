package me.hapyl.fight.game.cosmetic.archive;

import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.eterna.module.util.CollectionUtils;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

public class EmeraldExplosion extends Cosmetic {

    private final Material[] MATERIALS;
    private final int ITEM_SPAWN_COUNT = 10;

    public EmeraldExplosion() {
        super("Emerald Explosion", "Hide the villagers!", Type.DEATH, Rarity.UNCOMMON, Material.EMERALD);

        setExclusive(true);

        MATERIALS = new Material[] { Material.EMERALD_BLOCK, Material.EMERALD };
    }

    @Override
    protected void onDisplay(Display display) {
        for (int i = 0; i < ITEM_SPAWN_COUNT; i++) {
            display.item(CollectionUtils.randomElement(MATERIALS, MATERIALS[0]), 60);
        }

        display.particle(Particle.HAPPY_VILLAGER, 10, 0.5d, 0.25d, 0.5d, 0.0f);
        display.particle(Particle.TOTEM_OF_UNDYING, 20, 0.0d, 0.0d, 0.0d, 0.5f);

        new TickingGameTask() {
            @Override
            public void run(int tick) {
                if (tick > ITEM_SPAWN_COUNT) {
                    cancel();
                    return;
                }

                display.sound(Sound.ENTITY_PLAYER_LEVELUP, 1.0f - (0.5f / ITEM_SPAWN_COUNT * tick));
            }
        }.runTaskTimer(0, 1);
    }
}

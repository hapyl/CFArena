package me.hapyl.fight.game.cosmetic.kill;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.cosmetic.Type;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class BloodCosmetic extends Cosmetic {
    public BloodCosmetic(@Nonnull Key key) {
        super(key, "Blood", Type.KILL);

        setDescription("""
                A classic redstone particles mimicking blood.
                """
        );

        setRarity(Rarity.COMMON);
        setIcon(Material.REDSTONE);
    }

    @Override
    public void onDisplay(@Nonnull Display display) {
        display.particle(Particle.BLOCK, 20, 0.4d, 0.4d, 0.4d, 0.0f, Bukkit.createBlockData(Material.REDSTONE_BLOCK));
        display.sound(Sound.BLOCK_STONE_BREAK, 0.0f);
    }
}

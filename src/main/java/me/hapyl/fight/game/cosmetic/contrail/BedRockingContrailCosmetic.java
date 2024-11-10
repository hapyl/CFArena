package me.hapyl.fight.game.cosmetic.contrail;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Rarity;
import org.bukkit.Material;
import org.bukkit.Particle;

import javax.annotation.Nonnull;

public class BedRockingContrailCosmetic extends BlockContrailCosmetic {

    public BedRockingContrailCosmetic(@Nonnull Key key) {
        super(key, "Bed Rocking", "The strongest of its kind!", Rarity.LEGENDARY);

        setIcon(Material.BEDROCK);
        addMaterials(Material.BEDROCK);

        setExclusive(true);
    }

    @Override
    public void onMove(@Nonnull Display display) {
        super.onMove(display);
        display.particle(display.getLocation().add(0.0d, 0.25d, 0.0d), Particle.ASH, 3, 0.2d, 0.0d, 0.2d, 0);
    }
}

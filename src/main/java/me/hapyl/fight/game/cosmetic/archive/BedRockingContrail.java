package me.hapyl.fight.game.cosmetic.archive;

import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.cosmetic.contrail.BlockContrailCosmetic;
import org.bukkit.Material;
import org.bukkit.Particle;

public class BedRockingContrail extends BlockContrailCosmetic {

    public BedRockingContrail() {
        super("Bed Rocking", "The strongest of its kind!", Rarity.LEGENDARY);

        setExclusive(true);
        setIcon(Material.BEDROCK);
        addMaterials(Material.BEDROCK);
    }

    @Override
    public void onMove(Display display) {
        super.onMove(display);
        display.particle(display.getLocation().add(0.0d, 0.25d, 0.0d), Particle.ASH, 3, 0.2d, 0.0d, 0.2d, 0);
    }
}

package me.hapyl.fight.game.cosmetic.archive;

import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.cosmetic.contrail.BlockContrailCosmetic;
import org.bukkit.Material;

public class RainbowContrail extends BlockContrailCosmetic {

    public RainbowContrail() {
        super(
                "Rainbow",
                "There are all the colors!__&8Or are there?",
                Rarity.LEGENDARY
        );

        setExclusive(true);
        setIcon(Material.RED_STAINED_GLASS);

        addMaterials(
                Material.RED_STAINED_GLASS,
                Material.ORANGE_STAINED_GLASS,
                Material.YELLOW_STAINED_GLASS,
                Material.LIME_STAINED_GLASS,
                Material.LIGHT_BLUE_STAINED_GLASS,
                Material.MAGENTA_STAINED_GLASS,
                Material.BLACK_STAINED_GLASS
        );
    }
}

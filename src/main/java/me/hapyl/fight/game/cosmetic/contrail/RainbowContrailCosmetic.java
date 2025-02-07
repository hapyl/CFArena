package me.hapyl.fight.game.cosmetic.contrail;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.cosmetic.Rarity;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class RainbowContrailCosmetic extends BlockContrailCosmetic {

    public RainbowContrailCosmetic(@Nonnull Key key) {
        super(key, "Rainbow", "There are all the colors!__&8Or are there?", Rarity.LEGENDARY);

        addMaterials(
                Material.RED_STAINED_GLASS,
                Material.ORANGE_STAINED_GLASS,
                Material.YELLOW_STAINED_GLASS,
                Material.LIME_STAINED_GLASS,
                Material.LIGHT_BLUE_STAINED_GLASS,
                Material.MAGENTA_STAINED_GLASS,
                Material.BLACK_STAINED_GLASS
        );

        setIcon(Material.RED_STAINED_GLASS);
        setExclusive(true);
    }
}

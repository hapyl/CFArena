package me.hapyl.fight.game.cosmetic.storage;

import me.hapyl.fight.game.cosmetic.contrail.BlockContrailCosmetic;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.cosmetic.ShopItem;
import org.bukkit.Material;

public class RainbowContrail extends BlockContrailCosmetic {

    public RainbowContrail() {
        super(
                "Rainbow",
                "There are all the colors!__&8Or are there?",
                ShopItem.NOT_PURCHASABLE,
                Rarity.LEGENDARY
        );

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

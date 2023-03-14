package me.hapyl.fight.game.cosmetic;

import me.hapyl.fight.game.shop.Rarity;
import org.bukkit.Material;

public class HatCosmetic extends Cosmetic {

    public HatCosmetic(String name, String description, long cost, Type type, Rarity rarity, Material icon) {
        super(name, description, cost, type, rarity, icon);
    }

    @Override
    public final Cosmetic setIcon(Material icon) {
        return super.setIcon(Material.PLAYER_HEAD);
    }

    @Override
    public void onDisplay(Display display) {

    }
}

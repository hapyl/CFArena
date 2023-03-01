package me.hapyl.fight.game.cosmetic;

import me.hapyl.fight.game.shop.Rarity;

public class PrefixCosmetic extends Cosmetic {

    private final String prefix;

    public PrefixCosmetic(String name, String description, String prefix, long cost, Rarity rarity) {
        super(name, description, cost, Type.PREFIX, rarity);

        this.prefix = prefix;
    }

    @Override
    public void onDisplay(Display display) {

    }
}

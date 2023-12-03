package me.hapyl.fight.game.cosmetic;

import javax.annotation.Nonnull;

public interface RareItem {

    @Nonnull
    Rarity getRarity();

    @Nonnull
    String getId();

}

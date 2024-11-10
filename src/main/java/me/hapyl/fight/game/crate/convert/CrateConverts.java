package me.hapyl.fight.game.crate.convert;

import me.hapyl.fight.database.entry.Currency;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.crate.Crates;
import me.hapyl.fight.util.EnumWrapper;

import javax.annotation.Nonnull;

public enum CrateConverts implements EnumWrapper<CrateConvert> {

    COMMON_TO_UNCOMMON(
            new CrateConvert(Rarity.UNCOMMON.getColor() + "Uncommon Convert", "Convert your common crates into uncommon!")
                    .setToConvert(Crates.COMMON, 9)
                    .setToConvert(Currency.CHEST_DUST, 5)
                    .setToConvert(Currency.COINS, 500)
                    .setConvertProduct(Crates.UNCOMMON)
    ),

    UNCOMMON_TO_RARE(
            new CrateConvert(Rarity.RARE.getColor() + "Rare Convert", "Convert your uncommon crates into rare!")
                    .setToConvert(Crates.UNCOMMON, 9)
                    .setToConvert(Currency.CHEST_DUST, 10)
                    .setToConvert(Currency.COINS, 1000)
                    .setConvertProduct(Crates.RARE)
    ),

    ;


    private final CrateConvert crateConvert;

    CrateConverts(@Nonnull CrateConvert crateConvert) {
        this.crateConvert = crateConvert;
    }

    @Nonnull
    @Override
    public CrateConvert getWrapped() {
        return crateConvert;
    }
}

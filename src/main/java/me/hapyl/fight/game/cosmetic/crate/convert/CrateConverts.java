package me.hapyl.fight.game.cosmetic.crate.convert;

import me.hapyl.fight.database.entry.Currency;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.cosmetic.crate.Crates;
import me.hapyl.fight.util.EnumWrapper;

import javax.annotation.Nonnull;

public enum CrateConverts implements EnumWrapper<CrateConvert> {

    COMMON_TO_UNCOMMON(
            new CrateConvert(Rarity.UNCOMMON + "Uncommon Convert", "Convert your common crates into uncommon!")
                    .setCrateToConvert(Crates.COMMON, 9)
                    .setCurrencyToConvert(Currency.CHEST_DUST, 5)
                    .setCurrencyToConvert(Currency.COINS, 500)
                    .setConvertProduct(Crates.UNCOMMON)
    ),

    UNCOMMON_TO_RARE(
            new CrateConvert(Rarity.RARE + "Rare Convert", "Convert your uncommon crates into rare!")
                    .setCrateToConvert(Crates.UNCOMMON, 9)
                    .setCurrencyToConvert(Currency.CHEST_DUST, 10)
                    .setCurrencyToConvert(Currency.COINS, 1000)
                    .setConvertProduct(Crates.RARE)
    ),

    ;


    private final CrateConvert crateConvert;

    CrateConverts(@Nonnull CrateConvert crateConvert) {
        this.crateConvert = crateConvert;
    }

    @Nonnull
    @Override
    public CrateConvert get() {
        return crateConvert;
    }
}

package me.hapyl.fight.store;

import me.hapyl.fight.database.entry.Currency;
import me.hapyl.fight.game.reward.CurrencyType;

import javax.annotation.Nonnull;

public interface Purchasable {

    /**
     * Gets the {@link CurrencyType} of this {@link Purchasable}.
     *
     * @return the {@link CurrencyType} of this {@link Purchasable}.
     */
    @Nonnull
    Currency getCurrency();

    /**
     * Gets the amount of {@link CurrencyType} required to purchase this item.
     *
     * @return the amount of {@link CurrencyType} required to purchase this item.
     */
    long getPrice();

    /**
     * Returns true is the price is greater than 0.
     *
     * @return true if the price is greater than 0.
     */
    default boolean isPurchasable() {
        return getPrice() > 0;
    }

    @Nonnull
    default String getPriceFormatted() {
        return getCurrency().formatProduct(getPrice());
    }
}

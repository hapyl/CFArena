package me.hapyl.fight.game.cosmetic;

public interface RubyPurchasable {

    /**
     * Gets the price of this item in rubies.
     *
     * @return the price of this item in rubies.
     */
    long getRubyPrice();

    /**
     * Sets the price of this item in rubies.
     *
     * @param price - New price.
     */
    void setRubyPrice(long price);

    /**
     * Returns true if this item is purchasable with rubies.
     *
     * @return true if this item is purchasable with rubies, false otherwise.
     */
    default boolean isPurchasableWithRubies() {
        return getRubyPrice() > 0;
    }

}

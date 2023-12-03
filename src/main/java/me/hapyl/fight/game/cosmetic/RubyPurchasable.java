package me.hapyl.fight.game.cosmetic;

public interface RubyPurchasable {

    long getRubyPrice();

    void setRubyPrice(long rubies);

    default boolean isPurchasableWithRubies() {
        return getRubyPrice() > 0;
    }

}

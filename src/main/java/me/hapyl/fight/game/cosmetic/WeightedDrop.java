package me.hapyl.fight.game.cosmetic;

public interface WeightedDrop extends RandomDrop {

    int getWeight();

    @Override
    @Deprecated
    default float getDropChance() {
        return getWeight();
    }

    @Override
    @Deprecated
    default String getDropChanceString() {
        return RandomDrop.super.getDropChanceString();
    }
}

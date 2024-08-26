package me.hapyl.fight.game.reward;

import javax.annotation.Nonnull;

public abstract class SimpleReward implements Reward {

    private final String name;

    public SimpleReward(@Nonnull String name) {
        this.name = name;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}

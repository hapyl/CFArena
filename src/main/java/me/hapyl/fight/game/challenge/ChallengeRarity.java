package me.hapyl.fight.game.challenge;

import me.hapyl.eterna.module.util.Described;
import me.hapyl.fight.game.reward.CurrencyReward;
import me.hapyl.fight.game.reward.Reward;

import javax.annotation.Nonnull;

public enum ChallengeRarity implements Described {

    COMMON("Common", Reward.currency("Common Challenge")
            .withCoins(1000)
            .withExp(1000)
    ),
    UNCOMMON("Uncommon", Reward.currency("Uncommon Challenge")
            .withCoins(1500)
            .withExp(1500)
    ),
    RARE("Rare", Reward.currency("Rare Challenge")
            .withCoins(2500)
            .withExp(2500)
            .withRubies(1)
    );

    private final String name;
    private final CurrencyReward reward;

    ChallengeRarity(String name, CurrencyReward reward) {
        this.name = name + " Bond";
        this.reward = reward;
    }

    @Nonnull
    public CurrencyReward getReward() {
        return reward;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "";
    }
}

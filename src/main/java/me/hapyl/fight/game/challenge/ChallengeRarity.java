package me.hapyl.fight.game.challenge;

import me.hapyl.eterna.module.util.Described;
import me.hapyl.fight.game.reward.Reward;
import me.hapyl.fight.game.reward.RewardResource;

import javax.annotation.Nonnull;

public enum ChallengeRarity implements Described {

    COMMON(
            "Common", Reward.ofRepeatable("Common Challenge")
                            .withResource(RewardResource.COINS, 1000)
                            .withResource(RewardResource.EXPERIENCE, 1000)
    ),
    UNCOMMON(
            "Uncommon", Reward.ofRepeatable("Uncommon Challenge")
                              .withResource(RewardResource.COINS, 1500)
                              .withResource(RewardResource.EXPERIENCE, 1500)
    ),
    RARE(
            "Rare", Reward.ofRepeatable("Rare Challenge")
                          .withResource(RewardResource.COINS, 2500)
                          .withResource(RewardResource.EXPERIENCE, 2500)
                          .withResource(RewardResource.RUBY, 1)
    );

    private final String name;
    private final Reward reward;

    ChallengeRarity(String name, Reward reward) {
        this.name = name + " Bond";
        this.reward = reward;
    }

    @Nonnull
    public Reward getReward() {
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

package me.hapyl.fight.game.reward;

import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public enum EnumReward implements Reward {

    HERO_RATING_FIRST_TIME(Reward.currency("Hero Rating").withCoins(5_000)),

    ;

    private final Reward reward;

    EnumReward(@Nonnull Reward reward) {
        this.reward = reward;
    }

    @Nonnull
    @Override
    public String getName() {
        return reward.getName();
    }

    @Nonnull
    @Override
    public RewardDescription getDescription(@Nonnull Player player) {
        return reward.getDescription(player);
    }

    @Override
    public void grant(@Nonnull Player player) {
        reward.grant(player);
    }

    @Override
    public void revoke(@Nonnull Player player) {
        reward.revoke(player);
    }
}

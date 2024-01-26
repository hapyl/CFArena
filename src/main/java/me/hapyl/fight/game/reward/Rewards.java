package me.hapyl.fight.game.reward;

import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public enum Rewards {

    EMPTY(new Reward() {
        @Nonnull
        @Override
        public RewardDisplay getDisplay(@Nonnull Player player) {
            return RewardDisplay.EMPTY;
        }

        @Override
        public void grant(@Nonnull Player player) {
            player.sendMessage("Granting empty reward!");
        }

        @Override
        public void revoke(@Nonnull Player player) {
            player.sendMessage("Revoking empty reward!");
        }
    }),

    HERO_RATING_FIRST_TIME(new CurrencyReward().withCoins(5000)),

    ;

    private final Reward reward;

    Rewards(Reward reward) {
        this.reward = reward;
    }

    public Reward getReward() {
        return reward;
    }
}

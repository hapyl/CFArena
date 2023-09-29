package me.hapyl.fight.game.reward;

import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public abstract class DisplayReward extends Reward {
    public DisplayReward(String name) {
        super(name);
    }

    @Override
    public final void grantReward(@Nonnull Player player) {
    }

    @Override
    public final void revokeReward(@Nonnull Player player) {
    }
}

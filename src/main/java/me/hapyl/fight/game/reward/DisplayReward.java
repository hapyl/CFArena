package me.hapyl.fight.game.reward;

import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public abstract class DisplayReward extends Reward {
    public DisplayReward(String name) {
        super();
    }

    @Override
    public final void grant(@Nonnull Player player) {
    }

    @Override
    public final void revoke(@Nonnull Player player) {
    }
}

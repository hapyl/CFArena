package me.hapyl.fight.game.reward;

import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

/**
 * This is a dummy reward for display purposes.
 */
public class DisplayReward extends SimpleReward {

    private final RewardDescription description;

    DisplayReward(@Nonnull String name, @Nonnull String description) {
        super(name);

        this.description = RewardDescription.of(description);
    }

    @Nonnull
    @Override
    public RewardDescription getDescription(@Nonnull Player player) {
        return this.description;
    }

    @Override
    public final void grant(@Nonnull Player player) {
    }

    @Override
    public final void revoke(@Nonnull Player player) {
    }
}

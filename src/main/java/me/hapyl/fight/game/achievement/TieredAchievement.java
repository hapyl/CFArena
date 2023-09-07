package me.hapyl.fight.game.achievement;

import me.hapyl.fight.util.Range;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents an achievement that can be completed multiple times.
 */
public class TieredAchievement extends Achievement {

    private final Tier[] tiers;

    public TieredAchievement(@Nullable String id, @Nonnull String name, @Nonnull String description) {
        super(id, name, description);

        this.tiers = new Tier[5];
    }

    public TieredAchievement(@Nonnull String name, @Nonnull String description, @Nonnull @Range(min = 5, max = 5) int... tiers) {
        this(null, name, description, tiers);
    }

    public TieredAchievement(@Nullable String id, @Nonnull String name, @Nonnull String description, @Nonnull @Range(min = 5, max = 5) int... tiers) {
        this(id, name, description);

        if (tiers.length != 5) {
            throw new IllegalArgumentException("There must be 5 tiers!");
        }

        this.setCategory(Category.TIERED);

        for (int index = 0; index < tiers.length; index++) {
            final int tier = tiers[index];

            this.tiers[index] = new Tier(index, tier, 5 * (index + 1));
            this.maxCompleteCount = tier;
        }
    }

    @Nonnull
    public Tier[] getTiers() {
        return tiers;
    }

    @Override
    public void awardPlayer(Player player) {
        final Tier tier = getTier(getCompleteCount(player));

        if (tier == null) {
            return;
        }

        tier.reward(player);
    }

    @Override
    public void markComplete(Player player) {
        final int completeCount = getCompleteCount(player);

        if (completeCount < maxCompleteCount) {
            return;
        }

        super.markComplete(player);
    }

    @Override
    public void displayComplete(Player player) {
        final int nextCompleteCount = getCompleteCount(player);
        final Tier tier = getTier(nextCompleteCount);

        if (tier == null) {
            return;
        }

        super.displayComplete(player);
    }

    @Nullable
    public Tier getTier(int completeCount) {
        for (Tier tier : tiers) {
            if (tier.getTier() == completeCount) {
                return tier;
            }
        }

        return null;
    }

    @Nonnull
    @Override
    public String getType() {
        return "Tiered Achievement";
    }
}

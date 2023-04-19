package me.hapyl.fight.game.achievement;

import com.google.common.collect.Maps;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Represents an achievement that can be completed multiple times.
 */
public class ProgressAchievement extends Achievement {

    private final Map<Integer, ProgressTier> tiers;
    private int maxCompleteCount;

    public ProgressAchievement(String name, String description) {
        super(name, description);
        this.tiers = Maps.newLinkedHashMap();
        this.maxCompleteCount = 0;
    }

    public void setTier(int requirement, ProgressTier tier) {
        this.tiers.put(requirement, tier);
        this.maxCompleteCount = Math.max(requirement, maxCompleteCount);
    }

    /**
     * Returns ProgressTier if requirement is met, null otherwise.
     *
     * @param requirement requirement to check
     * @return ProgressTier if requirement is met, null otherwise.
     */
    @Nullable
    public ProgressTier getTier(int requirement) {
        if (requirement > maxCompleteCount) {
            return null;
        }

        return this.tiers.get(requirement);
    }

    public int getMaxCompleteCount() {
        return maxCompleteCount;
    }

}

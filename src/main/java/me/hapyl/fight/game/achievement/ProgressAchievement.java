package me.hapyl.fight.game.achievement;

import me.hapyl.fight.game.reward.Reward;

/**
 * Represents an achievement that can be completed multiple times.
 */
public class ProgressAchievement extends Achievement {

    public ProgressAchievement(String name, String description) {
        super(name, description);
    }

    @Override
    public ProgressAchievement setReward(Reward reward) {
        return setReward(1, reward);
    }

    public ProgressAchievement setReward(int requirement, Reward reward) {
        this.rewards.put(requirement, reward);
        this.maxCompleteCount = Math.max(requirement, maxCompleteCount);

        return this;
    }

}

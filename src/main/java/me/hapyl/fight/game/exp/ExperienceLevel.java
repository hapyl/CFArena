package me.hapyl.fight.game.exp;

import com.google.common.collect.Lists;
import me.hapyl.fight.game.reward.Reward;

import java.util.List;

public class ExperienceLevel {

    private final long level;
    private final long exp;

    private List<Reward> reward;

    public ExperienceLevel(long level, long exp) {
        this.level = level;
        this.exp = exp;
        this.reward = Lists.newArrayList();
    }

    public ExperienceLevel addReward(Reward reward) {
        this.reward.add(reward);
        return this;
    }

    public void setReward(List<Reward> reward) {
        this.reward = reward;
    }

    public long getLevel() {
        return level;
    }

    public long getExpRequired() {
        return exp;
    }

    public List<Reward> getRewards() {
        return reward;
    }

}

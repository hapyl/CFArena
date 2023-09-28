package me.hapyl.fight.game.experience;

import com.google.common.collect.Lists;
import me.hapyl.fight.game.reward.Reward;

import java.util.List;

public class ExperienceLevel {

    private final long level;
    private final long exp;

    private final List<Reward> reward;

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
        this.reward.clear();
        this.reward.addAll(reward);
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

    public <T> List<T> getRewards(Class<T> filter) {
        return reward.stream().filter(filter::isInstance).map(filter::cast).toList();
    }

    public boolean hasRewards() {
        return !reward.isEmpty();
    }

    @Override
    public String toString() {
        return "ExperienceLevel{level=%s, exp=%s}".formatted(level, exp);
    }

    public boolean isPrestige() {
        return level % 5 == 0;
    }
}

package kz.hapyl.fight.game.reward;

import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class ExpLevelUpReward extends Reward {

    private final long points;

    public ExpLevelUpReward(long points) {
        super("Experience Level Up Reward");
        this.points = points;
    }

    @Nullable
    @Override
    public String[] getRewardString() {
        return new String[] { "%s Skill Points".formatted(points) };
    }

    @Override
    public void grantReward(Player player) {

    }

    @Override
    public void revokeReward(Player player) {

    }
}

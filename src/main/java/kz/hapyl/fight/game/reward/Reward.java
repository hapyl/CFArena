package kz.hapyl.fight.game.reward;

import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public abstract class Reward {

    private final String name;

    public Reward(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public abstract String[] getRewardString();

    public abstract void grantReward(Player player);

    public abstract void revokeReward(Player player);

}

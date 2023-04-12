package me.hapyl.fight.game.reward;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.game.Manager;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public abstract class Reward {

    private final String name;

    public Reward(String name) {
        this.name = name;
    }

    public static Reward create(String name) {
        return new Reward(name) {
            @Override
            public void grantReward(Player player) {

            }

            @Override
            public void revokeReward(Player player) {

            }

            @Nullable
            @Override
            public String getDisplay() {
                return null;
            }
        };
    }

    public PlayerDatabase getDatabase(Player player) {
        return Manager.current().getProfile(player).getDatabase();
    }

    public String getName() {
        return name;
    }

    @Nullable
    public abstract String getDisplay();

    public abstract void grantReward(Player player);

    public abstract void revokeReward(Player player);

    @Override
    public String toString() {
        return "Reward{name=%s}".formatted(name);
    }


}

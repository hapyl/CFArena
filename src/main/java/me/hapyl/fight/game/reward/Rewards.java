package me.hapyl.fight.game.reward;

import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public enum Rewards {

    EMPTY(new Reward("Empty Reward") {
        @Override
        public void display(@Nonnull Player player, @Nonnull ItemBuilder builder) {
        }

        @Override
        public void grantReward(@Nonnull Player player) {
            player.sendMessage("Granting empty reward!");
        }

        @Override
        public void revokeReward(@Nonnull Player player) {
            player.sendMessage("Revoking empty reward!");
        }
    }),

    DAILY(new DailyReward()),

    ;

    private final Reward reward;

    Rewards(Reward reward) {
        this.reward = reward;
    }

    public Reward getReward() {
        return reward;
    }
}

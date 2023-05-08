package me.hapyl.fight.game.reward;

import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.entity.Player;

public enum Rewards {

    EMPTY(new Reward("Empty Reward") {
        @Override
        public void display(Player player, ItemBuilder builder) {
        }

        @Override
        public void grantReward(Player player) {
            player.sendMessage("Granting empty reward!");
        }

        @Override
        public void revokeReward(Player player) {
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

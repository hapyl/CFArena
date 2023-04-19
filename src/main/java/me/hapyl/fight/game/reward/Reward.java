package me.hapyl.fight.game.reward;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.game.Manager;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.entity.Player;

public abstract class Reward {

    public static final Reward EMPTY = new Reward("Empty Reward") {
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
    };

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

            @Override
            public void display(Player player, ItemBuilder builder) {
            }
        };
    }

    public PlayerDatabase getDatabase(Player player) {
        return Manager.current().getOrCreateProfile(player).getDatabase();
    }

    public String getName() {
        return name;
    }

    public abstract void display(Player player, ItemBuilder builder);

    public abstract void grantReward(Player player);

    public abstract void revokeReward(Player player);

    @Override
    public String toString() {
        return "Reward{name=%s}".formatted(name);
    }


}

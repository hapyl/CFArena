package me.hapyl.fight.game.reward;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public abstract class Reward {

    private final String name;

    public Reward(String name) {
        this.name = name;
    }

    public PlayerDatabase getDatabase(Player player) {
        return PlayerDatabase.getDatabase(player);
    }

    public String getName() {
        return name;
    }

    public abstract void display(@Nonnull Player player, @Nonnull ItemBuilder builder);

    public abstract void grantReward(@Nonnull Player player);

    public abstract void revokeReward(@Nonnull Player player);

    @Nonnull
    public ItemBuilder displayGet(@Nonnull Player player, @Nonnull ItemBuilder builder) {
        display(player, builder);

        return builder;
    }

    @Override
    public String toString() {
        return "Reward{name=%s}".formatted(name);
    }

    // static members

    @Nonnull
    public static Reward create(String name) {
        return new Reward(name) {
            @Override
            public void grantReward(@Nonnull Player player) {

            }

            @Override
            public void revokeReward(@Nonnull Player player) {

            }

            @Override
            public void display(@Nonnull Player player, @Nonnull ItemBuilder builder) {
            }
        };
    }

    @Nonnull
    public static CurrencyReward currency() {
        return CurrencyReward.create();
    }

}

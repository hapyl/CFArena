package me.hapyl.fight.game.reward;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.CosmeticEntry;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Cosmetics;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.util.Validate;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public abstract class Reward {

    public static final String BULLET = "&8+ &7";

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
        return name;
    }

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

    @Nonnull
    public static Reward cosmetics(final Cosmetics... cosmetics) {
        Validate.isTrue(cosmetics != null, "cosmetics cannot be null");
        Validate.isTrue(cosmetics.length > 0, "there must be at least one cosmetic");

        return new Reward("Cosmetics Reward") {

            @Override
            public void display(@Nonnull Player player, @Nonnull ItemBuilder builder) {
                for (Cosmetics enumCosmetic : cosmetics) {
                    final Cosmetic cosmetic = enumCosmetic.getCosmetic();

                    builder.addLore(BULLET + cosmetic.getFormatted());
                }
            }

            @Override
            public void grantReward(@Nonnull Player player) {
                final CosmeticEntry entry = PlayerDatabase.getDatabase(player).getCosmetics();

                for (Cosmetics cosmetic : cosmetics) {
                    entry.addOwned(cosmetic);
                }
            }

            @Override
            public void revokeReward(@Nonnull Player player) {
                final CosmeticEntry entry = PlayerDatabase.getDatabase(player).getCosmetics();

                for (Cosmetics cosmetic : cosmetics) {
                    entry.removeOwned(cosmetic);
                }
            }
        };
    }
}

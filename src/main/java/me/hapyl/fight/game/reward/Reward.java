package me.hapyl.fight.game.reward;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.CosmeticEntry;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Cosmetics;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.util.Validate;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public abstract class Reward {

    public static final String BULLET = "&8+ &7";

    public Reward() {
    }

    public PlayerDatabase getDatabase(Player player) {
        return PlayerDatabase.getDatabase(player);
    }

    /**
     * Gets the display for this reward.
     * Display is used in item lore and player chat to notify them what this reward gives.
     *
     * @param player - Player.
     * @return a {@link RewardDisplay}.
     */
    @Nonnull
    public abstract RewardDisplay getDisplay(@Nonnull Player player);

    /**
     * Grants this reward to a player.
     *
     * @param player - Player.
     */
    public abstract void grant(@Nonnull Player player);

    /**
     * Revokes this reward from a player.
     *
     * @param player - Player.
     */
    public abstract void revoke(@Nonnull Player player);

    @Nonnull
    public ItemBuilder formatBuilder(@Nonnull Player player, @Nonnull ItemBuilder builder) {
        getDisplay(player).forEach(builder::addLore);

        return builder;
    }

    public void displayChat(Player player) {
        Chat.sendMessage(player, "&6&lRewards:");

        getDisplay(player).forEach(string -> {
            Chat.sendMessage(player, string);
        });
    }

    @Nonnull
    public static Reward create() {
        return new Reward() {
            @Override
            public void grant(@Nonnull Player player) {
            }

            @Override
            public void revoke(@Nonnull Player player) {
            }

            @Override
            @Nonnull
            public RewardDisplay getDisplay(@Nonnull Player player) {
                return RewardDisplay.EMPTY;
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

        return new Reward() {

            @Override
            @Nonnull
            public RewardDisplay getDisplay(@Nonnull Player player) {
                final RewardDisplay display = new RewardDisplay();

                for (Cosmetics enumCosmetic : cosmetics) {
                    final Cosmetic cosmetic = enumCosmetic.getCosmetic();

                    display.add(cosmetic.getFormatted());
                }

                return display;
            }

            @Override
            public void grant(@Nonnull Player player) {
                final CosmeticEntry entry = PlayerDatabase.getDatabase(player).getCosmetics();

                for (Cosmetics cosmetic : cosmetics) {
                    entry.addOwned(cosmetic);
                }
            }

            @Override
            public void revoke(@Nonnull Player player) {
                final CosmeticEntry entry = PlayerDatabase.getDatabase(player).getCosmetics();

                for (Cosmetics cosmetic : cosmetics) {
                    entry.removeOwned(cosmetic);
                }
            }
        };
    }
}

package me.hapyl.fight.game.reward;

import me.hapyl.eterna.module.util.Named;
import me.hapyl.fight.Notifier;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public interface Reward extends Named {

    String BULLET = "&8+ &7";

    /**
     * Gets the name of this {@link Reward}.
     *
     * @return the name of this reward.
     */
    @Nonnull
    @Override
    String getName();

    /**
     * Gets the {@link RewardDescription} of this {@link Reward} for the given {@link Player}.
     *
     * @param player - Player.
     * @return the description of this reward for the given player.
     */
    @Nonnull
    default RewardDescription getDescription(@Nonnull Player player) {
        return RewardDescription.EMPTY;
    }

    /**
     * Grants this {@link Reward} to the given {@link Player}.
     *
     * @param player - Player to grant the reward to.
     */
    void grant(@Nonnull Player player);

    /**
     * Revokes this {@link Reward} from the given {@link Player}.
     *
     * @param player - Player to revoke the reward from.
     */
    void revoke(@Nonnull Player player);

    /**
     * Sends a reward message to the give {@link Player}.
     * <br>
     * The default implementation behaves as if:
     * <pre>{@code
     * Notifier.success(player, "&6&lReward: ");
     *
     * final RewardDescription description = getDescription(player);
     * description.forEach(player, Notifier::info);
     * }</pre>
     *
     * @param player - Player to send the message to.
     */
    default void sendRewardMessage(@Nonnull Player player) {
        Notifier.success(player, "&6&lReward: ");

        final RewardDescription description = getDescription(player);
        description.forEach(player, Notifier::info);
    }

    /**
     * Creates a new {@link CurrencyReward} with the given name.
     *
     * @param name - Reward name.
     * @return a new currency reward with the given name.
     */
    @Nonnull
    static CurrencyReward currency(@Nonnull String name) {
        return new CurrencyReward(name);
    }

    /**
     * Creates a new {@link CosmeticsReward} with the given name.
     *
     * @param name      - Name of the reward.
     * @param cosmetics - Cosmetics the reward will grant/revoke.
     * @return a new cosmetics reward with the given name.
     */
    @Nonnull
    static CosmeticsReward cosmetics(@Nonnull String name, @Nonnull Cosmetic... cosmetics) {
        return new CosmeticsReward(name, cosmetics);
    }

    /**
     * Creates a new {@link DisplayReward} with the given name and description.
     *
     * @param name        - Name of the reward.
     * @param description - Description of the reward.
     * @return a new display reward with the given name and description.
     */
    @Nonnull
    static DisplayReward display(@Nonnull String name, @Nonnull String description) {
        return new DisplayReward(name, description);
    }

}

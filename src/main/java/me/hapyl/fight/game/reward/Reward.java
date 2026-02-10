package me.hapyl.fight.game.reward;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.eterna.module.util.Named;
import me.hapyl.fight.CF;
import me.hapyl.fight.Message;
import me.hapyl.fight.database.entry.CosmeticEntry;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Reward implements Keyed, Named {

    public static final String BULLET = "&8+ &7";

    private final Key key;
    private final String name;

    private final Map<RewardResource, Long> resources;
    private final List<Cosmetic> cosmetics;

    public Reward(@Nonnull Key key, @Nonnull String name) {
        this.key = key;
        this.name = name;
        this.resources = Maps.newHashMap();
        this.cosmetics = Lists.newArrayList();
    }

    public Reward withResource(@Nonnull RewardResource resource, long amount) {
        this.resources.put(resource, amount);
        return this;
    }

    public long getResource(@Nonnull RewardResource resource) {
        return this.resources.getOrDefault(resource, 0L);
    }

    public Reward withCosmetic(@Nonnull Cosmetic cosmetic) {
        this.cosmetics.add(cosmetic);
        return this;
    }

    @Nonnull
    public List<Cosmetic> getCosmetics() {
        return Lists.newArrayList(this.cosmetics);
    }

    @Nonnull
    @Override
    public final Key getKey() {
        return this.key;
    }

    /**
     * Gets the name of this {@link Reward}.
     *
     * @return the name of this reward.
     */
    @Nonnull
    @Override
    public final String getName() {
        return this.name;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    @Nonnull
    public String getNameWithCheckmark(@Nonnull Player player) {
        return this.name + (hasClaimed(player) ? "&a" : "&c❌");
    }

    public boolean hasClaimed(@Nonnull Player player) {
        return CF.getDatabase(player).metadataEntry.claimedRewards.get(getKey(), false);
    }

    public void setClaimed(@Nonnull Player player, boolean obtained) {
        CF.getDatabase(player).metadataEntry.claimedRewards.set(getKey(), obtained);
    }

    public void appendDescription(@Nonnull Player player, @Nonnull RewardDescription description) {
    }

    @Nonnull
    public final RewardDescription getDescription(@Nonnull Player player) {
        return getDescription(player, true);
    }

    @Nonnull
    public final RewardDescription getDescription(@Nonnull Player player, boolean includeResources) {
        final RewardDescription description = new RewardDescription();

        if (includeResources) {
            resources.forEach((currency, value) -> description.appendIf(value > 0, currency.format(value)));
        }

        cosmetics.forEach(cosmetic -> description.append(cosmetic.getFormatted()));
        appendDescription(player, description);
        return description;
    }

    /**
     * Forcefully grants this reward to the given player.
     *
     * @param player - The player to forcefully grant this reward to.
     */
    @OverridingMethodsMustInvokeSuper
    public void doGrant(@Nonnull Player player) {
        final CosmeticEntry entry = CF.getDatabase(player).cosmeticEntry;

        // Grant currency
        resources.forEach((currency, value) -> currency.increment(player, value));

        // Grant cosmetics
        cosmetics.forEach(entry::addOwned);
    }

    /**
     * Revokes this {@link Reward} from the given {@link Player}.
     *
     * @param player - Player to revoke the reward from.
     */
    @OverridingMethodsMustInvokeSuper
    public void doRevoke(@Nonnull Player player) {
        final CosmeticEntry entry = CF.getDatabase(player).cosmeticEntry;

        // Revoke currency
        resources.forEach(((currency, value) -> currency.decrement(player, value)));

        // Revoke cosmetics
        cosmetics.forEach(entry::removeOwned);
    }

    /**
     * Grants this reward if the given player has not yet claimed this reward.
     *
     * @param player - The player to grant the reward to.
     */
    public final void grant(@Nonnull Player player) {
        grant(player, false);
    }

    public final void grant(@Nonnull Player player, boolean sendRewardMessage) {
        if (hasClaimed(player)) {
            return;
        }

        doGrant(player);
        setClaimed(player, true);

        if (sendRewardMessage) {
            sendRewardMessage(player);
        }
    }

    /**
     * Revokes this reward if the given player has claimed this reward.
     *
     * @param player - The player to revoke the reward from.
     */
    public final void revoke(@Nonnull Player player) {
        if (!hasClaimed(player)) {
            return;
        }

        doRevoke(player);
        setClaimed(player, false);
    }

    /**
     * Sends a reward message to the give {@link Player}.
     *
     * @param player - Player to send the message to.
     */
    public void sendRewardMessage(@Nonnull Player player) {
        getDescription(player).forEach(player, Message::info);
    }

    @Nonnull
    public static Reward of(@Nonnull Key key, @Nonnull String name, @Nonnull String description, @Nonnull Consumer<Player> grant) {
        return new Reward(key, name) {
            @Override
            public void doGrant(@Nonnull Player player) {
                super.doGrant(player);
                grant.accept(player);
            }

            @Override
            public void appendDescription(@Nonnull Player player, @Nonnull RewardDescription rewardDescription) {
                rewardDescription.append(description);
            }

        };
    }

    @Nonnull
    public static Reward of(@Nonnull Key key, @Nonnull String name, @Nonnull Consumer<Player> grant) {
        return of(key, name, name, grant); // Default description to just the name ig
    }

    @Nonnull
    public static Reward ofRepeatable(@Nonnull String name) {
        return new RepeatableReward(name);
    }

    @Nonnull
    public static Reward ofRepeatableResource(@Nonnull String name, long coins, long experience, long rubies) {
        return ofRepeatable(name)
                .withResource(RewardResource.COINS, coins)
                .withResource(RewardResource.EXPERIENCE, experience)
                .withResource(RewardResource.RUBY, rubies);
    }

    @Nonnull
    public static Reward ofRepeatableResource(@Nonnull String name, long coins, long experience) {
        return ofRepeatableResource(name, coins, experience, 0L);
    }

    @Nonnull
    public static Reward ofRepeatableResource(@Nonnull String name, long coins) {
        return ofRepeatableResource(name, coins, 0L, 0L);
    }

    @Nonnull
    public static Reward ofDisplay(@Nonnull String name, @Nonnull String description) {
        return new RepeatableReward(name) {
            @Override
            public void appendDescription(@Nonnull Player player, @Nonnull RewardDescription rewardDescription) {
                rewardDescription.append(description);
            }
        };
    }

    public static void sendRewardsHeader(@Nonnull Player player) {
        Message.info(player, "&6&lʀᴇᴡᴀʀᴅꜱ:");
    }
}

package me.hapyl.fight.game.reward;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.util.Compute;
import me.hapyl.fight.Message;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public class StackedReward {

    private final List<Reward> rewards;
    private final Map<RewardResource, Long> resources;

    public StackedReward(@Nonnull List<Reward> rewards) {
        this.rewards = rewards;
        this.resources = Maps.newLinkedHashMap(); // Need to preserve the order

        // Count resources
        rewards.forEach(reward -> {
            for (RewardResource resource : RewardResource.values()) {
                final long rewardResource = reward.getResource(resource);

                if (rewardResource > 0) {
                    resources.compute(resource, Compute.longAdd(rewardResource));
                }
            }
        });
    }

    public void grantAll(@Nonnull Player player) {
        final RewardDescription description = new RewardDescription();

        // Append resources first because I like it better this way
        resources.forEach((resource, value) -> {
            description.appendIf(value > 0, resource.format(value));
        });

        rewards.forEach(reward -> {
            reward.grant(player, false);
            description.append(reward.getDescription(player, false));
        });

        // Display
        Reward.sendRewardsHeader(player);
        description.forEach(string -> Message.info(player, string));
    }
}

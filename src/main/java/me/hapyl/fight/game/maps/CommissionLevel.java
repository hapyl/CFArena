package me.hapyl.fight.game.maps;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.fight.game.commission.CommissionReward;
import me.hapyl.fight.game.commission.CommissionRewardTable;
import me.hapyl.fight.game.commission.MonsterSpawn;
import me.hapyl.fight.game.commission.Tier;
import me.hapyl.fight.game.entity.commission.CommissionEntityType;
import me.hapyl.fight.util.PlayerItemCreator;
import me.hapyl.fight.util.PlayerRequirement;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CommissionLevel extends Level implements PlayerItemCreator {

    private static final int MAX_REWARD_DISPLAY = 4;

    protected final CommissionRewardTable rewards;
    protected final List<MonsterSpawn> monsterSpawns;
    protected final List<PlayerRequirement> requirements;

    @Nonnull protected String texture;
    @Nonnull protected Supplier<CommissionEntityType> boss;

    protected long expReward;

    protected CommissionLevel(@Nonnull EnumLevel handle, @Nonnull String name) {
        super(handle, name);

        this.rewards = new CommissionRewardTable();
        this.monsterSpawns = Lists.newArrayList();
        this.requirements = Lists.newArrayList();

        this.texture = "8ff6948468de84497c5d469ceb7b19a2ce09a0ec5a60b0af79c927763c49cc7d";
        this.boss = () -> null;
        this.expReward = 100;
    }

    @Nonnull
    public String texture() {
        return texture;
    }

    @Nonnull
    public List<MonsterSpawn> monsterSpawns() {
        return monsterSpawns;
    }

    @Nonnull
    public CommissionRewardTable rewards() {
        return rewards;
    }

    public long expReward() {
        return expReward;
    }

    public long expReward(@Nonnull Tier tier) {
        return (long) (expReward * tier.expMultiplier());
    }

    @Override
    public final boolean isPlayable() {
        // Commission levels handled differently, so they cannot be "played" in a normal PvP
        return false;
    }

    public boolean hasMetAllRequirements(@Nonnull Player player) {
        for (PlayerRequirement requirement : this.requirements) {
            if (!requirement.hasRequirements(player)) {
                return false;
            }
        }

        return true;
    }

    @Nonnull
    @Override
    public ItemBuilder create(@Nonnull Player player) {
        final ItemBuilder builder = ItemBuilder.playerHeadUrl(texture);
        builder.setName(getName());
        builder.addLore();
        builder.addTextBlockLore(getDescription());
        builder.addLore();

        // Boss
        final CommissionEntityType boss = this.boss.get();

        if (boss != null) {
            builder.addLore("Boss: &f%s".formatted(boss.getNameFormatted()));
            builder.addTextBlockLore(boss.getDescription(), "&8 ");
        }
        else {
            builder.addLore("&cNo boss!");
        }

        // Rewards
        builder.addLore();
        builder.addLore("Possible Rewards:");

        final LinkedHashMap<CommissionReward, Double> rewards = this.rewards.getRewardsSorted(CommissionRewardTable.RewardSort.RARE_TO_COMMON);

        rewards.entrySet()
               .stream()
               .limit(MAX_REWARD_DISPLAY)
               .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, LinkedHashMap::new))
               .forEach((reward, chance) -> {
                   builder.addLore(" %s  &8%.2f%%".formatted(reward.getName(), chance * 100));
               });

        if (rewards.size() > MAX_REWARD_DISPLAY) {
            final int moreRewards = rewards.size() - MAX_REWARD_DISPLAY;

            builder.addLore(" &8...and %s more reward%s!".formatted(moreRewards, moreRewards > 1 ? "s" : ""));
        }

        // Requirements
        if (!requirements.isEmpty()) {
            builder.addLore();
            builder.addLore("Requirements:");

            requirements.forEach(requirement -> {
                builder.addLore(" " + requirement.getRequirementsString(player));
            });
        }

        return builder;
    }

}

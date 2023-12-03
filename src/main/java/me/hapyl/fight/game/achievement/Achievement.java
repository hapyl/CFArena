package me.hapyl.fight.game.achievement;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.reward.Reward;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Base achievement class.
 */
public class Achievement {

    protected final LinkedHashMap<Integer, Reward> rewards;
    private final String name;
    private final String description;
    protected int maxCompleteCount;
    private Category category;

    public Achievement(String name, String description) {
        this.name = name;
        this.description = description;
        this.rewards = Maps.newLinkedHashMap();
        this.category = Category.GAMEPLAY;
        this.maxCompleteCount = 1;
    }

    @Nullable
    public Reward getReward() {
        return this.rewards.get(1);
    }

    public Achievement setReward(Reward reward) {
        this.rewards.put(1, reward);
        return this;
    }

    @Nonnull
    public Map<Integer, Reward> getRewards() {
        return new HashMap<>(rewards);
    }

    @Nullable
    public Reward getReward(int requirement) {
        if (requirement > maxCompleteCount) {
            return null;
        }

        return this.rewards.get(requirement);
    }

    @Nullable
    public Reward nextReward(int current) {
        if (current >= maxCompleteCount) {
            return null;
        }

        return this.rewards.get(current + 1);
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void displayComplete(Player player) {
        Chat.sendMessage(player, "");
        Chat.sendCenterMessage(player, "&6&lACHIEVEMENT COMPLETE");
        Chat.sendCenterMessage(player, "&a" + getName());
        Chat.sendMessage(player, "");
    }

    public void onComplete(Player player) {
    }

    public int getMaxCompleteCount() {
        return maxCompleteCount;
    }
}

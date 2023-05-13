package me.hapyl.fight.game.achievement;

import com.google.common.collect.Maps;
import me.hapyl.fight.annotate.ForceLowercase;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.AchievementEntry;
import me.hapyl.fight.game.reward.Reward;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Base achievement class.
 */
public class Achievement extends PatternId {

    protected final LinkedHashMap<Integer, Reward> rewards;
    private final String name;
    private final String description;
    protected int maxCompleteCount;
    private Category category;

    public Achievement(@Nonnull @ForceLowercase String id, @Nonnull String name, @Nonnull String description) {
        super(Pattern.compile("^[a-z0-9_]+$"), id);
        this.name = name;
        this.description = description;
        this.rewards = Maps.newLinkedHashMap();
        this.category = Category.GAMEPLAY;
        this.maxCompleteCount = 1;
    }

    /**
     * @deprecated Names are subjective to change
     */
    @Deprecated
    public static Achievement create(@Nonnull String namedId, @Nonnull String description) {
        return new Achievement(namedId.toLowerCase().replace(" ", "_").trim(), namedId, description);
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

    /**
     * Tries to complete achievement for player.
     *
     * @param player - Player to complete achievement for.
     * @return true if completed, false if already completed.
     */
    public final boolean complete(Player player) {
        final PlayerDatabase database = PlayerDatabase.getDatabase(player);
        final AchievementEntry entry = database.getAchievementEntry();
        final int completeCount = entry.getCompleteCount(this);
        final int nextComplete = completeCount + 1;

        // If already completed, check if progress achievement
        if (completeCount > 0 && completeCount >= getMaxCompleteCount()) {
            return false;
        }

        entry.addCompleteCount(this);

        final Reward nextReward = getReward(nextComplete);

        if (nextReward == null) {
            return true;
        }

        entry.setCompletedAt(this, System.currentTimeMillis());

        onComplete(player);
        displayComplete(player);
        nextReward.grantReward(player);

        return true;
    }

    /**
     * Tries to complete achievement for all players.
     *
     * @param players - Players to complete achievement for.
     */
    public final void completeAll(Collection<Player> players) {
        players.forEach(this::complete);
    }

    public final boolean isCompleted(Player player) {
        return PlayerDatabase.getDatabase(player).getAchievementEntry().isCompleted(this);
    }

    public final int getCompleteCount(Player player) {
        return PlayerDatabase.getDatabase(player).getAchievementEntry().getCompleteCount(this);
    }

    public boolean isHidden() {
        return this instanceof HiddenAchievement;
    }

    public void onComplete(Player player) {
    }

    public int getMaxCompleteCount() {
        return maxCompleteCount;
    }

}

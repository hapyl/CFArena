package me.hapyl.fight.game.achievement;

import com.google.common.collect.Maps;
import me.hapyl.fight.annotate.ForceLowercase;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.AchievementEntry;
import me.hapyl.fight.game.reward.Reward;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.util.ProgressBarBuilder;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
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
    private AchievementTrigger trigger;

    public Achievement(@Nullable @ForceLowercase String id, @Nonnull String name, @Nonnull String description) {
        super(Pattern.compile("^[a-z0-9_]+$"));
        this.name = name;
        this.description = description;
        this.rewards = Maps.newLinkedHashMap();
        this.category = Category.GAMEPLAY;
        this.maxCompleteCount = 1;

        if (id != null) {
            setId(id);
        }
    }

    public Achievement(@Nonnull String name, @Nonnull String description) {
        this(null, name, description);
    }

    public void format(Player player, ItemBuilder builder) {
        final boolean hasCompletedOnce = hasCompletedAtLeastOnce(player);
        final boolean isCompleted = isComplete(player);
        final int completeCount = getCompleteCount(player);

        // Naming
        if (isHidden() && !hasCompletedOnce) {
            builder.setName("&7???");
            builder.setLore("&8???");
        }

        // Progress
        final int nextComplete = nextComplete(completeCount);

        builder.addLore();
        builder.addLore("&aProgress          &7%s/%s", completeCount, nextComplete);
        builder.addLore(ProgressBarBuilder.of("-", completeCount, nextComplete));

        // Rewards
        builder.addLore();
        if (rewards.isEmpty()) {
            builder.addLore("&7No rewards :(");
        }
        else {
            // If a single reward, just display the reward
            if (rewards.size() == 1) {
                builder.addLore("&7Reward:" + checkmark(isCompleted));
                getRewardNonnull().display(player, builder);
            }
            else {
                builder.addLore("&7Rewards:" + checkmark(isCompleted));
                int tier = 1;
                for (Map.Entry<Integer, Reward> entry : rewards.entrySet()) {
                    if (tier++ != 1) {
                    }

                    final Integer requirement = entry.getKey();
                    final Reward reward = entry.getValue();

                    builder.addLore("&7⭐".repeat(tier - 1) + checkmark(completeCount >= requirement));
                    reward.display(player, builder);
                }
            }
        }

        if (isCompleted) {
            builder.addLore();
            builder.addLore("&a&lCompleted!");
            builder.addLore("&8" + new SimpleDateFormat("MMMM d'th' yyyy, HH:mm:ss").format(getCompletedAt(player)));
        }
    }

    /**
     * Returns the sole reward; or null if none.
     *
     * @return the sole reward; or null if none.
     */
    @Nullable
    public Reward getReward() {
        return this.rewards.get(1);
    }

    /**
     * Sets the sole reward.
     *
     * @param reward - New reward.
     */
    public Achievement setReward(Reward reward) {
        this.rewards.put(1, reward);
        return this;
    }

    /**
     * Get the sole reward; or throws error if none.
     *
     * @return the sole reward; or throws error if none.
     * @throws IllegalArgumentException if there is no reward.
     */
    @Nonnull
    public Reward getRewardNonnull() throws IllegalArgumentException {
        final Reward reward = getReward();

        if (reward != null) {
            return reward;
        }

        throw new IllegalArgumentException("check for rewards before calling this");
    }

    /**
     * Returns copy of a rewards map.
     *
     * @return copy of a rewards map.
     */
    @Nonnull
    public Map<Integer, Reward> getRewards() {
        return new HashMap<>(rewards);
    }

    /**
     * Gets the reward for specific requirement; or null if none.
     *
     * @param requirement - Requirement. Starts at 1.
     * @return the reward for specific requirement; or null if none.
     */
    @Nullable
    public Reward getReward(int requirement) {
        if (requirement > maxCompleteCount) {
            return null;
        }

        return this.rewards.get(requirement);
    }

    /**
     * Returns the next reward; or null or last.
     *
     * @param current - Current reward index.
     * @return the next reward; or null or last.
     */
    @Nullable
    public Reward nextReward(int current) {
        if (current >= maxCompleteCount) {
            return null;
        }

        return this.rewards.get(current + 1);
    }

    /**
     * Returns the next requirement.
     *
     * @param requirement - Requirement.
     * @return the next requirement.
     */
    public int nextComplete(int requirement) {
        if (requirement >= maxCompleteCount) {
            return requirement;
        }

        final Integer[] integers = rewards.keySet().toArray(new Integer[] {});

        for (int i = integers.length - 1; i >= 0; i--) {
            if (requirement >= integers[i] && (i + 1 < integers.length)) {
                return integers[i + 1];
            }
        }

        return integers.length > 0 ? integers[0] : 1; // default to a first element or 1
    }

    /**
     * Returns the tier for requirement.
     *
     * @param requirement - Requirement.
     * @return the tier for requirement.
     */
    public int getTier(int requirement) {
        final Integer[] integers = rewards.keySet().toArray(new Integer[] {});

        for (int i = integers.length - 1; i >= 0; i--) {
            if (requirement >= integers[i]) {
                return i + 1;
            }
        }

        return 1;
    }

    /**
     * Gets the achievement category.
     *
     * @return the achievement category.
     */
    @Nonnull
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    /**
     * Gets the achievement name.
     *
     * @return the achievement name.
     */
    @Nonnull
    public String getName() {
        return name;
    }

    /**
     * Gets the achievement description.
     *
     * @return the achievement description.
     */
    @Nonnull
    public String getDescription() {
        return description;
    }

    /**
     * Displays the completion for the player.
     * Plugin may override this for custom display.
     *
     * @param player - Player.
     */
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

        // If last completion, mark completion time
        if (nextComplete >= getMaxCompleteCount()) {
            onComplete(player);
            entry.setCompletedAt(this, System.currentTimeMillis());
        }

        final Reward nextReward = getReward(nextComplete);

        if (nextReward == null) {
            return true;
        }

        displayComplete(player);
        nextReward.grantReward(player);

        return true;
    }

    /**
     * Tries to complete achievement for all players.
     *
     * @param players - Players to complete achievement for.
     */
    public final void completeAll(@Nonnull Collection<Player> players) {
        players.forEach(this::complete);
    }

    /**
     * Tries to complete achievement for all players in a team.
     *
     * @param team - Team to complete for.
     */
    public final void completeAll(@Nonnull GameTeam team) {
        if (team.isEmpty()) {
            return;
        }
        completeAll(team.getPlayersAsPlayers());
    }

    /**
     * Returns true if player has completed this achievement at least once.
     *
     * @param player - Player to check.
     * @return true if a player has completed this achievement at least once.
     */
    public final boolean hasCompletedAtLeastOnce(Player player) {
        return PlayerDatabase.getDatabase(player).getAchievementEntry().hasCompletedAtLeastOnce(this);
    }

    /**
     * Returns true if player has completed this achievement the max amount of times.
     *
     * @param player - Player to check.
     * @return true, if a player has completed this achievement the max amount of times.
     */
    public final boolean isComplete(Player player) {
        return getCompleteCount(player) == maxCompleteCount;
    }

    /**
     * Gets the number of times a player has completed this achievement.
     *
     * @param player - The player.
     * @return the number of times a player has completed this achievement.
     */
    public final int getCompleteCount(Player player) {
        return PlayerDatabase.getDatabase(player).getAchievementEntry().getCompleteCount(this);
    }

    /**
     * Gets the millis at when a player has completed this achievement.
     * Defaults to 0.
     *
     * @param player - Player.
     * @return the millis at when a player has completed this achievement.
     */
    public final long getCompletedAt(Player player) {
        return PlayerDatabase.getDatabase(player).achievementEntry.getCompletedAt(this);
    }

    /**
     * Returns true if this achievement is hidden.
     *
     * @deprecated just use instanceof
     */
    @Deprecated
    public boolean isHidden() {
        return this instanceof HiddenAchievement;
    }

    /**
     * Triggers each time player completes this achievement.
     *
     * @param player - Player.
     */
    public void onComplete(Player player) {
    }

    /**
     * Gets the maximum complete count for this achievement.
     *
     * @return the maximum complete count for this achievement.
     */
    public int getMaxCompleteCount() {
        return maxCompleteCount;
    }

    @Nonnull
    private String checkmark(boolean condition) {
        return condition ? " &a✔" : "";
    }

}

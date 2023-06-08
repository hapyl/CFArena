package me.hapyl.fight.game.achievement;

import com.google.common.collect.Maps;
import me.hapyl.fight.annotate.ForceLowercase;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.AchievementEntry;
import me.hapyl.fight.game.reward.CurrencyReward;
import me.hapyl.fight.game.reward.Reward;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.trigger.PlayerTrigger;
import me.hapyl.fight.trigger.Subscribe;
import me.hapyl.fight.util.ProgressBarBuilder;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.chat.Gradient;
import me.hapyl.spigotutils.module.chat.gradient.Interpolators;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
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
    private final String GRADIENT = new Gradient("ACHIVEMENT COMPLETE").makeBold()
            .rgb(new Color(235, 100, 52), new Color(235, 232, 52), Interpolators.QUADRATIC_SLOW_TO_FAST);
    protected int maxCompleteCount;
    @Nonnull
    protected Material icon;
    @Nonnull
    protected Material iconLocked;
    private Category category;

    public Achievement(@Nullable @ForceLowercase String id, @Nonnull String name, @Nonnull String description) {
        super(Pattern.compile("^[a-z0-9_]+$"));
        this.name = name;
        this.description = description;
        this.rewards = Maps.newLinkedHashMap();
        this.category = Category.GAMEPLAY;
        this.maxCompleteCount = 1;

        this.icon = Material.DIAMOND;
        this.iconLocked = Material.COAL;

        if (id != null) {
            setId(id);
        }
    }

    public Achievement(@Nonnull String name, @Nonnull String description) {
        this(null, name, description);
    }

    @Nonnull
    public Material getIcon() {
        return icon;
    }

    @Nonnull
    public Material getIconLocked() {
        return iconLocked;
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

        builder.addLore(getType());
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

                // Only show the next reward, or all rewards if fully complete
                long[] totalCoins = new long[2];
                long[] totalExp = new long[2];
                long[] totalRubies = new long[2];

                for (Map.Entry<Integer, Reward> entry : rewards.entrySet()) {
                    final Integer requirement = entry.getKey();
                    final Reward reward = entry.getValue();

                    if (reward instanceof CurrencyReward currencyReward) {
                        // If complete, store in gained rewards
                        if (completeCount >= requirement) {
                            totalCoins[0] += currencyReward.getCoins();
                            totalExp[0] += currencyReward.getExp();
                            totalRubies[0] += currencyReward.getRubies();
                        }
                        // Else store to 'to gain'
                        else {
                            totalCoins[1] += currencyReward.getCoins();
                            totalExp[1] += currencyReward.getExp();
                            totalRubies[1] += currencyReward.getRubies();
                        }
                    }
                }

                // If all rewards are claimed, show them all
                if (isCompleted) {
                    for (Reward reward : rewards.values()) {
                        if (reward instanceof CurrencyReward) {
                            continue;
                        }

                        reward.display(player, builder);
                    }

                    builder.addLore("");
                    builder.addLore("&7In addition to:");
                    addCoinsExpRubies(builder, totalCoins[0], totalExp[0], totalRubies[0]);
                }
                // Else display only the next reward and gained/to gain currency
                else {
                    // Display next reward
                    final Reward nextReward = getReward(nextComplete);

                    if (nextReward != null) {
                        builder.addLore("");
                        builder.addLore("&7Next Tier Rewards:");
                        nextReward.display(player, builder);
                    }

                    // Show gained rewards
                    if (totalCoins[0] > 0 || totalExp[0] > 0 || totalRubies[0] > 0) {
                        builder.addLore("");
                        builder.addLore("&7Gained");
                        addCoinsExpRubies(builder, totalCoins[0], totalExp[0], totalRubies[0]);
                    }

                    if (totalCoins[1] > 0 || totalExp[1] > 0 || totalRubies[1] > 0) {
                        builder.addLore("");
                        builder.addLore("&7To Gain:");
                        addCoinsExpRubies(builder, totalCoins[1], totalExp[1], totalRubies[1]);
                    }
                }
            }
        }

        if (isCompleted) {
            builder.addLore();
            builder.addLore("&a&lCompleted!");
            builder.addLore("&8" + new SimpleDateFormat("MMMM d'th' yyyy, HH:mm:ss").format(getCompletedAt(player)));
        }
    }

    @Nonnull
    public String getType() {
        return "Achievement";
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
     * Get the sole reward; or the empty reward.
     *
     * @return the sole reward; or the empty reward.
     */
    @Nonnull
    public Reward getRewardNonnull() {
        final Reward reward = getReward();

        if (reward != null) {
            return reward;
        }

        return Reward.EMPTY;
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

        return integers.length > 0 ? integers[0] : 1; // default to a a element or 1
    }

    public int getMaxTier() {
        return rewards.size();
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
        Chat.sendCenterMessage(player, GRADIENT);
        Chat.sendCenterMessage(player, "&a" + getName());
        Chat.sendMessage(player, "");

        PlayerLib.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.25f);
        PlayerLib.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 0.75f);
    }

    /**
     * Tries to complete achievement for player.
     *
     * @param player - Player to complete achievement for.
     * @return true if success, false if already completed.
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
     */
    public boolean isHidden() {
        return this instanceof HiddenAchievement;
    }

    /**
     * Returns true if this achievement can be completed multiple times.
     */
    public boolean isProgressive() {
        return this instanceof ProgressAchievement;
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

    public <T extends PlayerTrigger> Achievement setTrigger(Subscribe<T> sub, AchievementTrigger<T> trigger) {
        sub.subscribe(t -> {
            final Player player = t.player;

            if (isComplete(player)) { // don't check if already complete
                return;
            }

            if (trigger.test(t)) {
                complete(player);
            }
        });

        return this;
    }

    private void addCoinsExpRubies(ItemBuilder builder, long coins, long exp, long rubies) {
        builder.addLoreIf("&a+ &6" + coins + " Coins", coins > 0);
        builder.addLoreIf("&a+ &9" + exp + " Experience", exp > 0);
        builder.addLoreIf("&a+ &c" + rubies + " Rubies", rubies > 0);
    }

    @Nonnull
    private String checkmark(boolean condition) {
        return condition ? " &a✔" : "";
    }

}

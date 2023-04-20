package me.hapyl.fight.game.achievement;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.AchievementEntry;
import me.hapyl.fight.game.reward.CurrencyReward;
import me.hapyl.fight.game.reward.Reward;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public enum Achievements {

    //
    // READ BEFORE ADDING ACHIEVEMENT
    // 1. Make a good ENUM name, it should not be changed in the future.
    // 2. Make a good achievement name, something that is not too long.
    // 3. Make a good achievement description, how to get the achievement usually is advised.
    // 4. Use proper achievement class, Achievement for normal, ProgressAchievement for
    //    achievements that can be completed multiple times, HiddenAchievement for, well, hidden achievements.
    //

    PLAY_FIRST_GAME(new Achievement("So That's How It Is", "Play your first game.")
            .setReward(new CurrencyReward("").withCoins(500).withRubies(1))),

    TEST_PROGRESS_ACHIEVEMENT(new ProgressAchievement("Test Progress Achievement", "")
            .setReward(1, Reward.EMPTY)
            .setReward(5, Reward.EMPTY)
            .setReward(10, Reward.EMPTY)),

    TEST_HIDDEN_ACHIEVEMENT(new HiddenAchievement("WHAT", "HOW")),
    ;

    private static final Map<Category, List<Achievements>> BY_CATEGORY;

    static {
        BY_CATEGORY = Maps.newHashMap();

        for (Achievements value : values()) {
            BY_CATEGORY.compute(value.getAchievement().getCategory(), (category, list) -> {
                if (list == null) {
                    list = Lists.newArrayList();
                }

                list.add(value);
                return list;
            });
        }
    }

    private final Achievement achievement;

    Achievements(Achievement achievement) {
        this.achievement = achievement;
    }

    public Achievement getAchievement() {
        return achievement;
    }

    /**
     * Tries to complete achievement for player.
     *
     * @param player - Player to complete achievement for.
     * @return true if completed, false if already completed.
     */
    public boolean complete(Player player) {
        final PlayerDatabase database = PlayerDatabase.getDatabase(player);
        final AchievementEntry entry = database.getAchievementEntry();
        final int completeCount = entry.getCompleteCount(this);
        final int nextComplete = completeCount + 1;

        // If already completed, check if progress achievement
        if (completeCount > 0 && completeCount >= achievement.getMaxCompleteCount()) {
            return false;
        }

        entry.addCompleteCount(this);

        final Reward nextReward = achievement.getReward(nextComplete);

        if (nextReward == null) {
            return true;
        }

        entry.setCompletedAt(this, System.currentTimeMillis());

        achievement.onComplete(player);
        achievement.displayComplete(player);
        nextReward.grantReward(player);

        return true;
    }

    /**
     * Tries to complete achievement for all players.
     *
     * @param players - Players to complete achievement for.
     */
    public void completeAll(Collection<Player> players) {
        players.forEach(this::complete);
    }

    public boolean isCompleted(Player player) {
        return PlayerDatabase.getDatabase(player).getAchievementEntry().isCompleted(this);
    }

    public int getCompleteCount(Player player) {
        return PlayerDatabase.getDatabase(player).getAchievementEntry().getCompleteCount(this);
    }

    public boolean isHidden() {
        return achievement instanceof HiddenAchievement;
    }

    /**
     * Returns copy of all achievements in a category.
     *
     * @param category - Category to get achievements from.
     * @return List of achievements in category.
     */
    public static LinkedList<Achievements> byCategory(Category category) {
        return Lists.newLinkedList(BY_CATEGORY.getOrDefault(category, Lists.newArrayList()));
    }
}

package me.hapyl.fight.game.achievement;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.AchievementEntry;
import org.bukkit.entity.Player;

import java.util.Collection;

public enum Achievements {

    //
    // READ BEFORE ADDING ACHIEVEMENT
    // 1. Make a good ENUM name, it should not be changed in the future.
    // 2. Make a good achievement name, something that is not too long.
    // 3. Make a good achievement description, how to get the achievement usually is advised.
    // 4. Use proper achievement class, Achievement for normal, ProgressAchievement for
    //    achievements that can be completed multiple times, HiddenAchievement for, well, hidden achievements.
    //

    PLAY_FIRST_GAME(new Achievement("So That's How It Is", "Play your first game.")),
    ;

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

        // If already completed, check if progress achievement
        if (completeCount > 0) {
            if (achievement instanceof ProgressAchievement progressAchievement) {
                if (completeCount >= progressAchievement.getMaxCompleteCount()) {
                    return false;
                }

                complete0(player);
                return true;
            }
            return false;
        }

        complete0(player);
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

    private void complete0(Player player) {
        final PlayerDatabase database = PlayerDatabase.getDatabase(player);
        final AchievementEntry entry = database.getAchievementEntry();
        final int completeCount = entry.getCompleteCount(this);
        final int nextComplete = completeCount + 1;

        entry.addCompleteCount(this);

        if (achievement instanceof ProgressAchievement progressAchievement) {
            final ProgressTier nextTier = progressAchievement.getTier(nextComplete);
            if (nextTier == null) {
                return;
            }

            nextTier.reward().grantReward(player);
        }

        achievement.onComplete(player);
        achievement.displayComplete(player);
    }
}

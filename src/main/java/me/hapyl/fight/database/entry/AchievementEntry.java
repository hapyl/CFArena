package me.hapyl.fight.database.entry;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;
import me.hapyl.fight.game.achievement.Achievement;

public class AchievementEntry extends PlayerDatabaseEntry {

    public AchievementEntry(PlayerDatabase playerDatabase) {
        super(playerDatabase);
    }

    /**
     * Returns the amount of times the player has completed this achievement.
     *
     * @param achievement - Achievement to check.
     * @return number of times completed
     */
    public int getCompleteCount(Achievement achievement) {
        return getValue("achievement.%s.complete_count".formatted(achievement.getKey()), 0);
    }

    /**
     * Adds one complete count to achievement.
     *
     * @param achievement - Achievement to add complete count to.
     */
    public void addCompleteCount(Achievement achievement) {
        setCompleteCount(achievement, getCompleteCount(achievement) + 1);
    }

    /**
     * Removes one complete count from achievement.
     *
     * @param achievement - Achievement to remove complete count from.
     */
    public void subtractCompleteCount(Achievement achievement) {
        setCompleteCount(achievement, getCompleteCount(achievement) - 1);
    }

    /**
     * Returns true if player has completed this achievement at least once.
     *
     * @param achievement - Achievement to check.
     * @return true if completed at least once
     */
    public boolean hasCompletedAtLeastOnce(Achievement achievement) {
        return getCompleteCount(achievement) > 0;
    }

    public void reset(Achievement achievement) {
        setCompleteCount(achievement, 0);
    }

    public void setCompletedAt(Achievement achievement, long time) {
        setValue("achievement.%s.completed_at".formatted(achievement.getKey()), time);
    }

    public long getCompletedAt(Achievement achievement) {
        return getValue("achievement.%s.completed_at".formatted(achievement.getKey()), 0L);
    }

    public void setCompleteCount(Achievement achievement, int count) {
        if (count <= 0) {
            setValue("achievement.%s".formatted(achievement.getKey()), null);
            return;
        }

        setValue("achievement.%s.complete_count".formatted(achievement.getKey()), count);
    }

}

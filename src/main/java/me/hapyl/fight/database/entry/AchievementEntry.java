package me.hapyl.fight.database.entry;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;
import me.hapyl.fight.game.achievement.Achievements;

public class AchievementEntry extends PlayerDatabaseEntry {

    public AchievementEntry(PlayerDatabase playerDatabase) {
        super(playerDatabase);
    }

    /**
     * Returns the amount of times the player has completed this achievement.
     *
     * @param achievement - Achievement to check.
     * @return amount of times completed
     */
    public int getCompleteCount(Achievements achievement) {
        return getValue("achievement.%s.complete_count".formatted(achievement.name()), 0);
    }

    /**
     * Adds one complete count to achievement.
     *
     * @param achievements - Achievement to add complete count to.
     */
    public void addCompleteCount(Achievements achievements) {
        setCompleteCount(achievements, getCompleteCount(achievements) + 1);
    }

    /**
     * Removes one complete count from achievement.
     *
     * @param achievements - Achievement to remove complete count from.
     */
    public void subtractCompleteCount(Achievements achievements) {
        setCompleteCount(achievements, getCompleteCount(achievements) - 1);
    }

    /**
     * Returns true if player has completed this achievement at least once.
     *
     * @param achievement - Achievement to check.
     * @return true if completed at least once
     */
    public boolean isCompleted(Achievements achievement) {
        return getCompleteCount(achievement) > 0;
    }

    private void setCompleteCount(Achievements achievements, int count) {
        if (count <= 0) {
            setValue("achievement.%s".formatted(achievements.name()), null);
            return;
        }

        setValue("achievement.%s.complete_count".formatted(achievements.name()), count);
        setValue("achievement.%s.completed_at".formatted(achievements.name()), System.currentTimeMillis());
    }

}

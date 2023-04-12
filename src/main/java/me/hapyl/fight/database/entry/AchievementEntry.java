package me.hapyl.fight.database.entry;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;
import me.hapyl.fight.game.achievement.Achievements;
import org.bson.Document;

// FIXME (hapyl): 012, Apr 12, 2023: TESTME (hapyl)
public class AchievementEntry extends PlayerDatabaseEntry {

    public AchievementEntry(PlayerDatabase playerDatabase) {
        super(playerDatabase);
    }

    public int getCompleteCount(Achievements achievement) {
        return getValue("achievement." + achievement.name(), 0);
    }

    public void addCompleteCount(Achievements achievements) {
        final int complete = getCompleteCount(achievements);

        setValue("achievement." + achievements.name(), complete + 1);
    }

    public boolean isCompleted(Achievements achievement) {
        return getCompleteCount(achievement) > 0;
    }

    private Document getAchievements() {
        return getConfig().get("achievement", new Document());
    }

}

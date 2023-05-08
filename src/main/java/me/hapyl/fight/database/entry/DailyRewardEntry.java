package me.hapyl.fight.database.entry;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.PlayerDatabaseEntry;
import me.hapyl.fight.game.reward.DailyReward;

public class DailyRewardEntry extends PlayerDatabaseEntry {
    public DailyRewardEntry(PlayerDatabase playerDatabase) {
        super(playerDatabase);
    }

    public boolean canClaim() {
        final long lastDaily = lastDaily();
        return lastDaily == 0L || System.currentTimeMillis() >= (lastDaily + DailyReward.MILLIS_WHOLE_DAY);
    }

    public long nextDaily() {
        return DailyReward.MILLIS_WHOLE_DAY - (System.currentTimeMillis() - lastDaily());
    }

    public long lastDaily() {
        return getValue("reward.daily.last", 0L);
    }

    public int getStreak() {
        return getValue("reward.daily.streak", 0);
    }

    /**
     * @return the next streak
     */
    public int increaseStreak() {
        final int value = getStreak() + 1;
        setValue("reward.daily.streak", value);

        return value;
    }

    public void setLastDaily(long l) {
        setValue("reward.daily.last", l);
    }

    public void markLastDailyReward() {
        setLastDaily(System.currentTimeMillis());
    }

    public boolean isBonusReward() {
        final int streak = getStreak();
        return streak > 0 && streak % 7 == 0;
    }
}

package me.hapyl.fight.game.challenge;

import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.util.Described;

import javax.annotation.Nonnull;
import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

public class Challenge implements Described {

    private static final long ONE_DAY_IN_MILLIS = 86_400_000;
    private static final TimeZone THE_TIME_ZONE = TimeZone.getTimeZone("GMT");

    private final String name;
    private final String description;

    private ChallengeRarity rarity;

    private int min;
    private int max;

    public Challenge(String name, String description) {
        this.name = name;
        this.description = description;
        this.rarity = ChallengeRarity.COMMON;
        this.min = 1;
        this.max = 5;
    }

    @Nonnull
    public ChallengeRarity getRarity() {
        return rarity;
    }

    public Challenge setRarity(ChallengeRarity rarity) {
        this.rarity = rarity;
        return this;
    }

    public boolean canGenerate(@Nonnull PlayerProfile profile) {
        return true;
    }

    public int getMin() {
        return min;
    }

    public Challenge setMin(int min) {
        this.min = min;
        return this;
    }

    public int getMax() {
        return max;
    }

    public Challenge setMax(int max) {
        this.max = max;
        return this;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Nonnull
    @Deprecated
    @Override
    public String getDescription() {
        return description;
    }

    @Nonnull
    public String getDescription(@Nonnull PlayerChallenge challenge) {
        return description.replace("{}", "" + challenge.getGoal());
    }

    public int generateRandomGoal() {
        if (min == max) {
            return min;
        }

        return new Random().nextInt(min, max + 1);
    }

    /**
     * Gets the current day, formatted as such:
     * <pre>
     *     YEAR + DAYOFYEAR
     * </pre>
     * Example for Feb 25th 2024:
     * <pre>
     *     24056
     * </pre>
     *
     * @return the current day.
     * @implNote Technically just getting the day is enough, but in micro case where
     * a player left and joined the same day exactly one year later
     * (Which is probably never going to happen.), doing this little math.
     */
    public static int getCurrentDay() {
        final Calendar calendar = Calendar.getInstance(THE_TIME_ZONE);

        final int year = calendar.get(Calendar.YEAR);
        final int day = calendar.get(Calendar.DAY_OF_YEAR);

        return (year - 2000) * 1000 + day;
    }

    /**
     * Gets the time in millis until the next day's midnight.
     *
     * @return the time in millis until the next day's midnight.
     */
    public static long getTimeUntilReset() {
        final Calendar calendar = Calendar.getInstance(THE_TIME_ZONE);
        final long millis = System.currentTimeMillis();

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        calendar.add(Calendar.DAY_OF_MONTH, 1);

        final long nextDayInMillis = calendar.getTimeInMillis();
        return nextDayInMillis - millis;
    }

}

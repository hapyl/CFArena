package me.hapyl.fight.game.profile.data;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.achievement.Achievements;
import me.hapyl.fight.game.profile.PlayerProfile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.BiFunction;

public class AchievementData {

    private final PlayerProfile profile;
    private final Achievements achievement;
    private final Map<Type<?>, Object> objectMap;

    public AchievementData(PlayerProfile profile, Achievements achievement) {
        this.profile = profile;
        this.achievement = achievement;
        this.objectMap = Maps.newHashMap();
    }

    /**
     * Gets the value for the given type.
     *
     * @param type - Type.
     * @return a value assigned to the type; or null.
     */
    @Nullable
    public <T> T get(@Nonnull Type<T> type) {
        return getOrDefault(type, null);
    }

    /**
     * Gets the value for the given type; or default value.
     *
     * @param type - Type.
     * @param def  - Default value.
     * @return the value for the given type; or default value.
     */
    public <T> T getOrDefault(@Nonnull Type<T> type, @Nullable T def) {
        final Object obj = objectMap.get(type);

        return obj == null ? def : type.get(obj);
    }

    /**
     * Sets the value for the given type.
     *
     * @param type  - Type.
     * @param value - New value.
     * @return the previous value.
     */
    @Nullable
    public <T> T set(Type<T> type, T value) {
        final Object previous = objectMap.put(type, value);

        return previous == null ? null : type.get(previous);
    }

    /**
     * Increments an integer and returns the <b>new</b> value.
     *
     * @param type  - Increment type.
     * @param value - Increment.
     * @return the new value.
     */
    public int increment(@Nonnull Type<Integer> type, int value) {
        return increment(type, value, Integer::sum);
    }

    /**
     * Increments a long and returns the <b>new</b> value.
     *
     * @param type  - Increment type.
     * @param value - Increment.
     * @return the new value.
     */
    public long increment(@Nonnull Type<Long> type, long value) {
        return increment(type, value, Long::sum);
    }

    /**
     * Increments a double and returns the <b>new</b> value.
     *
     * @param type  - Increment type.
     * @param value - Increment.
     * @return the new value.
     */
    public double increment(@Nonnull Type<Double> type, double value) {
        return increment(type, value, Double::sum);
    }

    /**
     * Increments a float and returns the <b>new</b> value.
     *
     * @param type  - Increment type.
     * @param value - Increment.
     * @return the new value.
     */
    public float increment(@Nonnull Type<Float> type, float value) {
        return increment(type, value, Float::sum);
    }

    /**
     * Removes the value for the given type.
     *
     * @param type - Type.
     * @return true if a type had a value assigned to it; false otherwise.
     */
    public <T> boolean remove(@Nonnull Type<T> type) {
        return objectMap.remove(type) != null;
    }


    /**
     * Gets the value or default and sets a new value.
     *
     * @param type     - Type.
     * @param def      - Default.
     * @param newValue - New value.
     * @return the previous value or default.
     */
    @Nonnull
    public <T> T getOrDefaultAndSet(@Nonnull Type<T> type, @Nonnull T def, @Nonnull T newValue) {
        final T currentValue = getOrDefault(type, def);

        set(type, newValue);

        return currentValue == null ? def : currentValue;
    }

    /**
     * Clears the map.
     */
    public void clear() {
        objectMap.clear();
    }

    /**
     * Checks for expiration and clears if expired; does nothing otherwise.
     * <b>This updates the {@link Type#LAST_USE} value!</b>
     *
     * @param expireAfter - Expiration in millis.
     */
    public AchievementData checkExpire(long expireAfter) {
        final long lastModified = getOrDefault(Type.LAST_USE, System.currentTimeMillis());
        final long difference = System.currentTimeMillis() - lastModified;

        if (difference >= expireAfter) {
            objectMap.clear();
        }

        set(Type.LAST_USE, System.currentTimeMillis());
        return this;
    }

    /**
     * Checks for expiration and clears if expired; does nothing otherwise.
     * <b>This updates the {@link Type#LAST_USE} value!</b>
     *
     * @param expireAfterSec - Expiration in seconds.
     */
    public AchievementData checkExpireSec(int expireAfterSec) {
        return checkExpire(expireAfterSec * 1000L);
    }

    public void completeAchievement() {
        achievement.complete(profile.getPlayer());
        objectMap.clear();
    }

    private <T extends Number> T increment(Type<T> type, T value, BiFunction<T, T, T> function) {
        final T currentValue = get(type);

        if (currentValue == null) {
            set(type, value);

            return value;
        }
        else {
            final T newValue = function.apply(currentValue, value);
            set(type, newValue);

            return newValue;
        }
    }
}

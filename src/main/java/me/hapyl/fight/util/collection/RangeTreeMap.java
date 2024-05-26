package me.hapyl.fight.util.collection;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
 * A special impl of a {@link TreeMap}.
 * <br>
 * This map always returns the closest value, unless empty.
 *
 * @param <K> - Key.
 * @param <V> - Value.
 */
public final class RangeTreeMap<K, V> extends TreeMap<K, V> {

    RangeTreeMap(Comparator<K> comparator) {
        super(comparator);
    }

    /**
     * Gets the lowest value.
     *
     * @return the lowest value.
     * @throws IllegalStateException if the map is empty.
     */
    public V lowest() {
        for (V value : values()) {
            return value;
        }

        throw new IllegalStateException("Cannot retrieve the lowest value from an empty map.");
    }

    /**
     * Gets the highest value.
     *
     * @return the highest value.
     * @throws IllegalStateException if the map is empty.
     */
    public V highest() {
        for (V value : descendingMap().values()) {
            return value;
        }

        throw new IllegalStateException("Cannot retrieve the highest value from an empty map.");
    }

    /**
     * Gets the value for the key or the first closest key.
     *
     * @param key the key whose associated value is to be returned
     * @return The value or {@link #highest()}.
     */
    @Override
    @SuppressWarnings("unchecked")
    public V get(@Nullable Object key) {
        if (key == null) {
            return highest();
        }

        final Comparator<? super K> comparator = comparator();

        for (Map.Entry<K, V> entry : entrySet()) {
            final K entryKey = entry.getKey();

            // Fuck whoever made 'get' take Object instead of a fucking generic.
            if (!entryKey.getClass().isInstance(key)) {
                return null;
            }

            if (comparator.compare((K) key, entryKey) <= 0) {
                return entry.getValue();
            }
        }

        return highest();
    }

    public static <V> RangeTreeMap<Double, V> ofDouble() {
        return new RangeTreeMap<>(Comparator.comparingDouble(a -> -a));
    }

    public static <V> RangeTreeMap<Integer, V> ofInt() {
        return new RangeTreeMap<>(Comparator.comparingInt(a -> -a));
    }

    public static <V> RangeTreeMap<Long, V> ofLong() {
        return new RangeTreeMap<>(Comparator.comparingLong(a -> -a));
    }

}

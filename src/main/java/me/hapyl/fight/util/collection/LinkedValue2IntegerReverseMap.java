package me.hapyl.fight.util.collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class LinkedValue2IntegerReverseMap<K> extends LinkedHashMap<K, Integer> {

    private LinkedValue2IntegerReverseMap() {
    }

    /**
     * Performs a for each iteration with a given consumer.
     *
     * @param consumer - {@link Consumer}.
     */
    public void forEach(@Nonnull Consumer<K> consumer) {
        int index = 0;
        for (Map.Entry<K, Integer> entry : entrySet()) {
            final K key = entry.getKey();
            final Integer value = entry.getValue();

            consumer.accept(index++, key, value);
        }
    }

    /**
     * Performs a for i iteration given number of times, regardless of map size.
     *
     * @param times    - How many times to perform iterations.
     * @param consumer - {@link NullableConsumer}.
     */
    public void forEach(int times, @Nonnull NullableConsumer<K> consumer) {
        int index = 0;

        for (Map.Entry<K, Integer> entry : entrySet()) {
            consumer.accept(index++, entry.getKey(), entry.getValue());
        }

        for (; index < times; index++) {
            consumer.accept(index, null, null);
        }
    }

    public static <K> LinkedValue2IntegerReverseMap<K> of(@Nonnull Map<K, Integer> map, int limit) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(limit)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedValue2IntegerReverseMap::new));
    }

    public static <K> LinkedValue2IntegerReverseMap<K> of(@Nonnull Map<K, Integer> map) {
        return of(map, Integer.MAX_VALUE);
    }

    public interface NullableConsumer<K> {
        /**
         * Accepts consumer values.
         * Note that both key and value <b>may</b> be null.
         *
         * @param index   - Index of the value.
         * @param k       - Key.
         * @param integer - Value.
         */
        void accept(int index, @Nullable K k, @Nullable Integer integer);
    }

    public interface Consumer<K> {
        /**
         * Accepts consumer values.
         *
         * @param index   - Index of the value.
         * @param k       - Key.
         * @param integer - Value.
         */
        void accept(int index, @Nonnull K k, @Nonnull Integer integer);
    }

}

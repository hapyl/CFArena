package me.hapyl.fight.util.collection;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class Map2Long<K> extends LinkedHashMap<K, Long> {

    public boolean check(@Nonnull BiFunction<K, Long, Boolean> checker) {
        for (Map.Entry<K, Long> entry : entrySet()) {
            final K key = entry.getKey();
            final Long value = entry.getValue();

            if (!checker.apply(key, value)) {
                return false;
            }
        }

        return true;
    }

    public int check2(@Nonnull BiFunction<K, Long, Integer> checker) {
        int i = 0;
        for (Map.Entry<K, Long> entry : entrySet()) {
            final K key = entry.getKey();
            final Long value = entry.getValue();

            i = (int) Math.max(checker.apply(key, value) / value, i);
        }

        return i;
    }

}

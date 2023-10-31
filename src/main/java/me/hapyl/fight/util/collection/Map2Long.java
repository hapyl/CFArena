package me.hapyl.fight.util.collection;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.function.BiFunction;

public class Map2Long<K> extends HashMap<K, Long> {

    public boolean check(@Nonnull BiFunction<K, Long, Boolean> checker) {
        for (Entry<K, Long> entry : entrySet()) {
            final K key = entry.getKey();
            final Long value = entry.getValue();

            if (!checker.apply(key, value)) {
                return false;
            }
        }

        return true;
    }
}

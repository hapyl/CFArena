package me.hapyl.fight.database;

import com.google.common.collect.Maps;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.Map;

public interface EnumMappedEntry<K extends Enum<K>, V> extends MappedEntry<K, V> {

    @Nonnull
    K[] enumValues();

    @Nonnull
    V getMappedValue(@Nonnull K k);

    default boolean mappedCondition(@Nonnull V v) {
        if (v instanceof Number i) {
            return i.longValue() > 0;
        }

        return false;
    }

    @Nonnull
    @Override
    default Map<K, V> mapped() {
        final LinkedHashMap<K, V> map = Maps.newLinkedHashMap();

        for (K value : enumValues()) {
            final V v = getMappedValue(value);
            if (mappedCondition(v)) {
                map.put(value, v);
            }
        }

        return map;
    }
}

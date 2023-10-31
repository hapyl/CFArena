package me.hapyl.fight.util.collection;

import java.util.LinkedHashMap;
import java.util.Map;

public class LimitedHashMap<K, V> extends LinkedHashMap<K, V> {

    private final int maxCapacity;

    public LimitedHashMap(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxCapacity;
    }
}

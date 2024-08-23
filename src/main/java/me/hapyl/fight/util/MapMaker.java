package me.hapyl.fight.util;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public final class MapMaker<K, V, M extends Map<K, V>> {

    private final M map;

    private MapMaker(M map) {
        this.map = map;
    }

    public MapMaker<K, V, M> put(@Nonnull K k, @Nonnull V v) {
        map.put(k, v);
        return this;
    }

    @Nonnull
    public Map<K, V> makeMap() {
        return map;
    }

    @Nonnull
    public M makeGenericMap() {
        return map;
    }

    @Nonnull
    public static <K, V> MapMaker<K, V, HashMap<K, V>> of() {
        return new MapMaker<>(new HashMap<>());
    }

    @Nonnull
    public static <K, V> MapMaker<K, V, LinkedHashMap<K, V>> ofLinkedHashMap() {
        return new MapMaker<>(new LinkedHashMap<>());
    }

    @Nonnull
    public static <K, V> MapMaker<K, V, TreeMap<K, V>> ofTreeNap() {
        return new MapMaker<>(new TreeMap<>());
    }

}
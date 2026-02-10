package me.hapyl.fight.util;

import org.apache.commons.lang3.NotImplementedException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * A {@link Map} implementation that allows reading but not changing or writing.
 *
 * @param <K> - Key.
 * @param <V> - Value.
 */
@Nonnull // map view should never return null, use MapView.empty()
public final class MapView<K, V> implements Map<K, V> {

    private final Map<K, V> hashMap;

    public MapView(@Nonnull Map<K, V> hashMap) {
        this.hashMap = hashMap;
    }

    @Override
    public int size() {
        return hashMap.size();
    }

    @Override
    public boolean isEmpty() {
        return hashMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return hashMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return hashMap.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return hashMap.get(key);
    }

    /**
     * @throws UnsupportedOperationException always
     * @deprecated unsupported
     */
    @Nullable
    @Override
    @Deprecated
    public V put(K key, V value) throws UnsupportedOperationException {
        throw uoe("put");
    }

    /**
     * @throws UnsupportedOperationException always
     * @deprecated unsupported
     */
    @Override
    @Deprecated
    public V remove(Object key) {
        throw uoe("remove");
    }

    /**
     * @throws UnsupportedOperationException always
     * @deprecated unsupported
     */
    @Override
    @Deprecated
    public void putAll(@Nonnull Map<? extends K, ? extends V> m) {
        throw uoe("putAll");
    }

    /**
     * @throws UnsupportedOperationException always
     * @deprecated unsupported
     */
    @Override
    @Deprecated
    public void clear() {
        throw uoe("clear");
    }

    @Nonnull
    @Override
    public Set<K> keySet() {
        return new HashSet<>(hashMap.keySet());
    }

    @Nonnull
    @Override
    public Collection<V> values() {
        return new ArrayList<>(hashMap.values());
    }

    @Nonnull
    @Override
    public Set<Entry<K, V>> entrySet() {
        return new HashSet<>(hashMap.entrySet());
    }

    private UnsupportedOperationException uoe(String name) {
        return new NotImplementedException(name + " is not supported in MapView!");
    }

    @Nonnull
    public static <K, V> MapView<K, V> of(@Nonnull Map<K, V> map) {
        return new MapView<>(map);
    }

    @Nonnull
    public static <K, V> MapView<K, V> ofOrEmpty(@Nullable Map<K, V> map) {
        return (map == null || map.isEmpty()) ? empty() : of(map);
    }

    @Nonnull
    public static <K, V> MapView<K, V> empty() {
        return new MapView<>(Map.of());
    }

}

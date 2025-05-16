package me.hapyl.fight.util;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.function.Function;

/**
 * A {@code null}-safe {@link LinkedHashMap} implementation, with a {@link #makeValue(Object)} method ensuring
 * calling {@link #get(Object)} always returns either an existing object or compute a new value.
 */
public abstract class NonnullLinkedHashMap<K, V> extends LinkedHashMap<K, V> {
    
    public NonnullLinkedHashMap() {
    }
    
    @Nonnull
    public abstract V makeValue(@Nonnull K k);
    
    @Override
    @Nonnull
    public final V get(@Nonnull Object key) {
        return super.computeIfAbsent((K) key, this::makeValue);
    }
    
    @Nonnull
    public static <K, V> NonnullLinkedHashMap<K, V> of(@Nonnull Function<K, V> fn) {
        return new NonnullLinkedHashMap<>() {
            @Nonnull
            @Override
            public V makeValue(@Nonnull K k) {
                return fn.apply(k);
            }
        };
    }
    
}

package me.hapyl.fight.cache;

import com.google.common.collect.Maps;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class Cache<T> {
    private final Map<String, T> cached;

    public Cache() {
        this.cached = Maps.newHashMap();
    }

    public boolean isCached(@Nonnull String name) {
        return cached.containsKey(name.toLowerCase());
    }

    public boolean cache(@Nonnull String name, @Nonnull T t) {
        return cached.put(name.toLowerCase(), t) == null;
    }

    @Nullable
    public T getCached(@Nonnull String name) {
        return cached.get(name.toLowerCase());
    }
}

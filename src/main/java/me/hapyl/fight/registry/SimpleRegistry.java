package me.hapyl.fight.registry;

import com.google.common.collect.Maps;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SimpleRegistry<T extends Keyed> implements Registry<T> {

    protected final Map<Key, T> registered;

    public SimpleRegistry() {
        this.registered = Maps.newLinkedHashMap(); // Actually, keep the order.
    }

    @Nullable
    @Override
    public T get(@Nonnull Key key) {
        return registered.get(key);
    }

    @Override
    public T register(@Nonnull T t) {
        final Key key = t.getKey();
        final T previousValue = registered.put(key, t);

        if (previousValue != null) {
            throw new IllegalArgumentException("Duplicate registration of " + key + "!");
        }

        return t;
    }

    @Override
    public boolean unregister(@Nonnull T t) {
        return registered.remove(t.getKey()) != null;
    }

    @Nonnull
    @Override
    public List<T> values() {
        return new ArrayList<>(registered.values());
    }

}

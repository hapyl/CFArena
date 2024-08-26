package me.hapyl.fight.registry;

import com.google.common.collect.Maps;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Map} based registry.
 * <br>
 * This registry does not permit duplicate registration.
 *
 * @param <T> - Item type.
 */
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
    @OverridingMethodsMustInvokeSuper
    public T register(@Nonnull T t) {
        final Key key = t.getKey();

        if (registered.containsKey(key)) {
            throw new IllegalArgumentException("Duplicate registration of '%s'!".formatted(key));
        }

        registered.put(key, t);
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

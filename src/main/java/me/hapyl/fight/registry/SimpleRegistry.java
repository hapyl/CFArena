package me.hapyl.fight.registry;

import com.google.common.collect.Maps;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SimpleRegistry<T extends Identified> implements Registry<T> {

    private final Map<EnumId, T> registered;

    public SimpleRegistry() {
        this.registered = Maps.newLinkedHashMap(); // Actually, keep the order.
    }

    @Nullable
    @Override
    public T get(@Nonnull EnumId id) {
        return registered.get(id);
    }

    @Override
    public boolean register(@Nonnull T t) {
        return registered.put(t.getId(), t) == null;
    }

    @Override
    public boolean unregister(@Nonnull T t) {
        return registered.remove(t.getId()) != null;
    }

    @Nonnull
    @Override
    public List<T> values() {
        return new ArrayList<>(registered.values());
    }

}

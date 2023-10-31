package me.hapyl.fight.registry;

import com.google.common.collect.Maps;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class SimpleRegistry<T extends EnumId> implements Registry<T> {

    private final Map<String, T> registered;
    private boolean isLocked;

    public SimpleRegistry() {
        registered = Maps.newHashMap();
        isLocked = false;
    }

    @Nullable
    @Override
    public T get(@Nonnull EnumId id) {
        return registered.get(id.getId());
    }

    @Override
    public boolean register(@Nonnull T t) {
        if (isLocked) {
            throw new IllegalStateException("Register in a locked registry.");
        }

        return registered.put(t.getId(), t) == null;
    }

    @Override
    public boolean unregister(@Nonnull T t) {
        if (isLocked) {
            throw new IllegalStateException("Unregister in a locked registry.");
        }
        return registered.remove(t.getId()) != null;
    }
}

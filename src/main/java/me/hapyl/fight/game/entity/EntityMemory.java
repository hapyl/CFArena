package me.hapyl.fight.game.entity;

import com.google.common.collect.Maps;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class EntityMemory {

    private final LivingGameEntity entity;
    private final Map<MemoryKey, Object> memory;

    public EntityMemory(LivingGameEntity entity) {
        this.entity = entity;
        this.memory = Maps.newHashMap();
    }

    @Nonnull
    public LivingGameEntity getEntity() {
        return entity;
    }

    @SuppressWarnings("unchecked")
    public <T> T remember(MemoryKey key, T toRemember) {
        final Object oldValue = memory.put(key, toRemember);

        if (oldValue == null) {
            return null;
        }

        final Class<?> clazz = toRemember.getClass();

        if (clazz.isInstance(oldValue)) {
            return (T) clazz.cast(oldValue);
        }

        return null;
    }

    @Nullable
    public Object forget(MemoryKey key) {
        return memory.remove(key);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public <T> T forget(MemoryKey key, @Nonnull T def) {
        final Object oldValue = forget(key);

        if (oldValue == null) {
            return def;
        }

        final Class<?> clazz = def.getClass();

        if (clazz.isInstance(oldValue)) {
            return (T) clazz.cast(oldValue);
        }

        return def;
    }

    @Nonnull
    public <T> T forget(MemoryKey key, Class<T> type, @Nonnull T def) {
        final Object oldValue = forget(key);

        if (!type.isInstance(oldValue)) {
            return def;
        }

        return type.cast(oldValue);
    }

    public void forgetEverything() {
        memory.clear();
    }

}

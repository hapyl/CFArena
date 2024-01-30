package me.hapyl.fight.game.entity;

import com.google.common.collect.Maps;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * An {@link LivingGameEntity} "memory" implementation.
 * <p>
 * Used to remember objects.
 * Memory is cleared upon entity death/respawn.
 */
public class EntityMemory {

    private final LivingGameEntity entity;
    private final Map<MemoryKey, Object> memory;

    public EntityMemory(LivingGameEntity entity) {
        this.entity = entity;
        this.memory = Maps.newHashMap();
    }

    /**
     * Gets the {@link LivingGameEntity} this memory belongs to.
     *
     * @return the entity this memory belongs to.
     */
    @Nonnull
    public LivingGameEntity getEntity() {
        return entity;
    }

    /**
     * Remembers an {@link Object} with the given {@link MemoryKey}.
     *
     * @param key        - Key.
     * @param toRemember - Object to remember.
     * @return the previous object entity remembers at the same key.
     */
    @SuppressWarnings("unchecked")
    public <T> Object remember(@Nonnull MemoryKey key, @Nonnull T toRemember) {
        return memory.put(key, toRemember);
    }

    /**
     * Gets the raw {@link Object} by the given {@link MemoryKey} and forgets (removes) it.
     *
     * @param key - Key.
     * @return the raw object.
     */
    @Nullable
    public Object forget(@Nonnull MemoryKey key) {
        return memory.remove(key);
    }

    /**
     * Gets the <code>T</code> by the given {@link MemoryKey} and forgets (removes) it.
     *
     * @param key - Key.
     * @param def - Defaults.
     * @return the T by the given memory key.
     */
    @Nonnull
    @SuppressWarnings("unchecked")
    public <T> T forget(@Nonnull MemoryKey key, @Nonnull T def) {
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

    @Nullable
    public <T> T forget(@Nonnull MemoryKey key, @Nonnull Class<T> type) {
        return forget(key, type, null);
    }

    /**
     * Gets the <code>T</code> by the given {@link MemoryKey}, casts it to the given {@link Class} and forgets (removes) it.
     *
     * @param key  - Key.
     * @param type - Type.
     * @param def  - Default.
     * @return the <code>T</code> by the given memory key cast to the type.
     */
    public <T> T forget(@Nonnull MemoryKey key, @Nonnull Class<T> type, T def) {
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

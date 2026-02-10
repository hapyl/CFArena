package me.hapyl.fight.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Mimics a singleton, {@link Enum} like behaviour.
 */
public abstract class SingletonBehaviour {

    protected static final Map<Class<?>, Integer> SINGLETONS = new HashMap<>();

    /**
     * Throws {@link CloneNotSupportedException}.
     *
     * @throws CloneNotSupportedException always
     */
    @Deprecated(forRemoval = true)
    public final Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Singleton cannot be cloned.");
    }

    /**
     * Returns {@code true} if the given {@link Object} matches the singleton of this object.
     *
     * @param object - Object to check.
     * @return true if the given object matches the singleton of this object.
     */
    public final boolean equals(@Nullable Object object) {
        return this == object;
    }

    @Override
    public int hashCode() {
        throw new IllegalStateException("Singleton must implement hashCode()");
    }

    protected static void instantiate(@Nonnull Object reference) {
        final Class<?> clazz = reference.getClass();
        final int hashCode = reference.hashCode();

        if (SINGLETONS.containsKey(clazz)) {
            throw new IllegalArgumentException("Duplicate singleton instantiation! (%s belongs to %s)".formatted(clazz.getSimpleName(), hashCode));
        }

        SINGLETONS.put(clazz, hashCode);
    }
}

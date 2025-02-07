package me.hapyl.fight.database.serialize;

import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.fight.infraction.HexID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents {@link MongoWrapper}s for named types that needs to be converted to and from a {@link String}.
 */
public final class MongoWrappers {

    private static final Map<Class<?>, MongoWrapper<?>> WRAPPERS;

    static {
        WRAPPERS = new HashMap<>();

        ////////////////////////////
        // Register wrappers here //
        ////////////////////////////

        register(UUID.class, MongoWrapper.of(UUID::toString, BukkitUtils::getUUIDfromString));
        register(HexID.class, MongoWrapper.of(HexID::toString, HexID::fromString));
    }

    /**
     * Gets a {@link MongoWrapper} for the given {@link Class}.
     *
     * @param clazz - Class to get the wrapper for.
     * @return a wrapper for the given class or <code>null</code> if it doesn't exist.
     */
    @Nullable
    public static MongoWrapper<Object> get(@Nonnull Class<?> clazz) {
        return getExact(clazz, Object.class);
    }

    /**
     * Attempts to get a {@link MongoWrapper} for the given {@link Class}, or registers a new {@link MongoWrapper} if it doesn't exist.
     *
     * @param clazz      - Class to get and register the wrapper for.
     * @param wrapper - Wrapper to register if it doesn't exist.
     * @return a wrapper for the given class.
     */
    @Nonnull
    @SuppressWarnings("unchecked")
    public static <T> MongoWrapper<Object> getOrRegister(@Nonnull Class<T> clazz, @Nonnull MongoWrapper<T> wrapper) {
        final MongoWrapper<Object> existingWrapper = get(clazz);

        if (existingWrapper != null) {
            return existingWrapper;
        }

        register(clazz, wrapper);
        return (MongoWrapper<Object>) wrapper;
    }

    /**
     * Gets a {@link MongoWrapper} for the given {@link Class} with the known type.
     *
     * @param clazz      - Class to get the wrapper for.
     * @param exactClass - Class of the wrapper object.
     * @return a wrapper for the given class a <code>null</code> if it doesn't exist.
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> MongoWrapper<T> getExact(@Nonnull Class<?> clazz, @Nonnull Class<T> exactClass) {
        return (MongoWrapper<T>) WRAPPERS.get(clazz);
    }

    /**
     * Registers a {@link MongoWrapper} for the given {@link Class}.
     * <br>
     * Note that registering outside this class might not work because of how class loading works.
     * It is recommended to register in the <code>static</code> block or use {@link #getOrRegister(Class, MongoWrapper)}.
     *
     * @param clazz   - Class to register the wrapper to.
     * @param wrapper - Wrapper to register.
     */
    public static <T> void register(@Nonnull Class<T> clazz, @Nonnull MongoWrapper<T> wrapper) {
        if (WRAPPERS.containsKey(clazz)) {
            throw new IllegalStateException("Wrapper for %s is already registered!".formatted(clazz.getSimpleName()));
        }

        // Make sure it's not a primitive wrapper nor string
        if (clazz.isPrimitive() || clazz == String.class) {
            throw new IllegalArgumentException(clazz.getSimpleName() + " cannot have a wrapper!");
        }

        WRAPPERS.put(clazz, wrapper);
    }

}

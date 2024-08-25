package me.hapyl.fight.registry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public interface Registry<T extends Keyed> {

    /**
     * Gets the element by its {@link Key} or null if not registered.
     *
     * @param key - {@link Key}.
     * @return the element or null if not registered.
     */
    @Nullable
    T get(@Nonnull Key key);

    /**
     * Gets the element by its {@link Key} from a string Id; or null if not registered.
     *
     * @param string - String Id. Will be forced to uppercase.
     * @return the element by its {@link Key} from a string Id.
     * @throws IllegalStateException if the string does not match {@link Key#PATTERN}.
     */
    @Nullable
    default T get(@Nonnull String string) throws IllegalStateException {
        final Key key = Key.ofStringOrNull(string);

        return key != null ? get(key) : null;
    }

    /**
     * Gets an optional of the element by its {@link Key}.
     *
     * @param key - {@link Key}.
     * @return the optional of the element.
     */
    @Nonnull
    default Optional<T> getOptional(@Nonnull Key key) {
        return Optional.ofNullable(get(key));
    }

    /**
     * Attempts to register the item.
     *
     * @param t - Item to register.
     * @return the registered item.
     */
    T register(@Nonnull T t);

    /**
     * Attempts to register the item.
     *
     * @param key - Key to register to.
     * @param fn  - Function on how to create the registry item.
     * @return the registered item.
     */
    default T register(@Nonnull String key, @Nonnull KeyFunction<T> fn) {
        final T t = fn.apply(Key.ofString(key));

        register(t);
        return t;
    }

    /**
     * Attempts to unregister the item.
     *
     * @param t - Item to unregister.
     * @return true if unregistered; false otherwise.
     */
    boolean unregister(@Nonnull T t);

    /**
     * Gets a copy of all registered values.
     *
     * @return a copy of all registered values.
     */
    @Nonnull
    List<T> values();

}

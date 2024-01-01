package me.hapyl.fight.registry;

import me.hapyl.fight.fastaccess.FastAccessRegistry;
import me.hapyl.fight.game.artifact.ArtifactRegistry;
import me.hapyl.fight.item.ItemRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public interface Registry<T extends EnumId> {

    ItemRegistry ITEM_REGISTRY = new ItemRegistry();
    FastAccessRegistry FAST_ACCESS = new FastAccessRegistry();
    ArtifactRegistry ARTIFACTS = new ArtifactRegistry();

    /**
     * Gets the element by its {@link EnumId} or null if not registered.
     *
     * @param id - {@link EnumId}.
     * @return the element or null if not registered.
     */
    @Nullable
    T get(@Nonnull EnumId id);

    /**
     * Gets the element by its {@link EnumId} from a string Id; or null if not registered.
     *
     * @param id - String Id. Will be forced to uppercase.
     * @return the element by its {@link EnumId} from a string Id.
     * @throws IllegalStateException if the string does not match {@link EnumId#PATTERN}.
     */
    @Nullable
    default T get(@Nonnull String id) throws IllegalStateException {
        id = id.toUpperCase();

        if (!EnumId.PATTERN.matcher(id).matches()) {
            return null;
        }

        return get(new EnumId(id));
    }

    /**
     * Gets an optional of the element by its {@link EnumId}.
     *
     * @param id - {@link EnumId}.
     * @return the optional of the element.
     */
    @Nonnull
    default Optional<T> getOptional(@Nonnull EnumId id) {
        return Optional.ofNullable(get(id));
    }

    /**
     * Attempts to register the item.
     *
     * @param t - Item to register.
     * @return true if registered; false otherwise.
     */
    boolean register(@Nonnull T t);

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

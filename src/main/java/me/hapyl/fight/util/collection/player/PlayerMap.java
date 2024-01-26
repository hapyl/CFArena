package me.hapyl.fight.util.collection.player;

import me.hapyl.fight.game.entity.GamePlayer;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A {@link Map} interface with {@link GamePlayer} key.
 *
 * @param <V> - Value.
 */
public interface PlayerMap<V> extends Map<GamePlayer, V> {

    /**
     * Removes the value if the condition is true.
     *
     * @param key - Key.
     * @param fn  - Condition function.
     * @return true if the key was removed; false otherwise.
     */
    default boolean removeIf(@Nonnull GamePlayer key, @Nonnull Function<V, Boolean> fn) {
        final V v = get(key);

        if (v != null && fn.apply(v)) {
            remove(key);
            return true;
        }

        return false;
    }

    /**
     * Removes the value and applies the given consumer to it.
     *
     * @param key      - Key.
     * @param consumer - Consumer.
     */
    default void removeAnd(@Nonnull GamePlayer key, @Nonnull Consumer<V> consumer) {
        final V value = remove(key);

        if (value != null) {
            consumer.accept(value);
        }
    }

    /**
     * Performs a {@link #forEach(BiConsumer)} on this {@link PlayerMap} and {@link #clear()} it after.
     *
     * @param consumer - Consumer.
     */
    default void forEachAndClear(@Nonnull Consumer<V> consumer) {
        forEach((k, v) -> {
            consumer.accept(v);
        });
        clear();
    }

    default <T> void forEach(@Nonnull Class<T> vClazz, @Nonnull BiConsumer<GamePlayer, T> consumer) {
        forEach((player, v) -> {
            if (!vClazz.isInstance(v)) {
                throw new IllegalArgumentException(v + " is not an instance of " + vClazz.getSimpleName() + "!");
            }

            consumer.accept(player, vClazz.cast(v));
        });
    }

    // *==* Static Members *==* //

    /**
     * Creates a new {@link PlayerHashMap}.
     *
     * @return a new player hash map.
     */
    @Nonnull
    static <V> PlayerHashMap<V> newMap() {
        return new PlayerHashMap<>();
    }

    /**
     * Creates a new {@link ConcurrentPlayerMap}.
     *
     * @return a new concurrent player hash map.
     */
    @Nonnull
    static <V> ConcurrentPlayerMap<V> newConcurrentMap() {
        return new ConcurrentPlayerMap<>();
    }

    /**
     * Creates a new {@link LinkedPlayerMap}.
     *
     * @return a new linked player hash map.
     */
    @Nonnull
    static <V> LinkedPlayerMap<V> newLinkedMap() {
        return new LinkedPlayerMap<>();
    }

    @Nonnull
    static <A, B> PlayerMultiMap<A, B> newMultiMap() {
        return new PlayerMultiMap<>();
    }

    static int clearAll(@Nonnull PlayerMap<?>... maps) {
        int cleared = 0;

        for (PlayerMap<?> map : maps) {
            map.clear();
            cleared++;
        }

        return maps.length - cleared;
    }

}

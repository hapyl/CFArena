package me.hapyl.fight.util;

import com.google.common.collect.Lists;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Allows for static map computations.
 */
public final class Compute {

    @Nonnull
    public static <K> BiFunction<K, Integer, Integer> intAdd() {
        return intAdd(1);
    }

    @Nonnull
    public static <K> BiFunction<K, Integer, Integer> intAdd(int addition) {
        return (k, integer) -> {
            if (integer == null) {
                return 0;
            }

            return integer + addition;
        };
    }

    @Nonnull
    public static <K> BiFunction<K, Integer, Integer> intSubtract() {
        return intSubtract(1);
    }

    @Nonnull
    public static <K> BiFunction<K, Integer, Integer> intSubtract(int subtraction) {
        return (k, integer) -> {
            if (integer == null) {
                return 0;
            }

            return integer - subtraction;
        };
    }

    @Nonnull
    public static <K> BiFunction<K, Long, Long> longSubtract(long subtraction) {
        return (k, aLong) -> aLong == null ? 0 : aLong - subtraction;
    }

    @Nonnull
    public static <K, V> BiFunction<K, List<V>, List<V>> listAdd(@Nonnull V v) {
        return collectionAdd(v, fn -> Lists.newArrayList());
    }

    @Nonnull
    public static <K, V> BiFunction<K, List<V>, List<V>> listRemove(@Nonnull V v) {
        return collectionRemove(v, fn -> Lists.newArrayList());
    }

    @Nonnull
    public static <K, V, C extends Collection<V>> BiFunction<K, C, C> collectionAdd(@Nonnull V v, @Nonnull Function<K, C> newFn) {
        return (k, c) -> {
            (c = c != null ? c : newFn.apply(k)).add(v);

            return c;
        };
    }

    @Nonnull
    public static <K, V, C extends Collection<V>> BiFunction<K, C, C> collectionRemove(@Nonnull V v, @Nonnull Function<K, C> newFn) {
        return (k, c) -> {
            (c = c != null ? c : newFn.apply(k)).remove(v);

            return c;
        };
    }

    @Nonnull
    public static <K, V> BiFunction<K, V, V> nullable(@Nonnull Function<K, V> ifNull, @Nonnull Function<V, V> ifNotNull) {
        return (k, v) -> v == null ? ifNull.apply(k) : ifNotNull.apply(v);
    }


}

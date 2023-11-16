package me.hapyl.fight.util;

import com.google.common.collect.Lists;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

// TODO (hapyl): 016, Nov 16: This should really be in EternaAPI
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
    public static <K, V, C extends Collection<V>> BiFunction<K, C, C> collectionAdd(V toAdd, Function<K, C> newColl) {
        return (k, list) -> {
            if (list == null) {
                list = newColl.apply(k);
            }

            list.add(toAdd);
            return list;
        };
    }

    @Nonnull
    public static <K, V, C extends Collection<V>> BiFunction<K, C, C> collectionRemove(V toAdd, Function<K, C> newColl) {
        return (k, list) -> {
            if (list == null) {
                list = newColl.apply(k);
            }

            list.remove(toAdd);
            return list;
        };
    }

    @Nonnull
    public static <K, V> BiFunction<K, List<V>, List<V>> listAdd(V toAdd) {
        return collectionAdd(toAdd, fn -> Lists.newArrayList());
    }

    @Nonnull
    public static <K, V> BiFunction<K, List<V>, List<V>> listRemove(V toAdd) {
        return collectionRemove(toAdd, fn -> Lists.newArrayList());
    }

}

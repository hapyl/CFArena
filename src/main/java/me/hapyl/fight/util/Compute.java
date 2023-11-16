package me.hapyl.fight.util;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;

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


}

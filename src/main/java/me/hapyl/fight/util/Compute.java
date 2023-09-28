package me.hapyl.fight.util;

import java.util.function.BiFunction;

public final class Compute {

    public static <K> BiFunction<K, Integer, Integer> intAdd() {
        return intAdd(1);
    }

    public static <K> BiFunction<K, Integer, Integer> intAdd(int addition) {
        return (k, integer) -> {
            if (integer == null) {
                return 0;
            }

            return integer + addition;
        };
    }

    public static <K> BiFunction<K, Integer, Integer> intSubtract() {
        return intSubtract(1);
    }

    public static <K> BiFunction<K, Integer, Integer> intSubtract(int subtraction) {
        return (k, integer) -> {
            if (integer == null) {
                return 0;
            }

            return integer - subtraction;
        };
    }


}

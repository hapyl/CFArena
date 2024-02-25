package me.hapyl.fight.util;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.function.Function;

public final class Comparators {

    @Nonnull
    public static <T> Comparator<T> comparingBool(@Nonnull Function<T, Boolean> fn) {
        return (o1, o2) -> {
            final Boolean boolA = fn.apply(o1);
            final Boolean boolB = fn.apply(o2);

            return boolB ? 1 : boolA ? -1 : 0;
        };
    }

}

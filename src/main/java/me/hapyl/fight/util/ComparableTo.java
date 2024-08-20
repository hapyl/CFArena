package me.hapyl.fight.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ComparableTo<T> extends Comparable<T> {

    default boolean isEquals(@Nonnull T t) {
        return compareTo(t) == 0;
    }

    static <T> int comparingObjects(@Nullable T a, @Nullable T b) {
        if (a == null || b == null) {
            return -1;
        }

        return a == b ? 0 : -1;
    }

}

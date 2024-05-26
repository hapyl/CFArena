package me.hapyl.fight.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;

public class ParameterPrecondition {

    private ParameterPrecondition() {
    }

    @Nonnull
    public static <T> T of(@Nullable T value, @Nonnull Predicate<T> predicate) {
        return of(value, predicate, "Illegal parameter: " + value);
    }

    @Nonnull
    public static <T> T of(@Nullable T value, @Nonnull Predicate<T> predicate, @Nonnull String message) {
        if (value == null) {
            throw new IllegalArgumentException("Parameter must not be null.");
        }

        if (predicate.test(value)) {
            return value;
        }

        throw new IllegalArgumentException(message);
    }

}

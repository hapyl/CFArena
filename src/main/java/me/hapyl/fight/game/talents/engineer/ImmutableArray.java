package me.hapyl.fight.game.talents.engineer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ImmutableArray<T> {

    private final T[] values;

    @SafeVarargs
    public ImmutableArray(@Nullable T... values) {
        this.values = values;
    }

    @Nullable
    public T get(int index) {
        if (values == null || values.length == 0 || index < 0 || index >= values.length) {
            return null;
        }

        return values[index];
    }

    @Nonnull
    public T get(int index, @Nonnull T or) {
        final T t = get(index);
        return t == null ? or : t;
    }

    public boolean isEmpty() {
        return values == null || values.length == 0;
    }

    public static <T> ImmutableArray<T> empty() {
        return new ImmutableArray<>();
    }

    @SafeVarargs
    public static <T> ImmutableArray<T> of(@Nonnull T... values) {
        return new ImmutableArray<>(values);
    }

}

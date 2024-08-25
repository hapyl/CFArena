package me.hapyl.fight.util;

import javax.annotation.Nonnull;
import java.util.Objects;

public class Property<T> {

    private T value;

    public Property(@Nonnull T value) {
        set(value);
    }

    public void set(@Nonnull T value) {
        this.value = nonNull(value);
    }

    @Nonnull
    public T get() {
        return nonNull(this.value);
    }

    private T nonNull(T t) {
        return Objects.requireNonNull(t, "Property value must not be null!");
    }

}

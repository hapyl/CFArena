package me.hapyl.fight.util;

import javax.annotation.Nonnull;
import java.util.function.Function;

/**
 * Represents a one time mutable value holder.
 *
 * @param <T> - Value type.
 */
public abstract class Compile<T> {

    private T value;
    private boolean compiled;

    public Compile(@Nonnull T value) {
        this.value = value;
        this.compiled = false;
    }

    protected abstract T compile(@Nonnull T t);

    @Nonnull
    public final T compile() {
        if (!compiled) {
            value = compile(value);
            compiled = true;
        }

        return value;
    }

    @Nonnull
    public static <T> Compile<T> of(@Nonnull T value, @Nonnull Function<T, T> compile) {
        return new Compile<>(value) {
            @Override
            public T compile(@Nonnull T t) {
                return compile.apply(t);
            }
        };
    }

}

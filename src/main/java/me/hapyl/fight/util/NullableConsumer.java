package me.hapyl.fight.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * A {@link Consumer} that can accept nullable value via {@link #acceptNullable(Object)}
 *
 * @param <T> - Type.
 */
public interface NullableConsumer<T> extends Consumer<T> {
    void accept(@Nonnull T t);

    default void acceptNullable(@Nullable T t) {
        if (t != null) {
            accept(t);
        }
    }
}

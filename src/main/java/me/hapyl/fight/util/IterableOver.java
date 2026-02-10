package me.hapyl.fight.util;

import javax.annotation.Nonnull;
import java.util.function.BiConsumer;

public interface IterableOver<O, T> extends Iterable<T> {

    default void forEach(@Nonnull O player, @Nonnull BiConsumer<O, T> consumer) {
        forEach(t -> {
            consumer.accept(player, t);
        });
    }

}

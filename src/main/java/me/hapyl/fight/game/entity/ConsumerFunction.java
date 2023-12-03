package me.hapyl.fight.game.entity;

import javax.annotation.Nonnull;

public interface ConsumerFunction<T, R> {

    @Nonnull
    R apply(T t);

    default void andThen(R r) {
    }


}

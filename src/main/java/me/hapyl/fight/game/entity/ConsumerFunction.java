package me.hapyl.fight.game.entity;

import javax.annotation.Nonnull;

/**
 * A {@link java.util.function.Function} with a {@link java.util.function.Consumer} behaviour.
 */
public interface ConsumerFunction<T, R> {

    /**
     * Applies the function to the given value.
     *
     * @param t - T.
     * @return R.
     */
    @Nonnull
    R apply(@Nonnull T t);

    /**
     * Performs an additional {@link java.util.function.Consumer} action on R.
     *
     * @param r - R.
     */
    default void andThen(@Nonnull R r) {
    }


}

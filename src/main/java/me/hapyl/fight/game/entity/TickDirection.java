package me.hapyl.fight.game.entity;

import javax.annotation.Nonnull;
import java.util.function.Function;

public interface TickDirection {

    /**
     * Ticks up until {@link Integer#MAX_VALUE}.
     */
    TickDirection UP = TickDirection.of(i -> i + 1, 0);

    /**
     * Ticks down until <code>0</code>.
     */
    TickDirection DOWN = TickDirection.of(i -> i - 1, 0);

    int tick(int i);

    int defaultValue();

    @Nonnull
    static TickDirection of(@Nonnull Function<Integer, Integer> fn, int min) {
        return new TickDirection() {
            @Override
            public int tick(int i) {
                return fn.apply(i);
            }

            @Override
            public int defaultValue() {
                return min;
            }

        };
    }
}

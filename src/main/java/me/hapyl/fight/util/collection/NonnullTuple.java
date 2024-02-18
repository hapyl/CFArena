package me.hapyl.fight.util.collection;

import javax.annotation.Nonnull;

/**
 * A two-value tuple.
 * <p>
 * Does <b>not</b> permit {@link javax.annotation.Nullable} values.
 *
 * @param <A> - First value.
 * @param <B> - Second value.
 */
public class NonnullTuple<A, B> extends Tuple<A, B> {
    NonnullTuple(@Nonnull A a, @Nonnull B b) {
        super(a, b);
    }

    @Nonnull
    @Override
    public A a() {
        return a;
    }

    @Nonnull
    @Override
    public A getA() {
        return a;
    }

    @Nonnull
    @Override
    public A getFirst() {
        return a;
    }

    @Nonnull
    @Override
    public B b() {
        return b;
    }

    @Nonnull
    @Override
    public B getB() {
        return b;
    }

    @Nonnull
    @Override
    public B getSecond() {
        return b;
    }
}

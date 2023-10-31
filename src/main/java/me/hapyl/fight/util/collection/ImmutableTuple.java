package me.hapyl.fight.util.collection;

import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;

/**
 * Represents an immutable tuple.
 *
 * @param a - First value.
 * @param b - Second value.
 */
public record ImmutableTuple<A, B>(@Nonnull A a, @Nonnull B b) {

    public A getA() {
        return a;
    }

    public A getFirst() {
        return a;
    }

    public B getB() {
        return b;
    }

    public B getSecond() {
        return b;
    }

    public static <A, B> ImmutableTuple<A, B> of(@Nonnull A a, @Nonnull B b) {
        Validate.notNull(a, "tuple does not permit null values");
        Validate.notNull(b, "tuple does not permit null values");
        return new ImmutableTuple<>(a, b);
    }

}

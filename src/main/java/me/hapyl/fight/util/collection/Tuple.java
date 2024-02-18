package me.hapyl.fight.util.collection;

import me.hapyl.spigotutils.module.util.Validate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A two-value tuple.
 * <p>
 * Either value may be {@link Nullable}.
 *
 * @param <A> - First value.
 * @param <B> - Second value.
 * @see NonnullTuple
 */
public class Tuple<A, B> {

    protected final A a;
    protected final B b;

    Tuple(A a, B b) {
        this.a = a;
        this.b = b;
    }

    /**
     * Gets the first element of this tuple.
     *
     * @return the first element.
     */
    public A a() { // record backwards compatibility
        return a;
    }

    /**
     * Gets the first element of this tuple.
     *
     * @return the first element.
     */
    public A getA() {
        return a;
    }

    /**
     * Gets the first element of this tuple.
     *
     * @return the first element.
     */
    public A getFirst() {
        return a;
    }

    /**
     * Gets the second element of this tuple.
     *
     * @return the second element.
     */
    public B b() { // record backwards compatibility
        return b;
    }

    /**
     * Gets the second element of this tuple.
     *
     * @return the second element.
     */
    public B getB() {
        return b;
    }

    /**
     * Gets the second element of this tuple.
     *
     * @return the second element.
     */
    public B getSecond() {
        return b;
    }

    @Override
    public String toString() {
        return "<" + a + ", " + b + ">";
    }

    public boolean isEmpty() {
        return a == null && b == null;
    }

    @Nonnull
    public static <A, B> Tuple<A, B> of(@Nullable A a, @Nullable B b) {
        return new Tuple<>(a, b);
    }

    public static <A, B> NonnullTuple<A, B> ofNonnull(@Nonnull A a, @Nonnull B b) {
        Validate.notNull(a, "NonnullTuple does not permit null values.");
        Validate.notNull(b, "NonnullTuple does not permit null values.");

        return new NonnullTuple<>(a, b);
    }


}

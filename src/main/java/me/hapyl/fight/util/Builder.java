package me.hapyl.fight.util;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * Builder utility.
 *
 * @param <T> - Builder type.
 */
public interface Builder<T> {

    /**
     * Finalizes the building object and return it.
     *
     * @return the finalized object.
     */
    @Nonnull
    T build();

    /**
     * Builds the object and then accepts it.
     *
     * @param consumer - Consumer.
     * @return the finalized object.
     */
    default T andThen(@Nonnull Consumer<T> consumer) {
        final T t = build();
        consumer.accept(t);

        return t;
    }

    /**
     * Optional predicate function.
     *
     * <br>
     * Include in {@link Builder#build()} and throw a {@link RuntimeException} to fail the build.
     * <pre><code>
     *         public void predicate() throws RuntimeException {
     *             if ( false ) {
     *                 throw new IllegalArgumentException( "Invalid argument: 'false'!" );
     *             }
     *         }
     *
     *         public Builder build() {
     *             predicate();
     *
     *             return new Object();
     *         }
     * </code></pre>
     *
     * @throws RuntimeException - An exception to fail building.
     */
    default void predicate() throws RuntimeException {
    }

}

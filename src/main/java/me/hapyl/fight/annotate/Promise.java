package me.hapyl.fight.annotate;

import javax.annotation.Nonnull;

/**
 * Indicates that the annotated element promises that {@link #value()} is always {@code true}, and if not, might throw a {@link IllegalArgumentException}.
 */
public @interface Promise {
    
    /**
     * Gets the promise.
     *
     * @return the promise.
     */
    @Nonnull
    String value();
    
}

package me.hapyl.fight.util;

import javax.annotation.Nonnull;

public interface WeakCopy extends Disposable {

    /**
     * Creates a weak copy of this object.
     * <p>
     * A weak copy implies that the object has the same values as the original,
     * but is not backed by the original. Meaning editing the values in the copy
     * should not affect the original.
     * <p>
     * The weak copy might also {@link Object#equals(Object)} or not to its original.
     *
     * @return a weak copy of this object.
     */
    @Nonnull
    Object weakCopy();

    @Override
    default void dispose() {
        throw new IllegalStateException("Cannot dispose of the object.");
    }
}

package me.hapyl.fight.util;

import javax.annotation.Nonnull;

/**
 * Allows creating a copy of the object.
 */
public interface Copyable {

    /**
     * Create a copy of this object.
     * <p>
     * The copy is not identical, and should not {@link Object#equals(Object)} to its original.
     *
     * @return a copy of this object.
     */
    @Nonnull
    Object createCopy();

}

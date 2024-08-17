package me.hapyl.fight.util;

import javax.annotation.Nonnull;

/**
 * A keyed item.
 *
 * @param <T> - Key type.
 */
public interface Keyed<T> {

    /**
     * Gets the key.
     * @return the key.
     */
    @Nonnull
    T getKey();

}

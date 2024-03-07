package me.hapyl.fight.util;

import javax.annotation.Nonnull;

public interface Caster<T> {

    /**
     * Attempts to cast the given {@link Object} to the <code>T</code>.
     *
     * @param object - Object to cast.
     * @return the object cast to the correct type.
     * @throws ClassCastException - If the object is not an instance of <code>T</code>.
     */
    @Nonnull
    T cast(@Nonnull Object object) throws ClassCastException;

}

package me.hapyl.fight.util;

import javax.annotation.Nonnull;

/**
 * An entry with name and description, usually an enum.
 */
public interface Described {

    /**
     * Gets a name of this object.
     *
     * @return name of the object.
     */
    @Nonnull
    String getName();

    /**
     * Gets a description of this object.
     *
     * @return description of this object.
     */
    @Nonnull
    String getDescription();

}

package me.hapyl.fight.util;

import javax.annotation.Nonnull;

/**
 * An entry with name and description, usually an enum.
 */
public interface Described extends Named {

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    String getName();

    /**
     * Gets a description of this object.
     *
     * @return description of this object.
     */
    @Nonnull
    String getDescription();

}

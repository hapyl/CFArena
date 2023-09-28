package me.hapyl.fight.util;

import javax.annotation.Nonnull;

/**
 * An entry with name and description, usually an enum.
 */
public interface Described {

    @Nonnull
    String getName();

    @Nonnull
    String getDescription();

}

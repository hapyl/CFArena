package me.hapyl.fight.enumclass;

import javax.annotation.Nonnull;

public interface EnumItem {

    /**
     * Returns the "name" of this item, should be in the same style as enum: <code>SCREAMING_SNAKE_CASE</code>.
     *
     * @return the "name" or ID of this item.
     */
    @Nonnull
    String name();

    /**
     * Returns the ordinal value of this item.
     *
     * @return the ordinal value of this item.
     */
    int ordinal();

}

package me.hapyl.fight.util;

import javax.annotation.Nonnull;

public interface Described {

    @Nonnull
    String getName();

    @Nonnull
    String getDescription();

}

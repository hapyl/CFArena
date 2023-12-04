package me.hapyl.fight.util;

import javax.annotation.Nonnull;

public interface Builder<T> {

    @Nonnull
    T build();

}

package me.hapyl.fight.util;

import javax.annotation.Nonnull;

public interface Keyed<T> {

    @Nonnull
    T getKey();

}

package me.hapyl.fight.util;

import javax.annotation.Nonnull;

public interface Opposable<T extends Enum<T>> {

    @Nonnull
    T opposite();
    
}

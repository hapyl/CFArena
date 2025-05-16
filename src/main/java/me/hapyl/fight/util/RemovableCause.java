package me.hapyl.fight.util;

import javax.annotation.Nonnull;

public interface RemovableCause<T extends Enum<T>> {
    
    void remove(@Nonnull T cause);
    
}

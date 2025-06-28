package me.hapyl.fight.annotate;

import javax.annotation.Nonnull;

public @interface BoolGuide {
    
    @Nonnull
    String whenNull();
    
    @Nonnull
    String whenTrue();
    
    @Nonnull
    String whenFalse();
    
}

package me.hapyl.fight.game.entity;

import javax.annotation.Nonnull;

public interface Pet {
    
    @Nonnull
    GamePlayer owner();
    
}

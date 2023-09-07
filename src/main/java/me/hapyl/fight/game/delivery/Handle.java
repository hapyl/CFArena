package me.hapyl.fight.game.delivery;

import javax.annotation.Nonnull;

public interface Handle<T> {

    @Nonnull
    T getHandle();

}

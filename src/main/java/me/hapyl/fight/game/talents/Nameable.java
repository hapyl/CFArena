package me.hapyl.fight.game.talents;

import javax.annotation.Nonnull;

public interface Nameable {

    @Nonnull
    String getName();

    void setName(@Nonnull String name);

}

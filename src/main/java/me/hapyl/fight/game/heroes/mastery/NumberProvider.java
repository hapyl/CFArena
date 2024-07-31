package me.hapyl.fight.game.heroes.mastery;

import javax.annotation.Nonnull;

public interface NumberProvider<T extends Number> {

    @Nonnull
    T getNumber();

}

package me.hapyl.fight.game;

import javax.annotation.Nonnull;

public interface Callback<T> {

    void callback(@Nonnull T t);

}

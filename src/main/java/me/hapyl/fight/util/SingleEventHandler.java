package me.hapyl.fight.util;

import javax.annotation.Nonnull;

public interface SingleEventHandler<T> {

    @Nonnull
    Class<T> getEventClass();

    void handle(@Nonnull T ev);

}

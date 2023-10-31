package me.hapyl.fight.registry;

import javax.annotation.Nonnull;

public interface EnumRegistry<T extends EnumId> {

    @Nonnull
    T getItem();

    @Nonnull
    Registry<T> getRegistry();

}

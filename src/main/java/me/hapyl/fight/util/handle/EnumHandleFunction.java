package me.hapyl.fight.util.handle;

import javax.annotation.Nonnull;

public interface EnumHandleFunction<E extends Enum<E>, H extends EnumHandle<E>> {

    @Nonnull
    H apply(@Nonnull E e);

    interface Handler<E extends Enum<E>, H extends EnumHandle<E>> {
        @Nonnull
        H apply(@Nonnull H e);
    }
}

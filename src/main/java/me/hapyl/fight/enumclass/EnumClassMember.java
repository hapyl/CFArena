package me.hapyl.fight.enumclass;

import me.hapyl.eterna.module.util.Tuple;

import javax.annotation.Nonnull;

public interface EnumClassMember {

    @Nonnull
    Tuple<Boolean, String> OK = Tuple.of(true, "");

    <E extends EnumClass> Tuple<Boolean, String> validate(@Nonnull Class<E> clazz);

    @Nonnull
    static Tuple<Boolean, String> ok() {
        return OK;
    }

    @Nonnull
    static Tuple<Boolean, String> error(@Nonnull String errorMessage) {
        return Tuple.of(false, errorMessage);
    }

}

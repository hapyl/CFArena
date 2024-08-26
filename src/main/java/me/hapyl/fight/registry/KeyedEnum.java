package me.hapyl.fight.registry;

import org.apache.commons.lang.IllegalClassException;

import javax.annotation.Nonnull;

public interface KeyedEnum extends Keyed {

    @Nonnull
    @Override
    default Key getKey() {
        if (this instanceof Enum<?> anEnum) {
            return Key.ofString(anEnum.name().toLowerCase());
        }

        throw new IllegalClassException(getClass().getSimpleName() + " must be an enum!");
    }
}

package me.hapyl.fight.util;

import javax.annotation.Nonnull;
import java.util.function.Consumer;
import java.util.function.Function;

public final class EnumIterators {

    public static <E extends Enum<E>> void of(@Nonnull Class<E> enumClass, @Nonnull Consumer<E> action) {
        for (E enumConstant : enumClass.getEnumConstants()) {
            action.accept(enumConstant);
        }
    }

    public static <E extends Enum<E>, W> void ofWrapper(@Nonnull Class<E> enumClass, @Nonnull Function<E, W> wrapperFunction, @Nonnull Consumer<W> action) {
        for (E enumConstant : enumClass.getEnumConstants()) {
            action.accept(wrapperFunction.apply(enumConstant));
        }
    }

}

package me.hapyl.fight.util;

import me.hapyl.fight.game.cosmetic.Cosmetics;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;

public class Nulls {

    public static <E, R> R getIfNotNull(@Nullable E e, @Nonnull Function<E, R> function, R def) {
        if (e == null) {
            return def;
        }

        return function.apply(e);
    }

    public static <E> void runIfNotNull(@Nullable E e, @Nonnull Consumer<E> function) {
        if (e != null) {
            function.accept(e);
        }
    }

    public static Cosmetics notNullOr(@Nullable Cosmetics selected, @Nonnull Cosmetics def) {
        if (selected == null) {
            return def;
        }

        return selected;
    }
}

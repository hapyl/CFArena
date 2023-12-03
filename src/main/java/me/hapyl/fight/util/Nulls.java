package me.hapyl.fight.util;

import me.hapyl.fight.game.cosmetic.Cosmetics;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Nulls {

    public static <E> void runIfNotNull(E e, @Nonnull Function<E> function) {
        if (e != null) {
            function.execute(e);
        }
    }

    public static Cosmetics notNullOr(@Nullable Cosmetics selected, @Nonnull Cosmetics def) {
        if (selected == null) {
            return def;
        }

        return selected;
    }
}

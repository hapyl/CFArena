package me.hapyl.fight.util;

import javax.annotation.Nonnull;

public class Nulls {

    public static <E> void runIfNotNull(E e, @Nonnull Function<E> function) {
        if (e != null) {
            function.execute(e);
        }
    }


}

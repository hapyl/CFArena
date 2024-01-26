package me.hapyl.fight.game.entity;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Random;

public class EntityRandom extends Random {

    @Nonnull
    public <T> T choice(@Nonnull Collection<T> collection) {
        final int r = nextInt(collection.size());

        int i = 0;
        for (T t : collection) {
            if (i++ == r) {
                return t;
            }
        }

        throw nullPointer("empty collection");
    }

    @Nonnull
    @SafeVarargs
    public final <T> T choice(@Nonnull T... varargs) {
        if (varargs.length == 0) {
            throw nullPointer("empty array");
        }

        return varargs[nextInt(varargs.length)];
    }

    private RuntimeException nullPointer(String message) {
        return new NullPointerException(message);
    }

}

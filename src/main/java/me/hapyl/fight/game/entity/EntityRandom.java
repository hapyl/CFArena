package me.hapyl.fight.game.entity;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Random;

public class EntityRandom extends Random {

    /**
     * Gets either a positive or negative pseudo random float value within the bound.
     *
     * @param bound - Bound.
     *              (Exclusive)
     *              <br>
     *              Applies to both positive and negative.
     *
     *              <br>
     *              <code>nextFloatBool(15);</code>
     *              is bound between -14 and 14.
     * @return positive or negative pseudo random float value within the bound.
     */
    public float nextFloatBool(float bound) {
        return nextFloat(-bound + 1, bound);
    }

    /**
     * Gets either a positive or negative pseudo random double value within the bound.
     *
     * @param bound - Bound.
     *              (Exclusive)
     *              <br>
     *              Applies to both positive and negative.
     *
     *              <br>
     *              <code>nextDoubleBool(15);</code>
     *              is bound between -14 and 14.
     * @return either a positive or negative pseudo random double value within the bound.
     */
    public double nextDoubleBool(double bound) {
        return nextDouble(-bound + 1, bound);
    }

    /**
     * Gets a random value from the collection.
     *
     * @param collection - Collection
     * @return a random value from the collection.
     * @throws IllegalArgumentException if the collection is empty.
     */
    @Nonnull
    public <T> T choice(@Nonnull Collection<T> collection) {
        final int r = nextInt(collection.size());

        int i = 0;
        for (T t : collection) {
            if (i++ == r) {
                return t;
            }
        }

        throw illegalArgument("empty collection");
    }

    /**
     * Gets a random value from the array.
     *
     * @param varargs - Array
     * @return a random value from the array.
     * @throws IllegalArgumentException if the array is empty.
     */
    @Nonnull
    @SafeVarargs
    public final <T> T choice(@Nonnull T... varargs) {
        if (varargs.length == 0) {
            throw illegalArgument("empty array");
        }

        return varargs[nextInt(varargs.length)];
    }

    public boolean checkBound(double chance) {
        final float v = nextFloat();

        return v >= chance;
    }

    private RuntimeException illegalArgument(String message) {
        return new IllegalArgumentException(message);
    }

}

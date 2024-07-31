package me.hapyl.fight.util;

public interface MutableNumericObject extends NumericObject {

    void setInt(final int i);

    default void setLong(final long l) {
        setInt((int) l);
    }

    default void setDouble(final double d) {
        setInt((int) d);
    }

    default void setFloat(final float f) {
        setInt((int) f);
    }

    default void zero() {
        setInt(0);
    }

    default void min() {
        setInt(Integer.MIN_VALUE);
    }

    default void max() {
        setInt(Integer.MAX_VALUE);
    }

}

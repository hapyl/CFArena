package me.hapyl.fight.util;

/**
 * Numeric implementation of an object that can be converted or interpreted as a number.
 * <br>
 * Mimics {@link Object}s {@link Object#toString()} method but with numbers.
 */
public interface NumericObject {

    /**
     * Gets an {@link Integer} representation of this object.
     *
     * @return an integer representation of this object.
     */
    int toInt();

    /**
     * Gets a {@link Long} representation of this object.
     *
     * @return a long representation of this object.
     */
    default long toLong() {
        return toInt();
    }

    /**
     * Gets a {@link Double} representation of this object.
     *
     * @return a double representation of this object.
     */
    default double toDouble() {
        return toInt();
    }

    /**
     * Gets a {@link Float} representation of this object.
     *
     * @return a float representation of this object.
     */
    default float toFloat() {
        return toInt();
    }

}

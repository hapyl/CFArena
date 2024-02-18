package me.hapyl.fight.util;

import javax.annotation.Nonnull;

/**
 * A helper number utility class, with its unique {@link #toString()} method, which formats the string depending on its type.
 *
 * <ul>
 *     <li>
 *         Integer: Zero decimal places.
 *     </li>
 *     <li>
 *         Float: One decimal place.
 *     </li>
 *     <li>
 *         Double: Two decimal place.
 *     </li>
 *     <li>
 *         Anything else: Throws an error.
 *     </li>
 * </ul>
 */
public class Numeric extends Number {

    private final double value;
    private final byte decimal;

    /**
     * Statically typed {@link Integer} constructor.
     *
     * @param value - Int.
     */
    public Numeric(int value) {
        this(value, (byte) 0);
    }

    /**
     * Statically typed {@link Float} constructor.
     *
     * @param value - Float.
     */
    public Numeric(float value) {
        this(value, (byte) 1);
    }

    /**
     * Statically typed {@link Double} constructor.
     *
     * @param value - Double.
     */
    public Numeric(double value) {
        this(value, (byte) 2);
    }

    private Numeric(double value, byte decimal) {
        this.value = value;
        this.decimal = decimal;
    }

    @Override
    public int intValue() {
        return (int) value;
    }

    @Override
    public long longValue() {
        return (long) value;
    }

    @Override
    public float floatValue() {
        return (float) value;
    }

    @Override
    public double doubleValue() {
        return value;
    }

    @Override
    public String toString() throws IllegalStateException {
        return switch (decimal) {
            case 0 -> "%,.0f".formatted(value);
            case 1 -> "%,.1f".formatted(value);
            case 2 -> "%,.2f".formatted(value);
            default -> throw new IllegalStateException("Unexpected value: " + decimal);
        };
    }

    @Nonnull
    public static Numeric of(@Nonnull Number number) {
        if (number instanceof Integer integer) {
            return new Numeric(integer);
        }
        else if (number instanceof Float aFloat) {
            return new Numeric(aFloat);
        }
        else if (number instanceof Double aDouble) {
            return new Numeric(aDouble);
        }

        throw new IllegalArgumentException("Not supported number: " + number);
    }

}

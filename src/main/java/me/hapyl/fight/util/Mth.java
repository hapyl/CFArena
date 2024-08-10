package me.hapyl.fight.util;

public final class Mth {

    private Mth() {
    }

    /**
     * Increments the given value by one while keeping it in the limit of min.
     *
     * @param i   - Integer.
     * @param min - Min.
     */
    public static int incrementMin(final int i, final int min) {
        return Math.min(i + 1, min);
    }

    /**
     * Decrements the given value by one while keeping it in the limit of max.
     *
     * @param i   - Integer.
     * @param max - Max.
     */
    public static int decrementMax(int i, int max) {
        return Math.max(i - 1, max);
    }

}

package me.hapyl.fight.util;

import javax.annotation.Nullable;

/**
 * Static utility class for easier bitmask manipulations.
 */
public final class Bitmask {

    private Bitmask() {
    }

    /**
     * Constructs a bitmask from the given bits.
     *
     * @param defaultMask - Default mask to return if the bits are null.
     * @param bits        - Bits.
     * @return a bitmask.
     */
    public static byte makeMask(byte defaultMask, @Nullable byte... bits) {
        if (bits == null || bits.length == 0) {
            return defaultMask;
        }
        else if (bits.length == 1) {
            return bits[0];
        }

        byte bitmask = 0;

        for (byte b : bits) {
            bitmask |= b;
        }

        return bitmask;
    }

    /**
     * Constructs a bitmask from the given bits.
     *
     * @param bits - Bits.
     * @return a bitmask.
     */
    public static byte makeMask(@Nullable byte... bits) {
        return makeMask((byte) 0, bits);
    }

    /**
     * Check if the given mask has a given bit set.
     *
     * @param bitmask - Bitmask.
     * @param bit     - Bit.
     * @return true if the given mask has a given bit; false otherwise.
     */
    public static boolean isMasked(byte bitmask, byte bit) {
        return (bitmask & bit) != 0;
    }

}

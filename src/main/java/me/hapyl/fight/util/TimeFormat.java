package me.hapyl.fight.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class TimeFormat {

    /**
     * Format hours.
     */
    public static final byte HOURS = 0x1;
    /**
     * Format minutes.
     */
    public static final byte MINUTES = 0x2;
    /**
     * Format seconds.
     */
    public static final byte SECONDS = 0x4;
    /**
     * Format millis.
     */
    public static final byte MILLIS = 0x8;

    /**
     * The default bitmask.
     */
    public static final byte DEFAULT_BIT_MASK = HOURS | MINUTES | SECONDS;

    /**
     * Formats the given millis with a given bit mask.
     *
     * @param millis - Millis.
     * @param mask   - Bitmask.
     *               Default bitmask is <code>HOURS | MINUTES | SECONDS</code>
     * @return the formatted millis.
     */
    @Nonnull
    public static String format(final long millis, @Nullable final byte... mask) {
        byte bitMask = makeBitMask(mask);

        final long seconds = millis / 1000;
        final long minutes = seconds / 60;
        final long hours = minutes / 60;

        final StringBuilder builder = new StringBuilder();

        if ((bitMask & HOURS) != 0) {
            builder.append("%02dh ".formatted(hours));
        }

        if ((bitMask & MINUTES) != 0) {
            builder.append("%02dm ".formatted(minutes % 60));
        }

        if ((bitMask & SECONDS) != 0) {
            builder.append("%02ds ".formatted(seconds % 60));
        }

        if ((bitMask & MILLIS) != 0) {
            builder.append("%03dms ".formatted(millis % 1000));
        }

        return builder.toString().trim();
    }

    public static byte makeBitMask(@Nullable byte... mask) {
        if (mask == null || mask.length == 0) {
            return DEFAULT_BIT_MASK;
        }

        byte bitMask = 0;

        for (byte b : mask) {
            bitMask |= b;
        }

        return bitMask;
    }

}
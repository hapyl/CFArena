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
     * @param bits   - Bitmask.
     *               Default bitmask is <code>HOURS | MINUTES | SECONDS</code>
     * @return the formatted millis.
     */
    @Nonnull
    public static String format(final long millis, @Nullable final byte... bits) {
        byte bitMask = Bitmask.makeMask(DEFAULT_BIT_MASK, bits);

        final long seconds = millis / 1000;
        final long minutes = seconds / 60;
        final long hours = minutes / 60;

        final StrBuilder builder = new StrBuilder();

        if (Bitmask.isMasked(bitMask, HOURS)) {
            builder.append("%02dh ".formatted(hours));
        }

        if (Bitmask.isMasked(bitMask, MINUTES)) {
            builder.append("%02dm ".formatted(minutes % 60));
        }

        if (Bitmask.isMasked(bitMask, SECONDS)) {
            builder.append("%02ds ".formatted(seconds % 60));
        }

        if (Bitmask.isMasked(bitMask, MILLIS)) {
            builder.append("%03dms ".formatted(millis % 1000));
        }

        return builder.toString().trim();
    }

}
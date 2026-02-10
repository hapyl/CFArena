package me.hapyl.fight.infraction;

import javax.annotation.Nonnull;
import java.security.SecureRandom;
import java.util.Arrays;

public final class HexID {

    private static final SecureRandom RANDOM;
    private static final int LENGTH;
    private static final char[] CHARS;
    private static final String STRING_CHAR;

    private static HexID EMPTY;

    // man I love static
    static {
        RANDOM = new SecureRandom();
        LENGTH = 8;
        CHARS = new char[] {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F',
                'a', 'b', 'c', 'd', 'e', 'f'
        };
        STRING_CHAR = "#";
    }

    private final char[] hex;

    private HexID() {
        this.hex = new char[LENGTH];

        for (int i = 0; i < LENGTH; i++) {
            hex[i] = CHARS[RANDOM.nextInt(LENGTH)];
        }
    }

    /**
     * Returns <code>true</code> if this {@link HexID} is empty, meaning all bytes are <code>0</code>.
     *
     * @return true if this hex id is empty.
     */
    public boolean isEmpty() {
        for (char c : this.hex) {
            if (c != CHARS[0]) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        return STRING_CHAR + new String(hex);
    }

    public boolean equals(@Nonnull String string) {
        return compareCharArray(string.toCharArray()) == 0;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        final HexID hexID = (HexID) other;
        return Arrays.equals(hex, hexID.hex);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(hex);
    }

    private int compareCharArray(char[] other) {
        int m = LENGTH;

        for (int i = 0; i < other.length; i++) {
            if (hex[i] == other[i]) {
                m--;
            }
        }

        return m;
    }

    /**
     * Creates a new pseudo-random {@link HexID}.
     *
     * @return a new hex id.
     */
    @Nonnull
    public static HexID random() {
        return new HexID();
    }

    /**
     * Gets an empty {@link HexID}.
     *
     * @return an empty hex id.
     */
    @Nonnull
    public static HexID empty() {
        if (EMPTY == null) {
            EMPTY = new HexID();
            Arrays.fill(EMPTY.hex, CHARS[0]);
        }

        return EMPTY;
    }

    /**
     * Creates a new {@link HexID} from the given {@link String}.
     *
     * @param string - String.
     * @return a new hex id.
     * @throws IllegalArgumentException if the given {@link String} length isn't {@link #LENGTH} or contains unsupported character.
     */
    @Nonnull
    public static HexID fromString(@Nonnull String string) {
        // strip # if present
        if (string.startsWith(STRING_CHAR)) {
            string = string.substring(1);
        }

        if (string.length() != LENGTH) {
            throw new IllegalArgumentException("Illegal length: " + string.length());
        }

        final HexID hexID = new HexID();
        final char[] chars = string.toCharArray();

        for (int i = 0; i < hexID.hex.length; i++) {
            final char c = chars[i];

            if (!isValidChar(c)) {
                throw new IllegalArgumentException("Illegal char: " + c);
            }

            hexID.hex[i] = c;
        }

        return hexID;
    }

    /**
     * Creates a new {@link HexID} from the given {@link String}, or {@link #empty()} if the {@link String} is invalid.
     *
     * @param string - String.
     * @return a new hex id or empty hex id if string is invalid.
     */
    @Nonnull
    public static HexID fromStringOrEmpty(@Nonnull String string) {
        try {
            return fromString(string);
        } catch (Exception e) {
            return empty();
        }
    }

    public static boolean isValidChar(final char c) {
        for (char aChar : CHARS) {
            if (aChar == c) {
                return true;
            }
        }

        return false;
    }
}

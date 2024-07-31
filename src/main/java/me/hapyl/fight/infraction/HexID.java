package me.hapyl.fight.infraction;

import javax.annotation.Nonnull;
import java.security.SecureRandom;
import java.util.Arrays;

public final class HexID {

    public static final HexID NULL;

    private static final SecureRandom RANDOM;
    private static final int LENGTH;
    private static final char[] CHARS;

    // man I love static
    static {
        RANDOM = new SecureRandom();
        LENGTH = 8;
        CHARS = new char[] {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F',
                'a', 'b', 'c', 'd', 'e', 'f'
        };

        // Make sure to keep this last
        NULL = new HexID(true);
    }

    private final char[] hex;

    private HexID(boolean empty) {
        this.hex = new char[LENGTH];

        for (int i = 0; i < LENGTH; i++) {
            hex[i] = CHARS[0];
        }
    }

    public HexID() {
        this.hex = new char[LENGTH];

        for (int i = 0; i < LENGTH; i++) {
            hex[i] = CHARS[RANDOM.nextInt(LENGTH)];
        }
    }

    @Override
    public String toString() {
        return "#" + new String(hex);
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
}

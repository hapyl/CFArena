package me.hapyl.fight.util;

import org.bukkit.ChatColor;

import javax.annotation.Nonnull;
import java.util.Map;

public final class MathBoldFont {

    private static final Map<String, String> NUMBER_MAP = Map.of(
            "0", "𝟎",
            "1", "𝟏",
            "2", "𝟐",
            "3", "𝟑",
            "4", "𝟒",
            "5", "𝟓",
            "6", "𝟔",
            "7", "𝟕",
            "8", "𝟖",
            "9", "𝟗"
    );

    public static String format(@Nonnull String string) {
        final int length = string.length();
        final StringBuilder builder = new StringBuilder(length);

        final char[] chars = string.toCharArray();
        for (int i = 0; i < length; i++) {
            final char c = chars[i];

            // Skip color codes
            if (c == ChatColor.COLOR_CHAR || c == '&') {
                ++i;
                continue;
            }

            final String stringChar = String.valueOf(c);
            builder.append(NUMBER_MAP.getOrDefault(stringChar, stringChar));
        }

        return builder.toString();
    }


}

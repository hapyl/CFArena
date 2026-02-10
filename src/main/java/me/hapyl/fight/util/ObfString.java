package me.hapyl.fight.util;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.util.BukkitUtils;

import javax.annotation.Nonnull;
import java.util.Set;

public class ObfString {

    private static final Set<Character> COLOR_CHARS = Set.of(
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'k', 'l', 'm', 'n', 'o', 'r', 'x',
            'A', 'B', 'C', 'D', 'E', 'F', 'K', 'L', 'M', 'N', 'O', 'R', 'X'
    );

    @Nonnull
    public static String of(@Nonnull String prefix, @Nonnull String text, int letters, @Nonnull String with) {
        if (letters == 0) {
            return text;
        }

        final String strippedText = text.replaceAll("&[0-9a-fk-orx]", "");
        final int textLength = strippedText.length();

        if (textLength < letters) {
            throw new IllegalArgumentException("The provided text is shorter than %s!".formatted(letters));
        }

        final Set<Integer> obfuscatedIndexes = Sets.newHashSet();

        while (obfuscatedIndexes.size() < letters) {
            obfuscatedIndexes.add(BukkitUtils.RANDOM.nextInt(textLength));
        }

        final StringBuilder builder = new StringBuilder();
        final char[] chars = text.toCharArray();

        int index = 0;
        boolean seenColorChar = false;

        for (final char c : chars) {
            // Skip colors
            if (seenColorChar) {
                if (COLOR_CHARS.contains(c)) {
                    continue;
                }
                else {
                    seenColorChar = false;
                }
            }

            if (c == '&') {
                seenColorChar = true;
                continue;
            }

            // Replace the letter
            if (obfuscatedIndexes.contains(index++)) {
                builder.append(with);
            }
            else {
                builder.append(c);
            }
        }

        return prefix + builder;
    }

}

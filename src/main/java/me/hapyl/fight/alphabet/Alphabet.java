package me.hapyl.fight.alphabet;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;

public interface Alphabet {

    Alphabet FUTHARK = AlphabetImpl.of(
            'ᚨ', 'ᛒ', 'ᚲ', 'ᛞ', 'ᛖ',
            'ᚠ', 'ᚷ', 'ᚺ', 'ᛁ', 'ᛃ',
            'ᚲ', 'ᛚ', 'ᛗ', 'ᚾ', 'ᛟ',
            'ᛈ', 'ᚲ', 'ᚱ', 'ᛊ', 'ᛏ',
            'ᚢ', 'ᚢ', 'ᚹ', 'ᚴ', 'ᛁ', 'ᛉ'
    );

    /**
     * Gets the alphabet character based on the given english character.
     *
     * @param enChar - The english character.
     * @return the alphabet character.
     */
    char getAlphabetChar(char enChar);

    /**
     * Gets the english character based on the given alphabet character.
     *
     * @param alphabetChar - The alphabet character.
     * @return the english character.
     */
    char getEnglishChar(char alphabetChar);

    /**
     * Translates the english string to the alphabet string.
     *
     * @param english - The string to translate.
     * @return The translated string.
     */
    @Nonnull
    default String translateTo(@Nonnull String english) {
        return translate(english, this, Alphabet::getAlphabetChar);
    }

    /**
     * Translates the alphabet string to the english.
     *
     * @param string - The string to translate.
     * @return The translated string.
     */
    @Nonnull
    default String translateFrom(@Nonnull String string) {
        return translate(string, this, Alphabet::getEnglishChar);
    }

    private static String translate(String string, Alphabet alphabet, BiFunction<Alphabet, Character, Character> fn) {
        final StringBuilder builder = new StringBuilder();
        final char[] chars = string.toCharArray();

        for (char c : chars) {
            if (Character.isWhitespace(c)) {
                builder.append(" ");
            }
            else {
                builder.append(fn.apply(alphabet, c));
            }
        }

        return builder.toString();
    }

}

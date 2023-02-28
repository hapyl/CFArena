package me.hapyl.fight.util;

import com.google.common.collect.Maps;

import java.util.Map;

public class SmallCaps {

    private static final Map<Character, Character> letters;

    static {
        letters = Maps.newHashMap();

        letters.put('a', 'ᴀ');
        letters.put('b', 'ʙ');
        letters.put('c', 'ᴄ');
        letters.put('d', 'ᴅ');
        letters.put('e', 'ᴇ');
        letters.put('f', 'ғ');
        letters.put('g', 'ɢ');
        letters.put('h', 'ʜ');
        letters.put('i', 'ɪ');
        letters.put('j', 'ᴊ');
        letters.put('k', 'ᴋ');
        letters.put('l', 'ʟ');
        letters.put('m', 'ᴍ');
        letters.put('n', 'ɴ');
        letters.put('o', 'ᴏ');
        letters.put('p', 'ᴘ');
        letters.put('q', 'ǫ');
        letters.put('r', 'ʀ');
        letters.put('s', 's');
        letters.put('t', 'ᴛ');
        letters.put('u', 'ᴜ');
        letters.put('v', 'ᴠ');
        letters.put('w', 'ᴡ');
        letters.put('x', 'x');
        letters.put('y', 'ʏ');
        letters.put('z', 'ᴢ');
    }

    public static String format(String string) {
        final StringBuilder builder = new StringBuilder();

        for (char c : string.toLowerCase().toCharArray()) {
            final Character smallCaps = letters.getOrDefault(c, c);
            builder.append(smallCaps);
        }

        return builder.toString();
    }

}

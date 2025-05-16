package me.hapyl.fight.util;

import me.hapyl.eterna.module.annotate.UtilityClass;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;

// TODO @May 11, 2025 (xanyjl) -> move to eterna
@UtilityClass
public final class Plural {
    
    private static final Set<String> ES_SUFFIXES;
    private static final Map<String, String> IRREGULARS;
    
    static {
        ES_SUFFIXES = Set.of("s", "x", "z", "ch", "sh");
        IRREGULARS = Map.ofEntries(
                Map.entry("man", "men"),
                Map.entry("woman", "women"),
                Map.entry("child", "children"),
                Map.entry("person", "people"),
                Map.entry("mouse", "mice"),
                Map.entry("goose", "geese"),
                Map.entry("tooth", "teeth"),
                Map.entry("foot", "feet"),
                Map.entry("cactus", "cacti"),
                Map.entry("focus", "foci"),
                Map.entry("phenomenon", "phenomena"),
                Map.entry("criterion", "criteria"),
                Map.entry("bacterium", "bacteria")
        );
        
    }
    
    private Plural() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    @Nonnull
    public static String pluralize(@Nonnull String string, @Nonnull Number number) {
        if (number.doubleValue() == 1.0) {
            return string;
        }
        
        final String lower = string.toLowerCase();
        
        // Irregulars
        final String irregular = IRREGULARS.get(lower);
        
        if (irregular != null) {
            // Preserve capitalization
            if (Character.isUpperCase(string.charAt(0))) {
                return Character.toUpperCase(irregular.charAt(0)) + irregular.substring(1);
            }
            
            return irregular;
        }
        
        // Ends in consonant + y → replace y with ies
        if (string.endsWith("y") && string.length() > 1) {
            final char beforeY = string.charAt(string.length() - 2);
            
            if (!isVowel(beforeY)) {
                return string.substring(0, string.length() - 1) + "ies";
            }
        }
        
        // -es suffix rules
        for (String suffix : ES_SUFFIXES) {
            if (lower.endsWith(suffix)) {
                return string + "es";
            }
        }
        
        return string + "s";
    }
    
    private static boolean isVowel(char c) {
        return "aeiouAEIOU".indexOf(c) != -1;
    }
}
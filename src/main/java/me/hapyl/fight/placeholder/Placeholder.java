package me.hapyl.fight.placeholder;

import com.google.common.collect.Maps;

import javax.annotation.Nonnull;
import java.util.Map;

public class Placeholder {

    private static final Map<String, String> PLACEHOLDERS = Maps.newHashMap();

    @Nonnull
    public static String format(@Nonnull String string) {
        return string;
    }

    public static void add(Placeholder2 p) {
        PLACEHOLDERS.put(p.getPlaceholder(), p.getReplacement());
    }

    public static void remove(Placeholder2 p) {
        PLACEHOLDERS.remove(p.getPlaceholder());
    }

}

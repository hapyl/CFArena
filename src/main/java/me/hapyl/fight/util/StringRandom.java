package me.hapyl.fight.util;

import me.hapyl.eterna.module.util.CollectionUtils;
import org.jetbrains.annotations.Range;

import javax.annotation.Nonnull;

public final class StringRandom {

    private StringRandom() {
    }

    @Nonnull
    public static String of(@Nonnull @Range(from = 1, to = Integer.MAX_VALUE) String... values) {
        if (values.length < 1) {
            return "";
        }

        return CollectionUtils.randomElement(values, "");
    }

}

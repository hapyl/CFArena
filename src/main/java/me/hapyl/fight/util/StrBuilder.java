package me.hapyl.fight.util;

import me.hapyl.eterna.module.util.Builder;

import javax.annotation.Nonnull;

import static java.lang.String.valueOf;

public final class StrBuilder implements Builder<String> {

    private String string;

    public StrBuilder(@Nonnull String string) {
        this.string = string;
    }

    public StrBuilder() {
        this("");
    }

    public StrBuilder append(@Nonnull Object object) {
        string += valueOf(object);
        return this;
    }

    public StrBuilder appendIf(@Nonnull Object object, boolean condition) {
        if (condition) {
            append(object);
        }

        return this;
    }

    public StrBuilder replace(@Nonnull String from, @Nonnull Object object) {
        string = string.replace(from, valueOf(object));
        return this;
    }

    public StrBuilder replaceFirst(@Nonnull String regex, @Nonnull Object object) {
        string = string.replaceFirst(regex, valueOf(object));
        return this;
    }

    @Nonnull
    @Override
    public String build() {
        return string;
    }

    @Override
    public String toString() {
        return build();
    }

}

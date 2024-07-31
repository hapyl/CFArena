package me.hapyl.fight.util;

import javax.annotation.Nonnull;

public class StringReplacer {

    private String string;

    public StringReplacer(@Nonnull String string) {
        this.string = string;
    }

    public StringReplacer replace(@Nonnull CharSequence from, @Nonnull CharSequence to) {
        this.string = this.string.replace(from, to);

        return this;
    }

    public StringReplacer replaceFirst(@Nonnull String regex, @Nonnull String to) {
        this.string = this.string.replaceFirst(regex, to);
        return this;
    }

    @Override
    public String toString() {
        return string;
    }
}

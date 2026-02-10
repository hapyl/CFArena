package me.hapyl.fight.util;

import javax.annotation.Nonnull;

public class Styler {

    private String prefix;
    private String suffix;

    public Styler() {
    }

    public Styler setPrefix(@Nonnull String string) {
        this.prefix = string;
        return this;
    }

    public Styler setSuffix(@Nonnull String string) {
        this.suffix = string;
        return this;
    }

    @Nonnull
    public String style(@Nonnull String string) {
        return prefix + string + suffix;
    }

}

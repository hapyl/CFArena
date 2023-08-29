package me.hapyl.fight.placeholder;

import javax.annotation.Nonnull;

public interface Placeholder2 {

    @Nonnull
    String getPlaceholder();

    @Nonnull
    String getReplacement();

    @Nonnull
    default String getPlaceholderFormatted() {
        return "{" + getPlaceholder() + "}";
    }

}

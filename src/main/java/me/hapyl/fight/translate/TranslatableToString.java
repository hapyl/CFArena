package me.hapyl.fight.translate;

import javax.annotation.Nonnull;

public interface TranslatableToString {

    @Nonnull
    String toString(@Nonnull Language language);

}

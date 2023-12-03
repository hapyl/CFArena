package me.hapyl.fight.util;

import javax.annotation.Nonnull;

public interface Prefixed extends Described {

    @Nonnull
    String getPrefix();

}

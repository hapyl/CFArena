package me.hapyl.fight.util;

import me.hapyl.eterna.module.util.Described;

import javax.annotation.Nonnull;

public interface Prefixed extends Described {

    @Nonnull
    String getPrefix();

}

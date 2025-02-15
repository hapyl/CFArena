package me.hapyl.fight.util;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;

import javax.annotation.Nonnull;

public interface CloneableKeyed {

    Keyed cloneAs(@Nonnull Key key);

}

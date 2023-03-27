package me.hapyl.fight.util.displayfield;

import javax.annotation.Nonnull;

public interface DisplayFieldFormatter {

    @Nonnull
    String format(String key, String value);

}

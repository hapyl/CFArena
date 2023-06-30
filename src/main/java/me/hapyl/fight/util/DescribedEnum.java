package me.hapyl.fight.util;

import javax.annotation.Nonnull;

public interface DescribedEnum {

    @Nonnull
    String getName();

    @Nonnull
    String getDescription();

}

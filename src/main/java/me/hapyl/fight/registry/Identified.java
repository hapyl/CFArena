package me.hapyl.fight.registry;

import javax.annotation.Nonnull;

public interface Identified {

    @Nonnull
    EnumId getId();

    @Nonnull
    default String getStringId() {
        return getId().toString();
    }

}

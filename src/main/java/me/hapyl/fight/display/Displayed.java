package me.hapyl.fight.display;

import org.bukkit.Material;

import javax.annotation.Nonnull;

public interface Displayed {

    @Nonnull
    Display getDisplay();

    @Nonnull
    default String getName() {
        return getDisplay().getName();
    }

    @Nonnull
    default String getDescription() {
        return getDisplay().getDescription();
    }

    @Nonnull
    default Material getMaterial() {
        return getDisplay().getMaterial();
    }

}

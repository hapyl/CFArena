package me.hapyl.fight.util;

import org.bukkit.Location;

import javax.annotation.Nonnull;

public interface Contact<E, C> {

    void onContact(@Nonnull E e, @Nonnull C c, @Nonnull Location location);

}

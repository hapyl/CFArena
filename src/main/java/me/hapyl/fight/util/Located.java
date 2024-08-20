package me.hapyl.fight.util;

import org.bukkit.Location;
import org.bukkit.World;

import javax.annotation.Nonnull;

public interface Located {

    /**
     * Gets the {@link Location} this entity is located at.
     *
     * @return the location this entity is located at.
     */
    @Nonnull
    Location getLocation();

    /**
     * Gets the {@link World} this entity is located in.
     *
     * @return the world this entity is located in.
     */
    @Nonnull
    default World getWorld() {
        return getLocation().getWorld();
    }

}

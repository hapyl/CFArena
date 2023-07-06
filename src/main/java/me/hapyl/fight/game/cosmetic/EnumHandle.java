package me.hapyl.fight.game.cosmetic;

import javax.annotation.Nonnull;

/**
 * Since enums are used as registries, this is used store enum to an object (handle).
 */
public interface EnumHandle<E extends Enum<E>> {

    /**
     * Gets the enum handle.
     */
    @Nonnull
    E getHandle();

    /**
     * Sets the enum handle.
     * @param handle - Handle
     */
    void setHandle(@Nonnull E handle);

}

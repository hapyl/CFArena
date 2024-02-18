package me.hapyl.fight.game.cosmetic;

import javax.annotation.Nonnull;

/**
 * Interface for <code>enum >< object</code> registries, where the object returns it's <code>enum</code> handle.
 */
public interface EnumHandle<E extends Enum<E>> {

    /**
     * Gets the <code>enum</code> handle of this object.
     *
     * @return the enum handle.
     * @implNote might or might not throw an error if the handle is not set.
     */
    @Nonnull
    E getHandle();

    /**
     * Sets the <code>enum</code> handle of this object.
     *
     * @param handle - Handle
     * @implNote might or might not throw an error if the handle is already set.
     */
    void setHandle(@Nonnull E handle);

}

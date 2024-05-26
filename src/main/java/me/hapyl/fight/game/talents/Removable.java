package me.hapyl.fight.game.talents;

import org.bukkit.entity.Entity;

/**
 * An interface to indicate a {@link Removable} element, be that {@link Entity} or whatever can be removed.
 */
public interface Removable {

    /**
     * Removes this {@link Removable}.
     */
    void remove();

    /**
     * Useful for maps computations.
     * Returning true and calling {@link #removeIf()} is a nifty way
     * to remove this {@link Removable} from a map and call the {@link #remove()} method.
     *
     * @return true if this removable should be removed.
     */
    default boolean shouldRemove() {
        return false;
    }

    /**
     * Returns true if this {@link Removable} should be removed, also calls {@link #remove()} if returns value is true.
     * <br>
     * Can be used in map like so:
     * <pre>
     *     map.removeIf(Removable::removeIf);
     * </pre>
     * to avoid doing something ugly like:
     * <pre>
     *     moonZones.removeIf(removable -> {
     *             if (removable.shouldRemove()) {
     *                 removable.remove();
     *                 return true;
     *             }
     *
     *             return false;
     *         });
     * </pre>
     *
     * @return true if this element will be removed.
     */
    default boolean removeIf() {
        if (shouldRemove()) {
            remove();
            return true;
        }

        return false;
    }

}

package me.hapyl.fight.registry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface Keyed {

    /**
     * Gets the {@link Key} of this {@link Keyed} object.
     *
     * @return the key of this object.
     */
    @Nonnull
    Key getKey();

    /**
     * Gets the {@link String} of the {@link Key} of this object.
     * <br>
     * This is identical to:
     * <pre>{@code
     * keyed.getKey().getKey();
     * }</pre>
     * <br>
     * Using
     * <pre>{@code
     * database.set(keyed.getKey(), object);
     * }</pre>
     * as a string is fine, but prefer calling this method.
     *
     * @return the string of the key if this object.
     */
    @Nonnull
    default String getKeyAsString() {
        return getKey().getKey();
    }

    /**
     * {@link Keyed} must implement {@link Object#equals(Object)} and compare the {@link Key}.
     * <pre>{@code
     * @Override
     * public final boolean equals(Object object) {
     *     if (this == object) {
     *         return true;
     *     }
     *
     *     if (object == null || getClass() != object.getClass()) {
     *         return false;
     *     }
     *
     *     final Keyed other = (Keyed) object;
     *     return Objects.equals(key, other.key);
     * }
     * }</pre>
     *
     * @param object - Object to compare to.
     * @return true if the objects are identical, false otherwise.
     */
    boolean equals(@Nullable Object object);

    /**
     * {@link Keyed} must implement {@link Object#hashCode()} and return the hash code of the {@link Key}.
     * <pre>{@code
     * @Override
     * public final int hashCode() {
     *     return Objects.hashCode(key);
     * }
     * }</pre>
     *
     * @return the hash code of the key.
     */
    int hashCode();

}

package me.hapyl.fight.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A <code>final</code> element instance. Allows setting an element once.
 *
 * @param <E> element.
 */
public class Final<E> {

    private E element;

    /**
     * Creates an empty instance.
     */
    public Final() {
        this(null);
    }

    /**
     * Creates an instance with the given element.
     *
     * @param e - Element. Might or might not be null.
     */
    public Final(@Nullable E e) {
        element = e;
    }

    /**
     * Sets the element if it's not present already.
     *
     * @return true if the element was set; false otherwise.
     */
    public boolean set(@Nonnull E element) {
        if (isNull()) {
            this.element = element;
            return true;
        }

        return false;
    }

    /**
     * Gets the element; or null.
     *
     * @return the element; or null.
     */
    @Nullable
    public E get() {
        return element;
    }

    /**
     * Gets the element or throws an error if the element is not set.
     *
     * @return the element.
     * @throws IllegalStateException if the element is not set.
     */
    @Nonnull
    public E getOrThrow() throws IllegalStateException {
        if (isNull()) {
            throw new IllegalStateException("Element is not set for " + this);
        }

        return element;
    }

    /**
     * Sets the element or throws an error if the element is already set.
     *
     * @param element - Element to set.
     * @throws IllegalStateException if the element is already set.
     */
    public void setOrThrow(@Nonnull E element) throws IllegalStateException {
        if (!set(element)) {
            throw new IllegalStateException("Element is already set for " + this);
        }
    }

    /**
     * Returns true if the element is not set; false otherwise.
     *
     * @return true if the element is not set; false otherwise.
     */
    public boolean isNull() {
        return element == null;
    }

    /**
     * Returns true if the element is set; false otherwise.
     *
     * @return true if the element is set; false otherwise.
     */
    public boolean isSet() {
        return element != null;
    }

    @Override
    public String toString() {
        return element != null ? element.toString() : "null";
    }


}

package me.hapyl.fight.util;

/**
 * Allows retrieving but never add if an element is already present.
 *
 * @param <E> element.
 */
public class Final<E> {

    private E element;

    public Final() {
        this(null);
    }

    public Final(E e) {
        element = e;
    }

    /**
     * Sets the element if it's not set already.
     *
     * @return success of setting the element.
     */
    public boolean set(E element) {
        if (isNull()) {
            this.element = element;
            return true;
        }
        return false;
    }

    public void setUnsafe(E element) throws IllegalStateException {
        if (isNull()) {
            set(element);
            return;
        }

        throw new IllegalStateException("Element is already set for " + getClass().getSimpleName());
    }

    public E get() {
        return element;
    }

    public boolean isNull() {
        return element == null;
    }

    public E getOr(E or) {
        return isNull() ? or : element;
    }


}

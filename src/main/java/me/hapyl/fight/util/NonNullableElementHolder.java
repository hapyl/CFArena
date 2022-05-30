package me.hapyl.fight.util;

import org.apache.commons.lang.Validate;

import javax.annotation.Nullable;

public class NonNullableElementHolder<E> {

    private E element;

    public NonNullableElementHolder(E element) {
        set(element);
    }

    @Nullable
    public E getElement() {
        return element;
    }

    public void set(E element) {
        Validate.notNull(element, "NonNullableElementHolder is prohibited of null values");
        this.element = element;
    }


}

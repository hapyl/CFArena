package me.hapyl.fight.util;

import javax.annotation.Nonnull;
import java.util.Objects;

public class NonNullableElementHolder<E> {
    
    private E element;
    
    public NonNullableElementHolder(@Nonnull E element) {
        set(element);
    }
    
    @Nonnull
    public E getElement() {
        return element;
    }
    
    public void set(@Nonnull E element) {
        this.element = Objects.requireNonNull(element, getClass().getSimpleName() + " does not permit null objects!");
    }
    
    
}

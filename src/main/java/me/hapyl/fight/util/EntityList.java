package me.hapyl.fight.util;

import org.bukkit.entity.Entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class EntityList<E extends Entity> implements SimpleList<E>, Sizeable {

    private final E[] elements;

    @SuppressWarnings("unchecked")
    public EntityList(final int size) {
        elements = (E[]) new Entity[size];
    }

    @Override
    public int size() {
        return elements.length;
    }

    @Override
    public boolean isEmpty() {
        for (E e : elements) {
            if (e != null) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean contains(Object o) {
        for (E e : elements) {
            if (e != null && e.equals(o)) {
                return true;
            }
        }

        return false;
    }

    @Nonnull
    @Override
    public java.util.Iterator<E> iterator() {
        return new Iterator();
    }

    @Nonnull
    @Override
    public Object[] toArray() {
        return Arrays.copyOf(elements, size());
    }

    @Override
    public boolean add(E e) {
        return set0(firstEmptyIndex(), e);
    }

    @Override
    public boolean remove(Object o) {
        return set0(indexOf(o), null);
    }

    @Override
    public boolean containsAll(@Nonnull Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean removeAll(@Nonnull Collection<?> c) {
        int mc = 0;
        for (int i = 0; i < elements.length; i++) {
            final E e = elements[i];

            if (e != null && c.contains(e)) {
                elements[i] = null;
                ++mc;
            }
        }

        return mc > 0;
    }

    @Override
    public boolean retainAll(@Nonnull Collection<?> c) {
        int mc = 0;
        for (int i = 0; i < elements.length; i++) {
            final E e = elements[i];

            if (e != null && c.contains(e)) {
                continue;
            }

            elements[i] = null;
            ++mc;
        }

        return mc > 0;
    }

    @Override
    public void clear() {
        for (int i = 0; i < elements.length; i++) {
            final E e = elements[i];

            if (e != null) {
                e.remove();
            }

            elements[i] = null;
        }
    }

    @Override
    @Nullable
    public E get(int index) {
        return isIndexOutOfBounds(index) ? null : elements[index];
    }

    @Nonnull
    public E getOrSet(int index, @Nonnull Supplier<E> entity)  {
        if (isIndexOutOfBounds(index)) {
            throw makeIndexOutOfBoundsException(index);
        }

        E e = elements[index];

        if (e == null) {
            e = elements[index] = entity.get();
        }

        return e;
    }

    @Override
    public E set(int index, E element) {
        if (isIndexOutOfBounds(index)) {
            return null;
        }

        final E previousElement = elements[index];
        elements[index] = element;

        return previousElement;
    }

    @Override
    public void add(int index, E element) {
        set0(index, element);
    }

    @Override
    public E remove(int index) {
        if (isIndexOutOfBounds(index)) {
            return null;
        }

        final E previousElement = elements[index];
        elements[index] = null;
        return previousElement;
    }

    @Override
    public int indexOf(Object o) {
        for (int i = 0; i < elements.length; i++) {
            final E e = elements[i];
            if (e != null && e.equals(o)) {
                return i;
            }
        }

        return -1;
    }

    @Nonnull
    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        final ArrayList<E> list = new ArrayList<>();

        for (; fromIndex < toIndex; ++fromIndex) {
            list.add(elements[fromIndex]);
        }

        return list;
    }

    private boolean set0(int index, E e) {
        if (isIndexOutOfBounds(index)) {
            return false;
        }

        elements[index] = e;
        return true;
    }

    private int firstEmptyIndex() {
        for (int i = 0; i < elements.length; i++) {
            if (elements[i] != null) {
                return i;
            }
        }

        return -1;
    }

    private class Iterator implements java.util.Iterator<E> {
        private final int length = elements.length;

        private int index = 0;

        @Override
        public boolean hasNext() {
            return index + 1 <= length;
        }

        @Override
        public E next() {
            return elements[index++];
        }
    }
}

package me.hapyl.fight.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * A buffer that can hold up to a given value of elements
 * before remove the oldest element.
 */
public class Buffer<E> implements List<E> {

    private final int maxCapacity;
    private final LinkedList<E> linkedList;

    public Buffer(int maxCapacity) {
        this.maxCapacity = maxCapacity;
        this.linkedList = new LinkedList<>();
    }

    @Nullable
    public E peekFirst() {
        return linkedList.peekFirst();
    }

    @Nullable
    public E pollFirst() {
        return linkedList.pollFirst();
    }

    @Nullable
    public E peekLast() {
        return linkedList.peekLast();
    }

    @Nullable
    public E pollLast() {
        return linkedList.pollLast();
    }

    @Override
    public int size() {
        return linkedList.size();
    }

    @Override
    public boolean isEmpty() {
        return linkedList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return linkedList.contains(o);
    }

    @Nonnull
    @Override
    public Iterator<E> iterator() {
        return linkedList.iterator();
    }

    @Nonnull
    @Override
    public Object[] toArray() {
        return linkedList.toArray();
    }

    @Nonnull
    @Override
    public <T> T[] toArray(@Nonnull T[] a) {
        return linkedList.toArray(a);
    }

    @Override
    public boolean add(@Nonnull E e) {
        if (size() + 1 > maxCapacity) {
            final E last = linkedList.pollFirst();
            if (last != null) {
                unbuffered(last);
            }
        }

        linkedList.addLast(e);
        return true;
    }

    // called whenever the first element is removed due to buffer size
    public void unbuffered(@Nonnull E e) {
    }

    @Override
    public boolean remove(Object o) {
        return linkedList.remove(o);
    }

    @Override
    public boolean containsAll(@Nonnull Collection<?> c) {
        return linkedList.containsAll(c);
    }

    @Override
    public boolean addAll(@Nonnull Collection<? extends E> c) {
        return addAll(0, c);
    }

    @Override
    public boolean addAll(int index, @Nonnull Collection<? extends E> c) {
        for (E e : c) {
            add(e);
        }

        return true;
    }

    @Override
    public boolean removeAll(@Nonnull Collection<?> c) {
        return linkedList.removeAll(c);
    }

    @Override
    public boolean retainAll(@Nonnull Collection<?> c) {
        return linkedList.retainAll(c);
    }

    @Override
    public void clear() {
        linkedList.clear();
    }

    @Override
    public E get(int index) {
        return linkedList.get(index);
    }

    @Override
    public E set(int index, E element) {
        return linkedList.set(index, element);
    }

    @Override
    @Deprecated
    public void add(int index, E element) {
        add(element);
    }

    @Override
    public E remove(int index) {
        return linkedList.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return linkedList.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return linkedList.lastIndexOf(o);
    }

    @Nonnull
    @Override
    public ListIterator<E> listIterator() {
        return linkedList.listIterator();
    }

    @Nonnull
    @Override
    public ListIterator<E> listIterator(int index) {
        return linkedList.listIterator(index);
    }

    @Nonnull
    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return linkedList.subList(fromIndex, toIndex);
    }

    @Override
    public String toString() {
        return "Buffer{" +
                "maxCapacity=" + maxCapacity +
                ", buffer=" + linkedList +
                '}';
    }
}

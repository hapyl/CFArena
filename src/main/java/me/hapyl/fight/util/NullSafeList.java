package me.hapyl.fight.util;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An {@link List} implementation that:
 * <ul>
 *     <li>Permits <code>null</code> values by <i>ignoring</i> them.
 *     <li>Provides {@link IndexOutOfBoundsException} protection.
 * </ul>
 */
public final class NullSafeList<T> extends ArrayList<T> {

    public NullSafeList() {
        super();
    }

    public NullSafeList(int initialCapacity) {
        super(initialCapacity);
    }

    public NullSafeList(@Nonnull Collection<? extends T> collection) {
        super();
        addAll(collection);
    }

    @Override
    public boolean add(T t) {
        if (t == null) {
            return false;
        }

        return super.add(t);
    }

    @Override
    public boolean addAll(@Nonnull Collection<? extends T> c) {
        boolean added = false;

        for (T t : c) {
            if (add(t)) {
                added = true;
            }
        }

        return added;
    }

    @Override
    public T get(int index) {
        return isIndexOutOfBounds(index) ? null : super.get(index);
    }

    @Override
    public T getFirst() {
        return isIndexOutOfBounds(0) ? null : super.getFirst();
    }

    @Override
    public T getLast() {
        return isIndexOutOfBounds(size() - 1) ? null : super.getLast();
    }

    @Override
    @Deprecated
    public boolean addAll(int index, @Nonnull Collection<? extends T> c) {
        return super.addAll(index, new NullSafeList<>(c));
    }

    @Override
    public T set(int index, T element) {
        if (isIndexOutOfBounds(index) || element == null) {
            return null;
        }

        return super.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        if (index < 0 || index > size() || element == null) {
            return;
        }

        super.add(index, element);
    }

    private boolean isIndexOutOfBounds(int index) {
        return index < 0 || index >= size();
    }

}

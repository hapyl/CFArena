package me.hapyl.fight.util;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

public interface SimpleList<E> extends List<E> {

    @Override
    default boolean isEmpty() {
        return size() == 0;
    }

    @Nonnull
    @Override
    default <T> T[] toArray(@Nonnull T[] a) {
        throw uoe("toArray");
    }

    @Override
    default boolean containsAll(@Nonnull Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }

        return true;
    }

    @Override
    @Deprecated
    default boolean addAll(@Nonnull Collection<? extends E> c) {
        throw uoe("addAll");
    }

    @Override
    @Deprecated
    default boolean addAll(int index, @Nonnull Collection<? extends E> c) {
        throw uoe("addAll");
    }

    @Override
    @Deprecated
    default boolean removeAll(@Nonnull Collection<?> c) {
        throw uoe("removeAll");
    }

    @Override
    @Deprecated
    default boolean retainAll(@Nonnull Collection<?> c) {
        throw uoe("retainAll");
    }

    @Override
    default void add(int index, E element) {
        add(element);
    }

    @Override
    default int lastIndexOf(Object o) {
        return indexOf(o);
    }

    @Nonnull
    @Override
    @Deprecated
    default ListIterator<E> listIterator() {
        throw uoe("listIterator");
    }

    @Nonnull
    @Override
    @Deprecated
    default ListIterator<E> listIterator(int index) {
        throw uoe("listIterator");
    }

    private static UnsupportedOperationException uoe(String r) {
        return new UnsupportedOperationException(r + " is unsupported in SimpleList");
    }
}

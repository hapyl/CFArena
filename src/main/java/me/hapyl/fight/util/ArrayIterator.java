package me.hapyl.fight.util;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.util.Tuple;
import me.hapyl.fight.util.collection.GenericArrayWithExpectedSize;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * An <code>array</code> implementation of a {@link Iterator}.
 *
 * @param <T> - Type.
 */
public class ArrayIterator<T> implements Iterator<T> {

    private final List<T> list;
    private int index;

    public ArrayIterator(@Nonnull Collection<T> collection) {
        list = Lists.newArrayList(collection);
        index = 0;
    }

    public ArrayIterator(@Nonnull Iterator<T> iterator) {
        list = arrayFromIterator(iterator);
        index = 0;
    }

    /**
     * Returns true if there is a next element in this iterator; false otherwise.
     *
     * @return true if there is a next element in this iterator; false otherwise.
     */
    @Override
    public boolean hasNext() {
        return index < list.size();
    }

    /**
     * Gets the next element, or null if there are not more elements.
     *
     * @return the next element or null.
     */
    @Override
    public T next() {
        return get(index++);
    }

    /**
     * Gets the {@link Tuple} of the two next elements.
     * <pre>
     *     [1, 2, 3] -> <1, 2>
     *     [2, 3]    -> <2, 3>
     *     [3]       -> <3, null>
     * </pre>
     * <p>
     * <b>This will only advance the pointer by 1</b>
     *
     * @return the tuple of the two next elements.
     */
    @Nonnull
    public GenericArrayWithExpectedSize<T> nextTuple() {
        return GenericArrayWithExpectedSize.of(get(index), get(++index));
    }

    /**
     * Gets the element by its index, or null.
     * <p>
     * <b>Does not advance the pointer.</b>
     *
     * @param i - Index.
     * @return the element or null.
     */
    @Nullable
    public T get(int i) {
        return (i < 0 || i >= list.size()) ? null : list.get(i);
    }

    public static <T> List<T> arrayFromIterator(Iterator<T> t) {
        final List<T> list = new ArrayList<>();

        while (t.hasNext()) {
            list.add(t.next());
        }

        return list;
    }
}

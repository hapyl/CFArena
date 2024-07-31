package me.hapyl.fight.util;

import me.hapyl.fight.annotate.WrapperInterface;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Iterator;

@WrapperInterface
public interface CollectionWrapper<T> extends Iterable<T> {

    @Nonnull
    Collection<T> getCollection();

    /**
     * Returns <code>true</code> if the given collection contains the given element.
     *
     * @param t - Element.
     * @return true if the given collection contains the given element.
     */
    default boolean contains(@Nonnull T t) {
        return getCollection().contains(t);
    }

    default boolean excludes(@Nonnull T t) {
        return !(getCollection().contains(t));
    }

    @Nonnull
    @Override
    default Iterator<T> iterator() {
        return getCollection().iterator();
    }
}

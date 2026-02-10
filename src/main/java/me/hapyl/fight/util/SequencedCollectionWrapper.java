package me.hapyl.fight.util;

import me.hapyl.fight.annotate.WrapperInterface;

import javax.annotation.Nonnull;
import java.util.SequencedCollection;

@WrapperInterface
public interface SequencedCollectionWrapper<T> extends CollectionWrapper<T> {

    @Nonnull
    SequencedCollection<T> getCollection();

    @Nonnull
    default T getFirst() {
        return getCollection().getFirst();
    }

    @Nonnull
    default T getLast() {
        return getCollection().getLast();
    }

}

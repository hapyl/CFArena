package me.hapyl.fight.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

// Eterna can use this
public class Iterators {

    /**
     * Iterates through a collection using an iterator.
     *
     * @param collection - Collection to iterate through.
     * @param consumer   - BiConsumer of iterator and the current element.
     */
    public static <E> void iterate(@Nonnull Collection<E> collection, @Nonnull BiConsumer<Iterator<E>, E> consumer) {
        if (collection.isEmpty()) {
            return;
        }

        final Iterator<E> iterator = collection.iterator();

        while (iterator.hasNext()) {
            final E next = iterator.next();
            consumer.accept(iterator, next);
        }
    }

    public static <E> void iterate(@Nonnull Collection<E> collection, @Nonnull Consumer<Element<E>> consumer) {
        iterate(collection, (iterator, e) -> {
            final Element<E> element = new Element<>(e);
            consumer.accept(element);

            if (element.remove) {
                iterator.remove();
            }
        });
    }

    public static <E> void removeIf(@Nonnull Collection<E> collection, @Nonnull Predicate<E> predicate, @Nullable Consumer<E> andThen) {
        iterate(collection, (iterator, e) -> {
            if (predicate.test(e)) {
                iterator.remove();
                if (andThen != null) {
                    andThen.accept(e);
                }
            }
        });
    }

    public static final class Element<E> {

        public final E element;
        private boolean remove;

        public Element(E element) {
            this.element = element;
            this.remove = false;
        }

        @Nonnull
        public E get() {
            return element;
        }

        public void remove() {
            this.remove = true;
        }
    }

}

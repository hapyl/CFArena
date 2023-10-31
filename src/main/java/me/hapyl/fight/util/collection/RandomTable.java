package me.hapyl.fight.util.collection;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomTable<E> {

    private final List<E> collection;

    public RandomTable() {
        collection = new ArrayList<>();
    }

    public RandomTable<E> add(E e) {
        collection.add(e);
        return this;
    }

    public RandomTable<E> remove(E e) {
        collection.remove(e);
        return this;
    }

    public void clear() {
        this.collection.clear();
    }

    @Nonnull
    public E getRandomElementNot(@Nonnull E not) {
        if (collection.size() == 1) {
            throw new IllegalStateException("size() == 1");
        }

        final E randomElement = getRandomElement();

        if (randomElement.equals(not)) {
            return getRandomElementNot(not);
        }

        return randomElement;
    }

    @Nonnull
    public E getRandomElement() {
        if (collection.isEmpty()) {
            throw new IllegalStateException("size() == 0");
        }

        return collection.get(ThreadLocalRandom.current().nextInt(collection.size()));
    }

}

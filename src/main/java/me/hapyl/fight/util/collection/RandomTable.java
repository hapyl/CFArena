package me.hapyl.fight.util.collection;

import com.google.common.collect.Lists;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

public class RandomTable<E> {

    private final List<E> collection;
    private final Random random;

    public RandomTable(@Nonnull List<E> collection) {
        this.collection = collection;
        this.random = new Random();
    }

    public RandomTable() {
        this(Lists.newArrayList());
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

        return collection.get(random.nextInt(collection.size()));
    }

}

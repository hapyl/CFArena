package me.hapyl.fight.util.collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public final class GenericArrayWithExpectedSize<T> {

    public final int length;

    private final T[] array;

    @SuppressWarnings("unchecked")
    public GenericArrayWithExpectedSize(final int size) {
        this.array = (T[]) new Object[size];
        this.length = size;
    }

    @Nullable
    public T get(final int index) {
        if (isIndexOutOfBound(index)) {
            return null;
        }

        return array[index];
    }

    @Nullable
    public T set(final int index, @Nullable final T t) {
        if (isIndexOutOfBound(index)) {
            return null;
        }

        final T previous = array[index];
        array[index] = t;

        return previous;
    }

    public int size() {
        return length;
    }

    public void forEach(@Nonnull Consumer<? super T> action) {
        for (T t : array) {
            action.accept(t);
        }
    }

    private boolean isIndexOutOfBound(int index) {
        return index < 0 || index >= array.length;
    }

    private GenericArrayWithExpectedSize<T> set(@Nonnull T[] elements) {
        if (length != elements.length) {
            throw new IllegalArgumentException("");
        }

        System.arraycopy(elements, 0, this.array, 0, elements.length);
        return this;
    }

    @SafeVarargs
    @Nonnull
    public static <T> GenericArrayWithExpectedSize<T> of(final @Nonnull T... elements) {
        final int length = elements.length;

        if (length == 0) {
            throw new IllegalArgumentException("illegal length: " + length);
        }

        return new GenericArrayWithExpectedSize<T>(length).set(elements);
    }
}

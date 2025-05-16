package me.hapyl.fight.util;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Represents an immutable array with fixed length.
 *
 * @param <T> - The type of the array.
 */
public interface ImmutableArray<T> extends Iterable<T> {
    
    /**
     * Gets the element at the given index, throws {@link IndexOutOfBoundsException} if the index is out of bounds.
     *
     * @param index - The index to retrieve the element at.
     * @return the element at the given index.
     * @throws IndexOutOfBoundsException if the index is out of bounds.
     * @implNote Implementation is required to throw {@link IndexOutOfBoundsException} either by explicit index check, or directly accessing the underlying array.
     */
    @Nonnull
    T get(int index) throws IndexOutOfBoundsException;
    
    /**
     * Gets the element at the given index, or default is index is out of bounds.
     * <p>The default implementation is as follows:
     * <pre>{@code
     * @CheckForNull
     * default T getOrDefault(int index, @CheckForNull T def) {
     *     try {
     *         return get(index);
     *     }
     *     catch (IndexOutOfBoundsException e) {
     *         return def;
     *     }
     * }}</pre>
     *
     * @param index - The index to retrieve the element at.
     * @param def   - The default element to return if index is out of bounds.
     * @return the element at the given index.
     */
    @CheckForNull
    default T getOrDefault(int index, @CheckForNull T def) {
        try {
            return get(index);
        }
        catch (IndexOutOfBoundsException e) {
            return def;
        }
    }
    
    /**
     * Gets the element at the given index, or {@code null} is index is out of bounds.
     *
     * @param index - The index to retrieve the element at.
     * @return the element at the given index.
     */
    @Nullable
    default T getOrNull(int index) {
        return getOrDefault(index, null);
    }
    
    /**
     * Gets the length of this array.
     * <p>Remember that the array is meant to be fixed length, so implementation should return a statically typed value rather than calling {@code array.length}.
     *
     * @return the length of this array.
     */
    int length();
    
    /**
     * Gets a {@link Stream} of this array.
     *
     * @return a {@link Stream} of this array.
     */
    @Nonnull
    default Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
    
    /**
     * Gets the {@link Iterator} for this array.
     *
     * @return the iterator for this array.
     */
    @Nonnull
    @Override
    default Iterator<T> iterator() {
        return new Iterator<>() {
            private int index;
            
            @Override
            public boolean hasNext() {
                return index < length();
            }
            
            @Override
            public T next() {
                return get(index++);
            }
        };
    }
    
}

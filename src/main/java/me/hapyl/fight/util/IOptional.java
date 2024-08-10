package me.hapyl.fight.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * {@link java.util.Optional} impl.
 */
public class IOptional<T> {

    private static final IOptional<?> EMPTY = new IOptional<>(null);

    @Nullable protected final T value;

    protected IOptional(@Nullable T value) {
        this.value = value;
    }

    @Nonnull
    public T get() {
        if (value == null) {
            throw new NoSuchElementException("optional is empty");
        }

        return value;
    }

    @Nullable
    public T getOrNull() {
        return value;
    }

    public boolean isEmpty() {
        return this.value == null;
    }

    public boolean isPresent() {
        return this.value != null;
    }

    public IOptionalCallback ifPresent(@Nonnull Consumer<T> action) {
        if (isPresent()) {
            action.accept(value);
            return new IOptionalCallback() {
                @Override
                public IOptionalCallback orElse(@Nonnull Runnable runnable) {
                    return this;
                }
            };
        }

        return new IOptionalCallback() {
        };
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public static <T> IOptional<T> empty() {
        return (IOptional<T>) EMPTY;
    }

    @Nonnull
    public static <T> IOptional<T> of(@Nullable T t) {
        return t != null ? new IOptional<>(t) : empty();
    }

    public interface IOptionalCallback {

        default IOptionalCallback orElse(@Nonnull Runnable runnable) {
            runnable.run();
            return this;
        }

        default IOptionalCallback always(@Nonnull Runnable runnable) {
            runnable.run();
            return this;
        }

    }
}

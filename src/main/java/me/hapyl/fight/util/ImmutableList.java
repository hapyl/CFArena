package me.hapyl.fight.util;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class ImmutableList<T> extends ArrayList<T> {

    protected ImmutableList(@Nonnull T[] array) {
        for (T t : array) {
            if (t != null) {
                super.add(t);
            }
        }
    }

    @Deprecated(forRemoval = true)
    private ImmutableList(int initialCapacity) {
        super(initialCapacity);
    }

    @Deprecated(forRemoval = true)
    private ImmutableList() {
        super();
    }

    @Deprecated(forRemoval = true)
    private ImmutableList(@Nonnull Collection<? extends T> c) {
        super(c);
    }

    public <D> Applier<D> apply(@Nonnull D d) {
        return new Applier<>(d);
    }

    @Override
    @Deprecated(forRemoval = true)
    public final void clear() {
        throw uoe();
    }

    @Override
    @Deprecated(forRemoval = true)
    public final T set(int index, T element) {
        throw uoe();
    }

    @Override
    @Deprecated(forRemoval = true)
    public final boolean add(T vehicleDirection) {
        throw uoe();
    }

    @Override
    @Deprecated(forRemoval = true)
    public final void add(int index, T element) {
        throw uoe();
    }

    @Override
    @Deprecated(forRemoval = true)
    public final void addFirst(T element) {
        throw uoe();
    }

    @Override
    @Deprecated(forRemoval = true)
    public final void addLast(T element) {
        throw uoe();
    }

    @Override
    @Deprecated(forRemoval = true)
    public final boolean addAll(Collection<? extends T> c) {
        throw uoe();
    }

    @Override
    @Deprecated(forRemoval = true)
    public final boolean addAll(int index, Collection<? extends T> c) {
        throw uoe();
    }

    @Override
    @Deprecated(forRemoval = true)
    public final boolean remove(Object o) {
        throw uoe();
    }

    @Override
    @Deprecated(forRemoval = true)
    public final T removeLast() {
        throw uoe();
    }

    @Override
    @Deprecated(forRemoval = true)
    public final T removeFirst() {
        throw uoe();
    }

    @Override
    @Deprecated(forRemoval = true)
    public final T remove(int index) {
        throw uoe();
    }

    @Override
    @Deprecated(forRemoval = true)
    public final void trimToSize() {
        throw uoe();
    }

    @Override
    @Deprecated(forRemoval = true)
    public final void ensureCapacity(int minCapacity) {
        throw uoe();
    }

    @Override
    @Deprecated(forRemoval = true)
    public final boolean removeAll(Collection<?> c) {
        throw uoe();
    }

    @Override
    @Deprecated(forRemoval = true)
    public final boolean retainAll(Collection<?> c) {
        throw uoe();
    }

    @Override
    @Deprecated(forRemoval = true)
    public final boolean removeIf(Predicate<? super T> filter) {
        throw uoe();
    }

    @Override
    @Deprecated(forRemoval = true)
    public final void replaceAll(UnaryOperator<T> operator) {
        throw uoe();
    }

    @Override
    @Deprecated(forRemoval = true)
    protected final void removeRange(int fromIndex, int toIndex) {
        throw uoe();
    }

    private UnsupportedOperationException uoe() {
        return new UnsupportedOperationException();
    }

    @SafeVarargs
    @Nonnull
    public static <T> ImmutableList<T> of(@Nonnull T... of) {
        return new ImmutableList<>(of);
    }

    @Nonnull
    public static <T> ImmutableList<T> of(@Nonnull Collection<T> of) {
        return new ImmutableList<>(of);
    }

    public final class Applier<D> {

        private final D d;

        private Applier(@Nonnull D d) {
            this.d = d;
        }

        public Applier<D> when(@Nonnull T t, @Nonnull Consumer<D> action) {
            if (contains(t)) {
                action.accept(d);
            }

            return this;
        }

        public ImmutableList<T> list() {
            return ImmutableList.this;
        }
    }

}

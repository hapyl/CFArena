package me.hapyl.fight.util.collection;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * A concurrent table impl.
 *
 * @param <R> - Row.
 * @param <C> - Column.
 * @param <V> - Value.
 */
public final class ConcurrentTable<R, C, V> {

    private final Map<Cell<R, C>, V> hashMap;

    public ConcurrentTable() {
        hashMap = Maps.newConcurrentMap();
    }

    @Nullable
    public V get(@Nonnull R row, @Nonnull C column) {
        return hashMap.get(Cell.of(row, column));
    }

    /**
     * Matches two keys using OR, where if either of the values or both
     * present contains a value, it will be added into a hash set.
     *
     * @param row    - Key 1.
     * @param column - Key 2.
     * @return set with corresponding values.
     */
    @Nonnull
    public Set<V> matchOR(@Nullable R row, @Nullable C column) {
        Set<V> set = Sets.newHashSet();

        if (row == null && column == null) {
            return set;
        }

        for (Map.Entry<Cell<R, C>, V> entry : entrySet()) {
            final Cell<R, C> cell = entry.getKey();
            final V value = entry.getValue();

            if (cell.row.equals(row)) {
                set.add(value);
            }
            else if (cell.column.equals(column)) {
                set.add(value);
            }
        }

        return set;
    }

    public Set<Map.Entry<Cell<R, C>, V>> entrySet() {
        return hashMap.entrySet();
    }

    @Nonnull
    public Optional<V> getOptional(@Nonnull R row, @Nonnull C column) {
        final V v = get(row, column);
        return v == null ? Optional.empty() : Optional.of(v);
    }

    public V getOrDefault(@Nonnull R row, @Nonnull C column, V def) {
        final V v = get(row, column);
        return v == null ? def : v;
    }

    public boolean contains(@Nonnull R row, @Nonnull C column) {
        return hashMap.containsKey(Cell.of(row, column));
    }

    public boolean contains(@Nonnull V v) {
        return hashMap.containsValue(v);
    }

    public boolean containsRow(@Nonnull R r) {
        for (Map.Entry<Cell<R, C>, V> entry : entrySet()) {
            if (entry.getKey().row.equals(r)) {
                return true;
            }
        }

        return false;
    }

    @Nullable
    public V remove(@Nonnull R row, @Nonnull C column) {
        return hashMap.remove(Cell.of(row, column));
    }

    public void forEach(Consumer<V> consumer) {
        hashMap.values().forEach(consumer);
    }

    public void clear() {
        hashMap.clear();
    }

    // nullable value insertion will be considered as removal
    @Nullable
    public V put(@Nonnull R row, @Nonnull C column, @Nullable V v) {
        if (v == null) {
            remove(row, column);
            return null;
        }

        return hashMap.put(Cell.of(row, column), v);
    }

    public record Cell<R, C>(R row, C column) {

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (obj instanceof ConcurrentTable.Cell<?, ?> other) {
                return Objects.equals(this.row, other.row) && Objects.equals(this.column, other.column);
            }

            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, column);
        }

        public static <R, C> Cell<R, C> of(R row, C column) {
            return new Cell<>(row, column);
        }
    }

}

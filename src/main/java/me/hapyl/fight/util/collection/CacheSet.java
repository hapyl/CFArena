package me.hapyl.fight.util.collection;

import me.hapyl.spigotutils.module.util.Validate;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Function;

/**
 * A {@link Set} implementation as a 'cache'.
 *
 * @param <K> - Key.
 */
public class CacheSet<K> implements Set<K> {

    private final Set<Entry> set;
    private final long expireAfter;

    public CacheSet(long expireAfter) {
        Validate.isTrue(expireAfter > 0, "Expiration time cannot be negative.");

        this.set = new HashSet<>();
        this.expireAfter = expireAfter;
    }

    @Override
    public int size() {
        return checkExpire(Set::size);
    }

    @Override
    public boolean isEmpty() {
        return checkExpire(Set::isEmpty);
    }

    @Override
    public boolean contains(Object o) {
        return checkExpire(set -> set.contains(new Entry(o)));
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public List<K> toList() {
        final List<K> list = new ArrayList<>();

        checkExpire(set -> {
            set.forEach(entry -> list.add((K) entry.value));
            return null;
        });

        return list;
    }

    @Nonnull
    @Override
    public Iterator<K> iterator() {
        return toList().iterator();
    }

    @Nonnull
    @Override
    public Object[] toArray() {
        return toList().toArray();
    }

    @Nonnull
    @Override
    public <T> T[] toArray(@Nonnull T[] a) {
        return toList().toArray(a);
    }

    @Override
    public boolean add(K k) {
        return set.add(new Entry(k)); // no need to check for expiration on insert
    }

    @Override
    public boolean remove(Object o) {
        return set.remove(new Entry(o));
    }

    @Override
    public boolean containsAll(@Nonnull Collection<?> c) {
        for (Object object : c) {
            if (!contains(object)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean addAll(@Nonnull Collection<? extends K> c) {
        boolean added = false;

        for (K k : c) {
            if (add(k)) {
                added = true;
            }
        }

        return added;
    }

    @Override
    @Deprecated
    public boolean retainAll(@Nonnull Collection<?> c) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(@Nonnull Collection<?> c) {
        boolean removed = false;

        for (Object object : c) {
            if (remove(object)) {
                removed = true;
            }
        }

        return removed;
    }

    @Override
    public void clear() {
        set.clear();
    }

    @Nonnull
    public String toString(@Nonnull K k) {
        return String.valueOf(k);
    }

    @Override
    public final String toString() {
        final StringBuilder builder = new StringBuilder("Cache{e=" + expireAfter + ",v=[");

        int index = 0;
        for (K k : this) {
            if (index++ != 0) {
                builder.append(", ");
            }

            builder.append(toString(k));
        }

        return builder.append("]").toString();
    }

    private <T> T checkExpire(Function<Set<Entry>, T> fn) {
        set.removeIf(Entry::isExpired);

        return fn.apply(set);
    }

    class Entry {
        Object value;
        long addedAt;

        public Entry(Object k) {
            this.value = k;
            this.addedAt = System.currentTimeMillis();
        }

        public boolean isExpired() {
            return System.currentTimeMillis() - addedAt >= expireAfter;
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }

            if (object == null || getClass() != object.getClass()) {
                return false;
            }

            final Entry entry = (Entry) object;
            return Objects.equals(value, entry.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }
}

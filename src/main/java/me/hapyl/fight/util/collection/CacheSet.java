package me.hapyl.fight.util.collection;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.util.Validate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A {@link Set} implementation as a 'cache'.
 *
 * @param <K> - Key.
 */
public class CacheSet<K> implements Set<K> {

    private final Set<Entry> set;
    private final long expireAfterMillis;

    public CacheSet(long expireAfterMillis) {
        Validate.isTrue(expireAfterMillis > 0, "Expiration time cannot be negative.");

        this.set = new HashSet<>();
        this.expireAfterMillis = expireAfterMillis;
    }

    @Override
    public int size() {
        return checkExpireAnd(Set::size);
    }

    @Override
    public boolean isEmpty() {
        return checkExpireAnd(Set::isEmpty);
    }

    @Override
    public boolean contains(Object o) {
        return checkExpireAnd(set -> set.contains(new Entry(o)));
    }

    @Nullable
    public K findFirst(@Nonnull Predicate<K> predicate) {
        checkExpire();

        for (K k : this) {
            if (predicate.test(k)) {
                return k;
            }
        }

        return null;
    }

    @Nonnull
    public List<K> findAll(@Nonnull Predicate<K> predicate) {
        checkExpire();
        List<K> matches = Lists.newArrayList();

        for (K k : this) {
            if (predicate.test(k)) {
                matches.add(k);
            }
        }

        return matches;
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public List<K> toList() {
        final List<K> list = new ArrayList<>();

        checkExpireAnd(set -> {
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
        final StringBuilder builder = new StringBuilder("Cache{e=" + expireAfterMillis + ",v=[");

        int index = 0;
        for (K k : this) {
            if (index++ != 0) {
                builder.append(", ");
            }

            builder.append(toString(k));
        }

        return builder.append("]").toString();
    }

    private void checkExpire() {
        set.removeIf(Entry::isExpired);
    }

    private <T> T checkExpireAnd(Function<Set<Entry>, T> fn) {
        checkExpire();
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
            return System.currentTimeMillis() - addedAt >= expireAfterMillis;
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

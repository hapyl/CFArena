package me.hapyl.fight.util;

import com.google.common.collect.Sets;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.Set;

public abstract class RetainSet<K> implements Iterable<K> {

    private final Set<K> set;
    private final Set<K> retainSet;

    public RetainSet() {
        this.set = Sets.newHashSet();
        this.retainSet = Sets.newHashSet();
    }

    public void add(@Nonnull K k) {
        final boolean newElement = set.add(k);
        retainSet.add(k);

        if (newElement) {
            onAdd(k);
        }
    }

    public void remove(@Nonnull K k) {
        set.remove(k);
    }

    public boolean contains(@Nonnull K k) {
        return set.contains(k);
    }

    public boolean isRetained(@Nonnull K k) {
        return retainSet.contains(k);
    }

    /**
     * Retains the elements.
     * <br>
     * Must be called after {@link #add(Object)} to retain.
     */
    public void retain() {
        set.removeIf(k -> {
            if (retainSet.contains(k)) {
                return false;
            }

            onRemove(k);
            return true;
        });

        retainSet.forEach(this::onRetain);
        retainSet.clear();
    }

    /**
     * Called once when a <code>K</code> is added to the set for the first time.
     *
     * @param k - K.
     */
    public abstract void onAdd(@Nonnull K k);

    /**
     * Called when {@link #retain()} is called, and the <code>K</code> is not retained.
     *
     * @param k - K.
     */
    public abstract void onRemove(@Nonnull K k);

    /**
     * Called when {@link #retain()} is called, and the <code>K</code> is retained.
     *
     * @param k - K.
     */
    public void onRetain(@Nonnull K k) {
    }

    /**
     * Clears the set and calls {@link #onRemove(Object)} on each remaining element.
     */
    public void clear() {
        set.forEach(this::onRemove);
        set.clear();

        retainSet.clear();
    }

    @Nonnull
    @Override
    public Iterator<K> iterator() {
        return set.iterator();
    }
}

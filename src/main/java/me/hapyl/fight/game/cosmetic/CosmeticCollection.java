package me.hapyl.fight.game.cosmetic;

import com.google.common.collect.Sets;

import javax.annotation.Nonnull;
import java.util.Set;

// This implementation is kinda weird since how enums are used as registries
public enum CosmeticCollection {

    NONE {
        private final RuntimeException error = new IllegalArgumentException(this + " cannot have items");

        @Override
        public void addItem(@Nonnull BelongsToCollection item) {
            throw error;
        }

        @Override
        public Set<BelongsToCollection> getItems() {
            throw error;
        }

        @Override
        public void removeItem(@Nonnull BelongsToCollection item) {
            throw error;
        }
    },

    PREFIX,

    ;

    private final Set<BelongsToCollection> items;

    CosmeticCollection() {
        items = Sets.newHashSet();
    }

    public void addItem(@Nonnull BelongsToCollection item) {
        final CosmeticCollection collection = item.getCollection();

        if (collection != this) {
            throw new IllegalArgumentException("item must belong to %s collection, not %s".formatted(this, collection));
        }

        items.add(item);
    }


    /**
     * Gets a copy of the collection items.
     *
     * @return a copy of the collection items.
     */
    public Set<BelongsToCollection> getItems() {
        return Sets.newHashSet(items);
    }

    public void removeItem(@Nonnull BelongsToCollection item) {
        items.remove(item);
    }
}

package me.hapyl.fight.game.cosmetic;

import javax.annotation.Nullable;

public interface BelongsToCollection {

    @Nullable
    CosmeticCollection getCollection();

    void setCollection(@Nullable CosmeticCollection collection);

    default void setCollectionAndAdd(@Nullable CosmeticCollection collection) {
        if (collection == null) {
            final CosmeticCollection cls = getCollection();
            if (cls == null) {
                return;
            }

            cls.removeItem(this);
        } else {
            setCollection(collection);
            collection.addItem(this);
        }
    }

}

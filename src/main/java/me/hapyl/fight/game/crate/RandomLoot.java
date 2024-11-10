package me.hapyl.fight.game.crate;

import me.hapyl.fight.game.cosmetic.Cosmetic;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class RandomLoot<T extends Cosmetic> {

    private final ItemContents<T> contents;
    private final RandomLootSchema<T> schema;

    public RandomLoot() {
        this.contents = new ItemContents<>();
        this.schema = new RandomLootSchema<>();
    }

    @Nonnull
    public final ItemContents<T> getContents() {
        return contents;
    }

    @SafeVarargs
    public final RandomLoot<T> setContents(@Nonnull T... items) {
        final ItemContents<T> contents = getContents();

        for (T item : items) {
            contents.addItem(item);
        }

        updateSchema();
        return this;
    }

    public final void setContents(@Nonnull List<T> items) {
        getContents().setContents(items);
        updateSchema();
    }

    @Nonnull
    public final RandomLootSchema<T> getSchema() {
        return schema;
    }

    public final void updateSchema() {
        getSchema().set(getContents());
    }

    @Override
    public String toString() {
        return "{" +
                "contents=" + contents +
                ", schema=" + schema +
                '}';
    }

}

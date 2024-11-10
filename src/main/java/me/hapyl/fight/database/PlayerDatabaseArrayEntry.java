package me.hapyl.fight.database;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class PlayerDatabaseArrayEntry<T> extends PlayerDatabaseEntry {

    public final int size;

    public PlayerDatabaseArrayEntry(@Nonnull PlayerDatabase playerDatabase, @Nonnull String parent, int size) {
        super(playerDatabase, parent);

        this.size = size;
    }

    @Nonnull
    public abstract T[] newArray();

    @Nullable // null is fine
    public abstract T fromString(@Nonnull String string);

    @Nonnull
    public abstract String toString(@Nonnull T t);

    @Nonnull
    public T[] getArray() {
        final T[] array = newArray();

        for (int i = 0; i < 9; i++) {
            array[i] = fromString(getValue(getPath(i), ""));
        }

        return array;
    }

    public void saveArray(@Nonnull T[] array) {
        if (array.length != size) {
            throw new IllegalArgumentException("Length must be %s, not %s!".formatted(size, array.length));
        }

        for (int i = 0; i < array.length; i++) {
            final T value = array[i];
            setValue(getPath(i), value == null ? "" : toString(value));
        }
    }

    private String getPath(int index) {
        return "slot_" + index;
    }
}

package me.hapyl.fight.util;

public interface Sizeable {

    int size();

    default int length() {
        return size();
    }

    default boolean isIndexOutOfBounds(int index) {
        return index < 0 || index >= size();
    }

    default IndexOutOfBoundsException makeIndexOutOfBoundsException(int index) {
        return new IndexOutOfBoundsException("Index %s is out bound for size %s!".formatted(index, size()));
    }

}

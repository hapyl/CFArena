package me.hapyl.fight.game.entity;

import java.util.Objects;

public class MemoryKey {

    private final String name;

    public MemoryKey(String name) {
        this.name = name.toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final MemoryKey memoryKey = (MemoryKey) o;
        return Objects.equals(name, memoryKey.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}

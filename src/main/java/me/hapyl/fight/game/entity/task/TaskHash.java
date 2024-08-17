package me.hapyl.fight.game.entity.task;

import javax.annotation.Nullable;
import java.util.Objects;

public class TaskHash {

    @Nullable
    private final Class<?> hash;

    public TaskHash() {
        this(null);
    }

    public TaskHash(@Nullable Class<?> hash) {
        this.hash = hash;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TaskHash otherHash)) {
            return false;
        }

        // compare hash code if anonymous
        if (hash == null) {
            return this.hashCode() == o.hashCode();
        }

        // Otherwise compare enums
        return Objects.equals(hash, otherHash.hash);
    }

    @Override
    public int hashCode() {
        // anonymous hash
        if (hash == null) {
            return super.hashCode();
        }

        return Objects.hash(hash);
    }

    @Override
    public String toString() {
        return String.valueOf(hash);
    }
}

package me.hapyl.fight.database.key;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public final class DatabaseKeyImpl implements DatabaseKey {

    private final String key;

    DatabaseKeyImpl(@Nonnull String key) {
        this.key = key;
    }

    @Nonnull
    @Override
    public String getKey() {
        return key;
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        final DatabaseKeyImpl that = (DatabaseKeyImpl) other;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(key);
    }
}

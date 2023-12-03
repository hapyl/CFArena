package me.hapyl.fight.perplayer;

import org.bukkit.entity.Entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Optional;

public class PlayerEntityArray<T extends Entity> implements Iterable<PlayerEntity<T>> {

    private final LinkedHashMap<Integer, PlayerEntity<T>> array;
    public final int length;

    public PlayerEntityArray(int size) {
        this.array = new LinkedHashMap<>();
        this.length = size;
    }

    @Nullable
    public PlayerEntity<T> get(int index) {
        if (index < 0 || index >= length) {
            return null;
        }

        return array.get(index);
    }

    @Nullable
    public T getEntity(int index) {
        final PlayerEntity<T> entity = get(index);

        if (entity == null) {
            return null;
        }

        return entity.getEntity();
    }

    @Nonnull
    public Optional<T> getEntityOptional(int index) {
        final PlayerEntity<T> playerEntity = get(index);

        if (playerEntity == null) {
            return Optional.empty();
        }

        final T entity = playerEntity.getEntity();
        return entity == null ? Optional.empty() : Optional.of(entity);
    }

    public void set(int index, PlayerEntity<T> t) {
        if (index < 0 || index >= length) {
            return;
        }

        array.put(index, t);
    }

    @Nullable
    public PlayerEntity<T> remove(int index) {
        return array.remove(index);
    }

    @Override
    public Iterator<PlayerEntity<T>> iterator() {
        return array.values().iterator();
    }
}

package me.hapyl.fight.util;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.fight.game.task.GameTask;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import javax.annotation.Nonnull;
import java.util.ArrayDeque;
import java.util.Set;
import java.util.function.Consumer;

public final class EntitySpawner<T extends Entity> implements Runnable {

    private final Entities<T> entity;
    private final Consumer<T> consumer;

    private final Set<T> spawnedEntities;
    private final ArrayDeque<EntitySpawnerOperation<T>> operations;

    private int wait;
    private EntitySpawnerOperation<T> current;
    private GameTask task;

    private EntitySpawner(Entities<T> entity, Consumer<T> consumer) {
        this.entity = entity;
        this.consumer = consumer;
        this.spawnedEntities = Sets.newHashSet();
        this.operations = new ArrayDeque<>();
    }

    public EntitySpawner<T> then(@Nonnull EntitySpawnerOperation<T> operation) {
        this.operations.add(operation);
        return this;
    }

    @Override
    public void run() {
        if (task != null) {
            throw new IllegalStateException("Duplicate run!");
        }

        task = new GameTask() {
            @Override
            public void run() {
                tick();
            }
        }.runTaskTimer(0, 1);
    }

    private void tick() {
        // Wait
        if (wait > 0 && current != null) {
            current.tick(this);
            wait--;
            return;
        }

        current = operations.pollFirst();

        if (current == null) {
            spawnedEntities.forEach(Entity::remove);
            task.cancel();
            return;
        }

        current.run(this);
    }

    private void doSpawn(Location location) {
        spawnedEntities.add(entity.spawn(location, consumer));
    }

    public static <T extends Entity> EntitySpawnerOperation<T> spawn(@Nonnull Location location) {
        return spawn(location, 1);
    }

    public static <T extends Entity> EntitySpawnerOperation<T> spawn(@Nonnull Location location, int spawnCount) {
        return spawner -> {
            for (int i = 0; i < spawnCount; i++) {
                spawner.doSpawn(location);
            }
        };
    }

    public static <T extends Entity> EntitySpawnerOperation<T> wait(int wait) {
        return tick(wait, ignored -> {
        });
    }

    public static <T extends Entity> EntitySpawnerOperation<T> tick(int times, @Nonnull Consumer<T> consumer) {
        return new EntitySpawnerOperation<>() {
            @Override
            public void run(@Nonnull EntitySpawner<T> spawner) {
                spawner.wait = times;
            }

            @Override
            public void tick(@Nonnull EntitySpawner<T> spawner) {
                spawner.spawnedEntities.forEach(consumer);
            }
        };
    }

    public static <T extends Entity> EntitySpawnerOperation<T> forEach(@Nonnull Consumer<T> consumer) {
        return spawner -> spawner.spawnedEntities.forEach(consumer);
    }

    @Nonnull
    public static <T extends Entity> EntitySpawner<T> of(@Nonnull Entities<T> entity, @Nonnull Consumer<T> consumer) {
        return new EntitySpawner<>(entity, consumer);
    }

    public interface EntitySpawnerOperation<T extends Entity> {

        void run(@Nonnull EntitySpawner<T> spawner);

        default void tick(@Nonnull EntitySpawner<T> spawner) {
        }

    }
}

package me.hapyl.fight.game.entity;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.entity.named.NamedEntityType;
import me.hapyl.fight.game.entity.named.NamedGameEntity;
import me.hapyl.fight.game.task.GameTask;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.Consumer;

/**
 * These entities are made with multiple ones, but they share the health.
 */
public class MultiPartLivingGameEntity<T extends LivingEntity> extends NamedGameEntity<T> {

    private final Set<Part<?>> parts;

    public MultiPartLivingGameEntity(NamedEntityType type, T entity) {
        super(type, entity);

        parts = Sets.newHashSet();
    }

    public <E extends LivingEntity> Part<E> createPart(@Nonnull Location location, @Nonnull Entities<E> type, @Nullable Consumer<Part<E>> consumer) {
        final Part<E> gameEntity = CF.createEntity(location, type, new ConsumerFunction<>() {
            @Nonnull
            @Override
            public Part<E> apply(@Nonnull E t) {
                final Part<E> part = new Part<>(t);

                if (consumer != null) {
                    consumer.accept(part);
                }

                return part;
            }
        });

        parts.add(gameEntity);
        return gameEntity;
    }

    @Override
    public void kill() {
        super.kill();

        parts.forEach(Part::forceRemove);
        parts.clear();
    }

    public void simulateHit() {
        entity.playHurtAnimation(0.0f);
        parts.forEach(part -> part.entity.playHurtAnimation(0.0f));
    }

    public <E extends LivingEntity> Part<E> createPart(@Nonnull Entities<E> type, @Nullable Consumer<Part<E>> consumer) {
        return createPart(getLocation(), type, consumer);
    }

    public class Part<E extends LivingEntity> extends LivingGameEntity {

        public final E entity;

        public Part(@Nonnull E entity) {
            super(entity);
            this.entity = entity;
        }

        /**
         * This will return the owner, or parent of the entity, not the actual part!
         * Use the public {@link #entity} field to access entity.
         */
        @Nonnull
        @Override
        @Deprecated
        public LivingGameEntity getGameEntity() {
            return MultiPartLivingGameEntity.this;
        }

        public void schedule(Runnable run, int delay) {
            GameTask.runLater(run, delay);
        }
    }
}

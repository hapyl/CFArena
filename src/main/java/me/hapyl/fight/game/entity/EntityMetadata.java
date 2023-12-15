package me.hapyl.fight.game.entity;

import me.hapyl.fight.game.Event;

import javax.annotation.Nonnull;

public class EntityMetadata {

    public final LivingGameEntity entity;

    /**
     * Whenever an entity can move.
     */
    public final Metadata<Boolean> canMove = new Metadata<>(true);

    public EntityMetadata(LivingGameEntity entity) {
        this.entity = entity;
    }

    public static class Metadata<T> {

        @Nonnull
        private T value;

        public Metadata(@Nonnull T value) {
            this.value = value;
        }

        @Nonnull
        public T getValue() {
            return value;
        }

        public void setValue(@Nonnull T value) {
            if (this.value == value) {
                return;
            }

            this.value = value;
            onSet(value);
        }

        public boolean isTrue() {
            if (value instanceof Boolean b) {
                return b;
            }
            else if (value instanceof Number n) {
                return n.intValue() == 1;
            }

            return false;
        }

        public boolean isFalse() {
            return !isTrue();
        }

        public void notify(@Nonnull GameEntity entity) {
        }

        @Event
        public void onSet(@Nonnull T value) {
        }

        public boolean isFalseAndNotify(@Nonnull GameEntity entityToNotify) {
            final boolean b = isFalse();

            if (b) {
                notify(entityToNotify);
            }

            return b;
        }

        public boolean isTrueAndNotify(@Nonnull GameEntity entityToNotify) {
            final boolean b = isTrue();

            if (b) {
                notify(entityToNotify);
            }

            return b;
        }
    }

}

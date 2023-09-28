package me.hapyl.fight.game.entity;

import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class EntityMetadata {

    /**
     * Whenever an entity can move.
     */
    public final Metadata<Boolean> CAN_MOVE = new Metadata<>(true);

    /**
     * Whenever the entity can be controller by crowd abilities.
     */
    public final Metadata<Boolean> CC_AFFECT = new Metadata<>(true) {
        @Override
        public void notify(@Nonnull GameEntity entity) {
            entity.sendMessage("&cThis creature is immune to crowd control abilities!");
            entity.playPlayerSound(Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
        }
    };

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
            this.value = value;
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

        public boolean isFalseAndNotify(@Nonnull GameEntity entity) {
            final boolean b = isFalse();

            if (b) {
                notify(entity);
            }

            return b;
        }

        public boolean isTrueAndNotify(@Nonnull GameEntity entity) {
            final boolean b = isTrue();

            if (b) {
                notify(entity);
            }

            return b;
        }
    }

}

package me.hapyl.fight.game;

import me.hapyl.fight.event.DamageInstance;

import javax.annotation.Nonnull;

public interface DamageFormat {

    DamageFormat DEFAULT = new DamageFormat() {
        @Nonnull
        @Override
        public String format(@Nonnull DamageInstance instance) {
            return "%.0f".formatted(instance.getDamage());
        }
    };

    @Nonnull
    String format(@Nonnull DamageInstance instance);

}

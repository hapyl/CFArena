package me.hapyl.fight.game;

import javax.annotation.Nonnull;

public interface DamageFormat {

    DamageFormat DEFAULT = new DamageFormat() {
        @Nonnull
        @Override
        public String getFormat() {
            return "{damage}";
        }
    };

    @Nonnull
    String getFormat();

    default String format(double damage) {
        return getFormat().replace("{damage}", "%.1f".formatted(damage));
    }

}

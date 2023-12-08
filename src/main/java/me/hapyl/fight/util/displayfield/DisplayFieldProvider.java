package me.hapyl.fight.util.displayfield;

import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.heroes.Hero;

import javax.annotation.Nonnull;

public interface DisplayFieldProvider {

    default void copyDisplayFieldsToUltimate() {
        if (!(this instanceof Hero hero)) {
            Debug.warn("Cannot copy to ultimate for " + getClass().getSimpleName());
            return;
        }

        copyDisplayFieldsTo(hero.getUltimate());
    }

    default void copyDisplayFieldsTo(@Nonnull DisplayFieldDataProvider provider) {
        DisplayFieldSerializer.copy(this, provider);
    }

    default void copyDisplayFieldsFrom(@Nonnull DisplayFieldProvider from) {
        if (!(this instanceof DisplayFieldDataProvider provider)) {
            Debug.warn("Cannot copy fields because this is not a DisplayFieldDataProvider!");
            return;
        }

        DisplayFieldSerializer.copy(from, provider);
    }
}

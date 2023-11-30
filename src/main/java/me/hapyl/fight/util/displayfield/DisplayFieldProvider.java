package me.hapyl.fight.util.displayfield;

import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.heroes.Hero;

import javax.annotation.Nonnull;

public interface DisplayFieldProvider {

    default void copyDisplayFieldsToUltimate() {
        if (this instanceof Hero hero) {
            copyDisplayFieldsTo(hero.getUltimate());
            return;
        }

        Debug.warn("Cannot copy to ultimate for " + getClass().getSimpleName());
    }

    default void copyDisplayFieldsTo(@Nonnull DisplayFieldDataProvider provider) {
        DisplayFieldSerializer.copy(this, provider);
    }
}

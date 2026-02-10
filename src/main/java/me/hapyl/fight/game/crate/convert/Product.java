package me.hapyl.fight.game.crate.convert;

import me.hapyl.eterna.module.util.Described;
import me.hapyl.fight.database.PlayerDatabase;

import javax.annotation.Nonnull;

public interface Product<E> extends Described {

    void subtractProduct(@Nonnull PlayerDatabase database, @Nonnull E value);

    @Nonnull
    E getProduct(@Nonnull PlayerDatabase database);

    @Nonnull
    default String formatProduct(@Nonnull E amount) {
        return getName() + " &bx" + amount;
    }
}
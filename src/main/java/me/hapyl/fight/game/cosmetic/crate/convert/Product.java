package me.hapyl.fight.game.cosmetic.crate.convert;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.util.Described;

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
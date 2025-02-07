package me.hapyl.fight.game.heroes.ultimate;

import javax.annotation.Nonnull;

public interface UltimateInvoker<E> {

    void invoke(E k);

    @Nonnull
    static UltimateInvoker<Void> ofRunnable(Runnable runnable) {
        return k -> runnable.run();
    }
}

package me.hapyl.fight.game.talents;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class Reference<T> {

    private final T t;

    public Reference(@Nonnull T t) {
        this.t = t;
    }

    public T get() {
        return t;
    }

    public T get(@Nonnull Consumer<T> butBefore) {
        butBefore.accept(t);
        return t;
    }
}

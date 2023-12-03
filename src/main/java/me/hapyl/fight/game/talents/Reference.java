package me.hapyl.fight.game.talents;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * Allows passing an object as a reference.
 */
public class Reference<T> {

    private final T t;

    public Reference(@Nonnull T t) {
        this.t = t;
    }

    /**
     * Returns the object.
     *
     * @return the object.
     */
    public T get() {
        return t;
    }

    /**
     * Applies a consumer to the object and returns it.
     *
     * @param butBefore - the consumer to apply.
     * @return the object.
     */
    public T get(@Nonnull Consumer<T> butBefore) {
        butBefore.accept(t);
        return t;
    }
}

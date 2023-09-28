package me.hapyl.fight.util;

import javax.annotation.Nonnull;
import java.util.function.BiConsumer;

public class Condition<R, T> {

    private final R r;
    private final T t;
    private boolean status;

    public Condition(@Nonnull R r, @Nonnull T t) {
        this.r = r;
        this.t = t;
        this.status = true;
    }

    public Condition<R, T> setStatus(boolean status) {
        this.status = status;
        return this;
    }

    public Condition<R, T> ifTrue(BiConsumer<R, T> consumer) {
        if (status) {
            consumer.accept(r, t);
        }

        return this;
    }

    public Condition<R, T> ifFalse(BiConsumer<R, T> consumer) {
        if (!status) {
            consumer.accept(r, t);
        }

        return this;
    }

}

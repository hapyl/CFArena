package me.hapyl.fight.anticheat;

import javax.annotation.Nonnull;

public class Variable<T> {

    private final T softLimit;
    private final T hardLimit;

    Variable(T softLimit, T hardlimit) {
        this.softLimit = softLimit;
        this.hardLimit = hardlimit;
    }

    @Nonnull
    public T getSoftLimit() {
        return softLimit;
    }

    @Nonnull
    public T getHardLimit() {
        return hardLimit;
    }

    @Nonnull
    public static <T> Variable<T> of(@Nonnull T softLimit, @Nonnull T hardLimit) {
        return new Variable<>(softLimit, hardLimit);
    }
}
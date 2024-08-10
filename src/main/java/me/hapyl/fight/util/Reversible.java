package me.hapyl.fight.util;

import javax.annotation.Nonnull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface Reversible<T> {

    @Retention(RetentionPolicy.RUNTIME)
    @java.lang.annotation.Target(ElementType.METHOD)
    @interface Target {
    }

    @Nonnull
    T reversed();

}

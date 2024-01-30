package me.hapyl.fight.annotate;

import javax.annotation.Nonnull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates that this method is <b>automatically</b> called within the code, but can be manually called if needed.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AutomaticallyHandled {

    /**
     * An optional parameter for programmers to annotate which class handles it.
     *
     * @return the class that handles this method.
     */
    @Nonnull
    Class<?> in() default void.class;
}

package me.hapyl.fight.annotate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated method should or should not call itself within the class.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SelfCallable {

    /**
     * Whenever the annotated method should call itself within the class.
     *
     * @return true if the annotated method can itself within the class; false otherwise.
     */
    boolean value() default true;

}

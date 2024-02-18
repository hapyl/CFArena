package me.hapyl.fight.annotate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface OverridingMethodsMustImplementEvents {

    /**
     * Which {@link me.hapyl.fight.game.Event} methods must be implemented.
     *
     * @return an array of methods that must be implemented.
     */
    String[] events() default "";

}

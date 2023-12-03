package me.hapyl.fight.annotate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates that this method should be called before or/and after
 * another method; else it might not work properly.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface ExecuteOrder {

    /**
     * Call <b>before</b> given method.
     */
    String[] before() default "";

    /**
     * Call <b>after</b> gives method.
     */
    String[] after() default "";

}

package me.hapyl.fight.annotate;

import javax.annotation.meta.When;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the value in the method parameter should be unique to other values.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Unique {

    When when() default When.ALWAYS;

}

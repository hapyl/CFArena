package me.hapyl.fight.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the parameter, or it's length should be between min and max. (inclusive)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.PARAMETER })
public @interface Range {

    /**
     * Minimum length of the parameter.
     *
     * @return minimum length.
     */
    int min() default 0;

    /**
     * Maximum length of the parameter.
     *
     * @return maximum length.
     */
    int max() default Integer.MAX_VALUE;

}

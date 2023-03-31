package me.hapyl.fight.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the parameter should length should be between min and max.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
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
    int max() default Byte.MAX_VALUE;

}

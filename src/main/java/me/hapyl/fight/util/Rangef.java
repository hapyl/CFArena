package me.hapyl.fight.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Indicates that the parameter, or it's length should be between min and max.
 */
@Target({ ElementType.METHOD, ElementType.PARAMETER })
public @interface Rangef {

    /**
     * Minimum length of the parameter.
     *
     * @return minimum length.
     */
    float min() default 0.0f;

    /**
     * Maximum length of the parameter.
     *
     * @return maximum length.
     */
    float max() default Float.MAX_VALUE;

    /**
     * Whenever the parameter or it's length is insured is the given argument if not within the range.
     *
     * @return true if insured; false otherwise.
     */
    boolean insured() default false;

}

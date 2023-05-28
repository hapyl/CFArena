package me.hapyl.fight.util.displayfield;

import javax.annotation.Nonnull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates that this field will be used in the item preview.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface DisplayField {

    /**
     * Name of the field. If not, present, field name will be formatted and used.
     */
    @Nonnull
    String name() default "";

    /**
     * Suffix of the field. If not present, no suffix will be used.
     */
    @Nonnull
    String suffix() default "";

    /**
     * Multiplier by which the numeral value will be scaled.
     */
    double scaleFactor() default 1.0d;

    /**
     * Extra line to add as a description. If not present, no extra line will be used.
     */
    @Nonnull
    String extra() default "";

}

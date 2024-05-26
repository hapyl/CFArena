package me.hapyl.fight.util.displayfield;

import me.hapyl.fight.game.attribute.AttributeType;

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
     * Whenever a space should prepend suffix.
     */
    boolean suffixSpace() default true;

    /**
     * Multiplier by which the numeral value will be scaled.
     */
    double scaleFactor() default 1.0d;

    /**
     * Annotates that this field is a percentage and will be scaled by 100 and appended with "%" without a space.
     *
     * @return is percentage.
     */
    boolean percentage() default false;

    /**
     * Annotates custom decimal point.
     * <br>
     * -1 to dynamically use 1 dp for <code>float</code> and 2 for <code>double</code>.
     *
     * @return the decimal point.
     */
    int dp() default -1;

    @Nonnull
    AttributeType attribute() default AttributeType.MAX_HEALTH;

}

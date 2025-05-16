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
     * Denotes the name display name of the field, defaults to field name if omitted.
     */
    @Nonnull
    String name() default "";
    
    /**
     * Denotes the suffix of the display to be appended at the end of the display.
     */
    @Nonnull
    String suffix() default "";
    
    /**
     * The multiplier a numeric field value will be multiplied in the display.
     * <p>
     *     Note that {@link Integer} are always parsed as 'ticks' and are not scaled!
     *     <br>
     *     If this behavior is unwanted, use {@link Short} or other numeric data types.
     * </p>
     */
    double scale() default 1;
    
    /**
     * Denotes that this field is a percentage and should be:
     * <ul>
     *     <li>Multiplied by 100
     *     <li>Suffixed with a '%'
     * </ul>
     * So a {@code 0.35} double would look like {@code 35%}
     */
    boolean percentage() default false;
    
}

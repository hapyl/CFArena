package me.hapyl.fight.annotate;

import javax.annotation.Nonnull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that this field is a constant.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface ConstantField {

    /**
     * A short description of this constant.
     *
     * @return a short description of this constant.
     */
    @Nonnull
    String description();

    /**
     * Gets an array of classes this constant is used/applicable to.
     *
     * @return an array of classes this constant is used/applicable to.
     */
    @Nonnull
    Class<?>[] applicableTo() default {};

}

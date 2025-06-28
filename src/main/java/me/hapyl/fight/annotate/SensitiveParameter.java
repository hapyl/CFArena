package me.hapyl.fight.annotate;

import javax.annotation.Nonnull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates that this parameter is sensitive and will throw:
 * <ul>
 *     <li>{@link IllegalArgumentException} if there is something wrong with the parameter.
 *     <li>{@link IllegalStateException} if the call is at the wrong time.
 *     <li>...or others.
 * </ul>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface SensitiveParameter {
    
    @Nonnull
    String throwsIllegalArgumentException() default "";
    
    @Nonnull
    String throwsIllegalStateException() default "";
    
    @Nonnull
    String throwsNullPointerException() default "";
    
    @Nonnull
    String throwsOther() default "";
    
}

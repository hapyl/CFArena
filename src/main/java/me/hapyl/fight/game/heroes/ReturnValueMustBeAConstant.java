package me.hapyl.fight.game.heroes;

import javax.annotation.Nonnull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the return of this method <b>must</b> be a constant.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface ReturnValueMustBeAConstant {

    /**
     * The class where the constant is located.
     *
     * @return the class where the constant is located.
     */
    @Nonnull
    Class<?> of();

}

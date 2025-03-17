package me.hapyl.fight.annotate;

import javax.annotation.Nonnull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A special case for overriding methods indicating that if the annotated method is overridden but {@code super()} wasn't called, the methods provided in {@link #value()} <b>must</b> be called within in.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface IfOverridingMethodsDoNotInvokeSuperTheyMustInvoke {

    @Nonnull
    String[] value();

}

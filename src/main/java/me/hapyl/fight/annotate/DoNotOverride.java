package me.hapyl.fight.annotate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates that this method should <b>not</b> be overridden.
 * <br>
 * Mostly used in <code>interfaces</code> that override the base interface.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DoNotOverride {
}

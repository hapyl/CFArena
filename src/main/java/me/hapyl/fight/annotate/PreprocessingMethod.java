package me.hapyl.fight.annotate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates that this method is used to "preprocess" and validate conditions, before calling the actual method.
 * Usually suffixed with a '0', as example:
 * <pre>
 *     // Actual method that class implements
 *     protected void sayHello() {
 *     }
 *
 *     //@PreprocessingMethod()
 *     public void sayHello0() {
 *         if (!canSayHello) { return; }
 *         if (alreadySaidHello) { return; }
 *
 *         sayHello();
 *     }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PreprocessingMethod {

}

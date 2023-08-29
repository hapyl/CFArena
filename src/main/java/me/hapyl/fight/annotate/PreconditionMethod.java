package me.hapyl.fight.annotate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Indicates that this method is a precondition to a similar named method
 * that processes preconditions before executing the method.
 * <p>
 * Usually contains 0 at the of the name:
 * <pre>
 *     // The method
 *     void sayHello();
 *
 *     // Precondition method
 *     void sayHello0() {
 *         if (entity != player) {
 *             return;
 *         }
 *
 *         sayHello();
 *     }
 * </pre>
 */
@Target(ElementType.METHOD)
public @interface PreconditionMethod {
}

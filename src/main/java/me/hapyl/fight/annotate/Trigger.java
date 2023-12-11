package me.hapyl.fight.annotate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Annotates that this method should be used as a trigger to update or trigger other values.
 */
@Target(ElementType.METHOD)
public @interface Trigger {
}

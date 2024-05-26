package me.hapyl.fight.annotate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Annotates that the parameter is forcefully cloned.
 */
@Target({ ElementType.PARAMETER })
public @interface ForceCloned {
}

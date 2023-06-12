package me.hapyl.fight.annotate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Annotates that this method explicitly perform an entity validation even is none provided.
 */
@Target(ElementType.METHOD)
public @interface ExplicitEntityValidation {
}

package me.hapyl.fight.annotate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Annotates that this talent must not be moved in a method.
 */
@Target(ElementType.METHOD)
public @interface StrictTalentPlacement {
}

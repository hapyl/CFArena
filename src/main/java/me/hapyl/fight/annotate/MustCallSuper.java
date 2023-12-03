package me.hapyl.fight.annotate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Indicates that overridden method <b>must</b> call super method.
 */
@Target(ElementType.METHOD)
public @interface MustCallSuper {
}

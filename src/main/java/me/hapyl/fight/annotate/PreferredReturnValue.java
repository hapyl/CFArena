package me.hapyl.fight.annotate;

import javax.annotation.Nonnull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Annotates that this method prefers a given value to be returned rather than <code>null</code>.
 * Though it is not necessary.
 */
@Target(ElementType.METHOD)
public @interface PreferredReturnValue {

    @Nonnull
    String value();

}

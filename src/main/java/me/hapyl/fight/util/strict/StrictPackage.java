package me.hapyl.fight.util.strict;

import javax.annotation.Nonnull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Types annotates with this interface must be in the given package.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface StrictPackage {

    /**
     * The name of the package.
     *
     * @return name of the package.
     */
    @Nonnull
    String value();

}

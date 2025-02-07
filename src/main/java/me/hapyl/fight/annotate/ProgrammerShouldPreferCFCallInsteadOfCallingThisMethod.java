package me.hapyl.fight.annotate;

import me.hapyl.fight.CF;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates that this method should preferably be called from {@link CF}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface ProgrammerShouldPreferCFCallInsteadOfCallingThisMethod {

}

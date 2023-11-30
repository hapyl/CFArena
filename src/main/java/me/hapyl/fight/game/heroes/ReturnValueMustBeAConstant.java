package me.hapyl.fight.game.heroes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates that return of this method <b>MUST</b> be a constant.
 * For {@link me.hapyl.fight.game.talents.Talent}s, a <code>enum</code> value should be returned, like:
 * <pre>
 *     return {@link me.hapyl.fight.game.talents.Talents#TRIPLE_SHOT}.getTalent();
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ReturnValueMustBeAConstant {
}

package me.hapyl.fight.game.heroes.dvar;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DVar {

    String value();

    /**
     * root:
     * {
     *     DvAR_APPLICABLE: {
     *         dVar_path: value
     *     }
     * }
     */

}

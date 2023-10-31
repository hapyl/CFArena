package me.hapyl.fight.annotate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ ElementType.PARAMETER })
public @interface ForceCloned {
}

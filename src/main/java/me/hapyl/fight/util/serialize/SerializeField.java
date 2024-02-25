package me.hapyl.fight.util.serialize;

import javax.annotation.Nonnull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface SerializeField {

    @Nonnull
    String name() default "\0";

    boolean setNullValues() default true;

}

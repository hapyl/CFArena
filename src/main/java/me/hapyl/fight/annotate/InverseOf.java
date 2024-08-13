package me.hapyl.fight.annotate;

import javax.annotation.Nonnull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates that this method is inverse of the {@link #value()}:
 *
 * <pre>{@code
 *  boolean isThisTrue() {
 *      return true;
 *  }
 *
 *  @InverseOf("isThisTrue")
 *  boolean isNotThisTrue() {
 *      return !isThisTrue();
 *  }
 * }</pre>
 * <br>
 * Usually exists for lambdas:
 * <pre>{@code
 *  filter(Object::isNotThisTrue);
 *
 *  // instead of
 *  filter(object -> !object.isThisTrue());
 * }</pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface InverseOf {

    @Nonnull
    String value();

}

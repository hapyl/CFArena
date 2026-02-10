package me.hapyl.fight.database;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that this parameter is a path and supports dot (.) separators.
 * <pre>{@code
 *  setValue("hello", 1);
 *
 *  {
 *      "hello": 1
 *  }
 * }</pre>
 * <pre>{@code
 *  setValue("hello.world", 2);
 *
 *  {
 *      "hello":
 *      {
 *          "world": 2
 *      }
 *  }
 * }</pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface Path {

}

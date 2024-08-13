package me.hapyl.fight.database.serialize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An <b>empty</b> (preferably <code>private</code> or <code>package-private</code>) constructor must
 * be annotated in {@link MongoSerializable} to deserialize an object.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.CONSTRUCTOR })
public @interface MongoSerializableConstructor {
}

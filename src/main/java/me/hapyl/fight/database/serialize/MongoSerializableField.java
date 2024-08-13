package me.hapyl.fight.database.serialize;


import javax.annotation.Nonnull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Fields annotated with this annotation in a {@link MongoSerializable} can be serialized.
 * <br>
 * Note that this only works for the following types:
 * <ul>
 *     <li>{@link Boolean}
 *     <li>{@link Byte}
 *     <li>{@link Short}
 *     <li>{@link Integer}
 *     <li>{@link Long}
 *     <li>{@link Float}
 *     <li>{@link Double}
 *     <li>{@link Character}
 *     <li>{@link Boolean}
 *     <li>{@link String}
 * </ul>
 * Any other types <b>must</b> have a wrapper, registered in {@link MongoWrappers}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface MongoSerializableField {

    /**
     * Name by which to serialize.
     * <br>
     * Leave empty for field name.
     *
     * @return name by which to serialize or use field name is empty.
     * @implNote Note that field names are very sensitive, and changing them is highly
     * unadvisable since the names are the key in the database.
     */
    @Nonnull
    String name() default "";

    /**
     * An optional description about the field.
     * <br>
     * <i>This is not saved in the database.</i>
     *
     * @return optional description about the field.
     */
    @Nonnull
    String description() default "";

}

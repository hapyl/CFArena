package me.hapyl.fight.database.serialize;

/**
 * A serializable object into mongo.
 * <br>
 * <br>
 * Implementing object must:
 * <ul>
 *     <li>Have at least one field annotated with {@link MongoSerializableField}.
 *     <li>Have empty private/package-private constructor annotated with {@link MongoSerializableConstructor}
 * </ul>
 *
 * @see MongoSerializableField
 * @see MongoSerializableConstructor
 */
public interface MongoSerializable {
}

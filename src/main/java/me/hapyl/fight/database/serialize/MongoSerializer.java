package me.hapyl.fight.database.serialize;

import me.hapyl.fight.util.serialize.SerializeConstructor;
import org.apache.commons.lang3.SerializationException;
import org.bson.Document;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * A utility class to serialize/deserialize {@link MongoSerializable} objects.
 *
 * @see MongoSerializable
 * @see MongoSerializableField
 * @see MongoSerializableConstructor
 */
public final class MongoSerializer {

    /**
     * Serializes {@link MongoSerializable} into a {@link Document}.
     *
     * @param serializable - Serializable.
     * @return a document.
     */
    @Nonnull
    public static <T extends MongoSerializable> Document serialize(@Nonnull T serializable) {
        final Document document = new Document();

        try {
            final Class<? extends MongoSerializable> clazz = serializable.getClass();
            final Field[] declaredFields = clazz.getDeclaredFields();

            int fieldCount = 0;

            for (Field field : declaredFields) {
                final MongoSerializableField annotation = field.getAnnotation(MongoSerializableField.class);

                if (annotation == null) {
                    continue;
                }

                ++fieldCount;
                field.setAccessible(true);

                final String name = annotation.name().isEmpty() ? field.getName() : annotation.name();
                final Object value = field.get(serializable);

                document.put(name, wrapObject(value));
            }

            if (fieldCount == 0) {
                throw new IllegalArgumentException(
                        serializable + " does not contain any fields annotated with " + MongoSerializableField.class.getSimpleName()
                );
            }
        } catch (Exception e) {
            throw new SerializationException(e);
        }

        return document;
    }

    /**
     * Deserializes the given {@link Document} into a {@link MongoSerializable}.
     *
     * @param document    - Document.
     * @param objectClass - Class of the deserializable object.
     * @return a deserialized object.
     */
    @Nonnull
    @SuppressWarnings("unchecked")
    public static <T extends MongoSerializable> T deserialize(@Nonnull Document document, @Nonnull Class<T> objectClass) {
        try {
            final Constructor<?> constructor = objectClass.getDeclaredConstructor((Class<?>[]) null);
            final MongoSerializableConstructor constructorAnnotation = constructor.getAnnotation(MongoSerializableConstructor.class);

            if (constructorAnnotation == null) {
                throw new SerializationException(
                        "Serializable constructor is not annotated with " + SerializeConstructor.class.getSimpleName());
            }

            constructor.setAccessible(true);
            final Object object = constructor.newInstance();

            for (Field field : objectClass.getDeclaredFields()) {
                final MongoSerializableField annotation = field.getAnnotation(MongoSerializableField.class);

                if (annotation == null) {
                    continue;
                }

                final String fieldName = annotation.name().isEmpty() ? field.getName() : annotation.name();
                final Object value = document.get(fieldName);

                // null value in document usually means that it's a new class
                // property, since we can't write null elements; continuing is better
                if (value == null) {
                    continue;
                }

                field.setAccessible(true);
                field.set(object, unwrapObject(value, field.getType()));
            }

            return (T) object;
        } catch (NoSuchMethodException exception) {
            throw new SerializationException("Serialize constructor does not exist for " + objectClass.getSimpleName() + "!");
        } catch (IllegalAccessException exception) {
            throw new SerializationException("Illegal access: " + exception.getMessage());
        } catch (InvocationTargetException | InstantiationException exception) {
            throw new SerializationException("Unable to instantiate serialize constructor: " + exception.getMessage());
        }
    }

    @Nonnull
    public static Object wrapObject(@Nonnull Object object) {
        final Object primitive = primitiveOrNull(object);

        if (primitive != null) {
            return primitive;
        }

        // Try primitive
        final MongoWrapper<Object> wrapper = MongoWrappers.get(object.getClass());

        if (wrapper == null) {
            throw new SerializationException("Unsupported type: " + object.getClass().getSimpleName());
        }

        return wrapper.wrapToString(object);
    }

    @Nonnull
    public static Object unwrapObject(@Nonnull Object object, @Nonnull Class<?> objectClass) {
        if (object instanceof String string) {
            // Try unwrapping before returning a string
            final MongoWrapper<Object> wrapper = MongoWrappers.get(objectClass);

            if (wrapper == null) {
                return string;
            }

            return wrapper.unwrapFromString(string);
        }

        final Object primitive = primitiveOrNull(object);

        if (primitive == null) {
            throw new SerializationException("Unsupported type: " + objectClass.getSimpleName());
        }

        return primitive;
    }

    @Nullable
    public static Object primitiveOrNull(@Nullable Object object) {
        return switch (object) {
            case String ignored -> object;
            case Boolean ignored -> object;
            case Byte ignored -> object;
            case Short ignored -> object;
            case Integer ignored -> object;
            case Long ignored -> object;
            case Float ignored -> object;
            case Double ignored -> object;
            case Character ignored -> object;
            case null, default -> null;
        };
    }

}

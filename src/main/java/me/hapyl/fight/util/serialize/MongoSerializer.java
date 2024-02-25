package me.hapyl.fight.util.serialize;

import me.hapyl.fight.database.MongoUtils;
import org.bson.Document;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class MongoSerializer {

    public static void serialize(@Nonnull Document document, @Nonnull Serializable serializable) {

    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T deserialize(@Nonnull Document document, @Nonnull Class<T> clazz) {
        try {
            for (Constructor<?> constructor : clazz.getConstructors()) {
                final SerializeConstructor constAnnotation = constructor.getAnnotation(SerializeConstructor.class);

                if (constAnnotation == null) {
                    continue;
                }

                final int parameterCount = constructor.getParameterCount();

                if (parameterCount != 0) {
                    throw new DeserializeException(clazz, "Serializable constructor must not have parameters!");
                }

                final int modifiers = constructor.getModifiers();

                if (!Modifier.isPrivate(modifiers)) {
                    throw new DeserializeException(clazz, "Serializable constructor must be private!");
                }

                final Object newObject = constructor.newInstance();

                // Write fields
                for (final Field field : clazz.getDeclaredFields()) {
                    final SerializeField annotation = field.getAnnotation(SerializeField.class);

                    if (annotation == null) {
                        continue;
                    }

                    field.setAccessible(true);

                    final String name = annotation.name();
                    final String fieldName = name.equals("\0") ? field.getName() : name;

                    final Object value = MongoUtils.get(document, fieldName, null);

                    if (value == null && !annotation.setNullValues()) {
                        continue;
                    }

                    field.set(newObject, value);
                }

                return (T) newObject;
            }

            throw new DeserializeException(clazz, "Missing @SerializeConstructor!");
        } catch (Exception e) {
            e.printStackTrace();
        }

        throw new DeserializeException(clazz, "Internal error!");
    }

}

package me.hapyl.fight.database.serialize;

import me.hapyl.fight.database.Database;
import org.apache.commons.lang3.SerializationException;
import org.bson.Document;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

/**
 * Represents an {@link Object} that can be serialized and saved into a {@link Database}.
 * <p>A serializable object must contain a {@code private} constructor, annotated with {@link MongoSerializableConstructor}, as well as imeplement the following methods:</p>
 * <ul>
 *     <li>{@link #serialize()}
 *     <li>{@link #deserialize(Document)}
 * </ul>
 * <p>Deserialization must be done statically, using {@link #deserialize(Class, Document)}.</p>
 */
public interface MongoSerializable {
    
    /**
     * Serializes this object into a {@link Document}.
     *
     * @return a new document with serialized fields of this object.
     */
    @Nonnull
    Document serialize();
    
    /**
     * Deserializes the given {@link Document} into this object.
     * <p>This method is called after a new instance of this object is created using the private constructor.</p>
     *
     * @param document - The document to deserialize.
     * @throws MongoDeserializationException if something went wrong during deserialization.
     */
    void deserialize(@Nonnull Document document) throws MongoDeserializationException;
    
    @Nonnull
    static <S extends MongoSerializable> S deserialize(@Nonnull Class<S> clazz, @Nonnull Document document) {
        final String simpleClassName = clazz.getSimpleName();
        
        try {
            final Constructor<S> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            
            if (!Modifier.isPrivate(constructor.getModifiers())) {
                throw new SerializationException("Serialization constructor must be private!");
            }
            
            if (!constructor.isAnnotationPresent(MongoSerializableConstructor.class)) {
                throw new SerializationException("Serialization constructor must be annotated with %s!".formatted(MongoSerializableConstructor.class.getSimpleName()));
            }
            
            final S newInstance = constructor.newInstance();
            newInstance.deserialize(document);
            
            return newInstance;
        }
        catch (NoSuchMethodException e) {
            throw new SerializationException("Missing private %s constructor in %s!".formatted(MongoSerializableConstructor.class.getSimpleName(), simpleClassName));
        }
        catch (MongoDeserializationException deserializationException) {
            throw new SerializationException("Exception duration deserialization: %s".formatted(deserializationException.getMessage()), deserializationException);
        }
        catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new AssertionError("Unable to serialize %s!".formatted(simpleClassName), e);
        }
    }
    
}

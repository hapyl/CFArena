package me.hapyl.fight.util.serialize;

public class DeserializeException extends RuntimeException {

    public <T extends Serializable> DeserializeException(Class<T> clazz, String message) {
        super("Cannot deserialize %s! ".formatted(clazz.getSimpleName()) + message);
    }
}

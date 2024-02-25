package me.hapyl.fight.util.serialize;

public class SerializeException extends RuntimeException {

    public <T extends Serializable> SerializeException(Class<Serializable> clazz, String message) {
        super("Cannot serialize %s! ".formatted(clazz.getSimpleName()) + message);
    }
}

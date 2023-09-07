package me.hapyl.fight.game.profile.data;

public class Type<T> {

    public static final Type<Long> LAST_USE = new Type<>(Long.class);
    public static final Type<Integer> USE_TIME = new Type<>(Integer.class);

    private final Class<T> clazz;

    private Type(Class<T> clazz) {
        this.clazz = clazz;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public T get(Object object) {
        if (clazz.isInstance(object)) {
            return clazz.cast(object);
        }

        throw new IllegalArgumentException("object must be %s, not %s!".formatted(clazz.getSimpleName(), object.getClass().getSimpleName()));
    }

}

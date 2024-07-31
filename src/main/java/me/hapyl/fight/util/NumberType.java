package me.hapyl.fight.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum NumberType {

    BYTE(byte.class, Byte.class),
    SHORT(short.class, Short.class),
    INTEGER(int.class, Integer.class),
    LONG(long.class, Long.class),
    FLOAT(float.class, Float.class),
    DOUBLE(double.class, Double.class);

    private final Class<?>[] classes;

    NumberType(Class<?> primitive, Class<?> object) {
        this.classes = new Class[] { primitive, object };
    }

    @Nonnull
    public Class<?> getPrimitiveClass() {
        return classes[0];
    }

    @Nonnull
    public Class<?> getObjectClass() {
        return classes[1];
    }

    public boolean matches(Object object) {
        if (object == null) {
            return false;
        }

        final Class<?> objectClass = object.getClass();

        return objectClass == getPrimitiveClass() || objectClass == getObjectClass();
    }

    @Nullable
    public static NumberType getType(@Nonnull Object object) {
        for (NumberType type : values()) {
            if (type.matches(object)) {
                return type;
            }
        }

        return null;
    }

}

package me.hapyl.fight.enumclass;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * Indicates that this class should be treated as an enum.
 * <p>
 * The class <b>must</b> implement the following:
 * <ul>
 *     <li>
 *         A static method <code>List<<E>E> values()</code> that will return all values in chronological order.
 *     </li>
 *     <li>
 *         A static method <code>E byName(String)</code> that will attempt to find and return a value by its name.
 *     </li>
 *     <li>
 *         A static method <code>int register(E)</code> that must register an item and return the ordinal of that item.
 *     </li>
 * </ul>
 */
public class EnumClass<E extends EnumItem> {

    /**
     * Though this is not mandatory, instantiation should be called on onEnable() to validate the enum.
     */
    @SuppressWarnings("")
    public static <E extends EnumClass<?>> E instantiate(E enumClass) {
        final var clazz = enumClass.getClass();

        checkMethod(clazz, "values", List.class);
        checkMethod(clazz, "byName", Object.class, String.class);
        checkMethod(clazz, "register", Integer.class, Object.class);

        return enumClass;
    }

    private static void checkMethod(Class<?> clazz, String name, Class<?> returnType, Class<?>... params) {
        try {
            final Method method = clazz.getDeclaredMethod(name);
            final Class<?> methodReturnType = method.getReturnType();

            if (methodReturnType != returnType) {
                throw new IllegalArgumentException("Invalid return type for '%s'! Must be '%s', not '%s'.".formatted(
                        name,
                        returnType.getSimpleName(),
                        methodReturnType.getSimpleName()
                ));
            }

            final int modifiers = method.getModifiers();

            if (!Modifier.isStatic(modifiers)) {
                throw new IllegalArgumentException("Method '%s' must be static!".formatted(name));
            }

            if (params != null) {
                final Class<?>[] types = method.getParameterTypes();

                if (params.length != types.length) {
                    throw new IllegalArgumentException("Parameter length mismatch for '%s'! Must be %s, not %s.".formatted(
                            name,
                            params.length,
                            types.length
                    ));
                }

                for (int i = 0; i < params.length; i++) {
                    final Class<?> type = types[i];
                    final Class<?> param = params[i];

                    if (type != param) {
                        throw new IllegalArgumentException("Parameter mismatch for '%s'! Must be '%s', not '%s.".formatted(
                                name,
                                param.getSimpleName(),
                                type.getSimpleName()
                        ));
                    }
                }
            }

        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Missing declared method '%s'!".formatted(name));
        }
    }

}

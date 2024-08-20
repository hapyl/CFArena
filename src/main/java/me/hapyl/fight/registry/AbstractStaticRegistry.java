package me.hapyl.fight.registry;

import me.hapyl.fight.database.key.DatabaseKeyed;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Indicates that this class is a static registry.
 * <br><br>
 * We're not using {@link Registry} for convenience, to be able to call
 * <pre>{@code
 *  MyRegistry.ITEM
 * }</pre>
 * rather than
 * <pre>{@code
 *  Registries.MY_REGISTRY.ITEM
 * }</pre>
 * <br><br>
 * Since it's a static registry, we cannot ensure by compiler the existence of methods, but the registry should include:
 * <ul>
 *     <li><pre>{@code
 *      // Gets the T by the given string, might throw an error or return a default value, up to implementation.
 *      @Nonnull
 *      T ofString(String)
 *     }</pre>
 *     <li><pre>{@code
 *      // Gets the T by the given string, or null if doesn't exist.
 *      @Nullable
 *      T ofStringOrNull(String)
 *     }</pre>
 *     <li><pre>{@code
 *      // Gets a copy of all the registered values.
 *      @Nonnull
 *      List<T> values()
 *     }</pre>
 *     <li><pre>{@code
 *      // Gets a list of all values keys.
 *      @Nonnull
 *      List<String> keys()
 *     }</pre>
 * </ul>
 * Registry should call {@link #ensure(Class, Class)} to validate the basic method requirements.
 *
 * @param <T> - A registrable item, must extend {@link DatabaseKeyed}.
 */
@SuppressWarnings("unused" /* keep generic to unsure type */)
public abstract class AbstractStaticRegistry<T extends DatabaseKeyed> {

    @Nonnull
    protected static <T extends DatabaseKeyed> List<String> keys(@Nonnull Set<T> values) {
        final List<String> keys = new ArrayList<>();

        for (T value : values) {
            keys.add(value.getDatabaseKey().key());
        }

        return keys;
    }

    @Nonnull
    protected static <T extends DatabaseKeyed> List<T> values(@Nonnull Set<T> values) {
        return new ArrayList<>(values);
    }

    protected static <D extends DatabaseKeyed, T extends AbstractStaticRegistry<?>> void ensure(@Nonnull Class<T> clazz, @Nonnull Class<D> registryClass) {
        validateMethod(clazz, "ofString", registryClass, String.class);
        validateMethod(clazz, "ofStringOrNull", registryClass, String.class);
        validateMethod(clazz, "values", List.class);
        validateMethod(clazz, "keys", List.class);
    }

    @Nullable
    protected static <T extends DatabaseKeyed> T ofStringOrNull(@Nonnull Set<T> values, @Nonnull String string) {
        for (T value : values) {
            if (value.getDatabaseKey().isKeyMatchesAnyCase(string)) {
                return value;
            }
        }

        return null;
    }

    @Nonnull
    protected static <T extends DatabaseKeyed> T ofString(@Nonnull Set<T> values, @Nonnull String string, @Nullable T defaultValue) {
        final T value = ofStringOrNull(values, string);

        if (value != null) {
            return value;
        }
        else if (defaultValue != null) {
            return defaultValue;
        }

        throw new NoSuchElementException("Element does not exist: " + string);
    }

    private static void validateMethod(Class<?> clazz, String methodName, Class<?> expectedReturnType, @Nullable Class<?>... expectedParameters) {
        final Object toString = new Object() {
            @Override
            public String toString() {
                return methodName + " of " + clazz.getSimpleName();
            }
        };

        try {
            final Method method = clazz.getDeclaredMethod(methodName, expectedParameters);

            // Validate return type
            final Class<?> actualReturnType = method.getReturnType();

            if (actualReturnType != expectedReturnType) {
                throw assertionError("Method %s must return %s, not %s!".formatted(
                        toString,
                        expectedReturnType.getSimpleName(),
                        actualReturnType.getSimpleName()
                ));
            }

        } catch (NoSuchMethodException e) {
            throw assertionError("Missing method: %s".formatted(toString));
        }
    }

    private static AssertionError assertionError(String string) {
        return new AssertionError(string);
    }

}

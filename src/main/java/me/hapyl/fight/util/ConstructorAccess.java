package me.hapyl.fight.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class ConstructorAccess<T> {

    private final Class<T> clazz;
    private final List<Class<?>[]> searchList;

    public ConstructorAccess(Class<T> clazz) {
        this.clazz = clazz;
        this.searchList = new ArrayList<>();
    }

    public ConstructorAccess<T> tryGet(@Nullable Class<?>... params) {
        searchList.add(params);
        return this;
    }

    public ConstructorAccessResult<T> tryInvoke(@Nullable Object... params) {
        if (searchList.isEmpty()) {
            return tryInvoke0((Constructor<T>) clazz.getConstructors()[0], null);
        }
        else {
            for (Class<?>[] classes : searchList) {
                try {
                    final Constructor<T> constructor = clazz.getConstructor(classes);

                    return tryInvoke0(constructor, params);
                } catch (Exception ignored) {
                    // don't care
                }
            }
        }

        return ConstructorAccessResult.empty("No constructors were found!");
    }

    private ConstructorAccessResult<T> tryInvoke0(Constructor<T> c, Object[] params) {
        c.setAccessible(true);

        if (params != null && c.getParameterCount() != params.length) {
            return ConstructorAccessResult.empty("Parameter count does not match, expected %s, got %s!".formatted(
                    c.getParameterCount(),
                    params.length
            ));
        }

        try {
            return new ConstructorAccessResult<>(c.newInstance(params));
        } catch (Exception e) {
            return ConstructorAccessResult.empty(e.getMessage());
        }
    }

    public static <T> ConstructorAccess<T> of(@Nonnull Class<T> clazz) {
        return new ConstructorAccess<>(clazz);
    }

    public static class ConstructorAccessResult<T> {

        private final T result;
        private final String reason;

        private ConstructorAccessResult(T result) {
            this(result, "ok");
        }

        private ConstructorAccessResult(T result, String reason) {
            this.result = result;
            this.reason = reason;
        }

        @Nonnull
        public String getReason() {
            return reason;
        }

        @Nullable
        public T getResult() {
            return this.result;
        }

        @Nonnull
        public T getResultOrDefault(@Nonnull T def) {
            return this.result != null ? this.result : def;
        }

        @Nonnull
        public T getResultOrThrow(@Nonnull RuntimeException throwable) {
            if (this.result != null) {
                return this.result;
            }

            throw throwable;
        }

        public static <T> ConstructorAccessResult<T> empty(@Nonnull String reason) {
            return new ConstructorAccessResult<>(null, reason);
        }
    }

}

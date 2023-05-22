package me.hapyl.fight;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * This class stores global variables that can be changed in runtime.
 * It is mainly for debug and testing purposes and probably should not
 * be in PROD, but is definitely fine to use. By fine, I mean not fine,
 * it's static, you know.
 */
public final class GVar {

    private static final Map<String, Object> variables = Maps.newConcurrentMap();

    private GVar() {
        throw new YeahIDon_tKnowWhyAreYouDoingThisItsClearlyPrivateForAReasonYa();
    }

    public static <T> void set(@Nonnull String name, @Nonnull T object) {
        variables.put(name.toLowerCase(), object);
    }

    public static boolean remove(@Nonnull String name) {
        return variables.remove(name.toLowerCase()) != null;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public static <T> T get(@Nonnull String name, @Nonnull T def) {
        final Object value = variables.computeIfAbsent(name.toLowerCase(), n -> def);

        try {
            return (T) value;
        } catch (Exception e) {
            return def;
        }
    }

    @Nullable
    public static Object getRaw(@Nonnull String name) {
        return variables.get(name.toLowerCase());
    }

    public static List<String> listNames() {
        return Lists.newArrayList(variables.keySet());
    }

    private static class YeahIDon_tKnowWhyAreYouDoingThisItsClearlyPrivateForAReasonYa extends RuntimeException {
    }
}

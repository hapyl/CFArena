package me.hapyl.fight.script;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.script.scripts.WineryLiftScript;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Function;

/**
 * This is a {@link Script} registry.
 * Both statically typed {@link Script}s and those loaded from a file are stored here.
 */
public final class Scripts {

    public static final Script WINERY_LIFT;

    private static final Map<Key, Script> values;

    static {
        values = Maps.newHashMap();

        WINERY_LIFT = register("winery_lift", WineryLiftScript::new);
    }

    @Nullable
    public static Script byId(@Nonnull String string) {
        final Key key = Key.ofStringOrNull(string);

        return key != null ? values.get(key) : null;
    }

    @Nonnull
    public static Script register(@Nonnull Script script) {
        final Key key = script.getKey();

        if (values.containsKey(key)) {
            throw new IllegalArgumentException("Script with id '%s' is already registered!".formatted(key));
        }

        values.put(key, script);
        return script;
    }

    @Nonnull
    public static Script register(@Nonnull String id, @Nonnull Function<Key, Script> fn) {
        final Key enumKey = Key.ofString(id);
        final Script script = fn.apply(enumKey);

        return register(script);
    }

}

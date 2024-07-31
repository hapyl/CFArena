package me.hapyl.fight.script;

import com.google.common.collect.Maps;
import me.hapyl.fight.registry.EnumId;
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

    private static final Map<String, Script> values;

    static {
        values = Maps.newHashMap();

        WINERY_LIFT = register("winery_lift", WineryLiftScript::new);
    }

    @Nullable
    public static Script byId(@Nonnull String id) {
        return values.get(EnumId.formatString(id));
    }

    @Nonnull
    public static Script register(@Nonnull Script script) {
        final String id = script.getStringId();

        if (values.containsKey(id)) {
            throw new IllegalArgumentException("Script with id '%s' is already registered!".formatted(id));
        }

        values.put(id, script);
        return script;
    }

    @Nonnull
    public static Script register(@Nonnull String id, @Nonnull Function<String, Script> fn) {
        return register(fn.apply(EnumId.formatString(id)));
    }

}

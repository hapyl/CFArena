package me.hapyl.fight.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hapyl.fight.Main;
import me.hapyl.fight.database.async.EnvironmentAsynchronousDocument;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class Environment {

    public final EnvironmentProperty.BooleanEnvironmentProperty debug;
    public final EnvironmentProperty.BooleanEnvironmentProperty allowDisabledHeroes;
    public final EnvironmentProperty.BooleanEnvironmentProperty ignoreCooldowns;

    protected final EnvironmentAsynchronousDocument document;
    protected final Map<String, EnvironmentProperty<?>> byName;

    public Environment(@Nonnull Main main) {
        this.document = new EnvironmentAsynchronousDocument();
        this.byName = Maps.newHashMap();

        this.debug = register(EnvironmentProperty.ofBoolean("debug", false));
        this.allowDisabledHeroes = register(EnvironmentProperty.ofBoolean("allow_disabled_heroes", false));
        this.ignoreCooldowns = register(EnvironmentProperty.ofBoolean("ignore_cooldowns", false));
    }

    @Nullable
    public EnvironmentProperty<?> byName(@Nonnull String name) {
        return byName.get(name);
    }

    @Nonnull
    public List<String> names() {
        return Lists.newArrayList(byName.keySet());
    }

    private <K, T extends EnvironmentProperty<K>> T register(T t) {
        byName.put(t.name(), t);
        return t;
    }

}

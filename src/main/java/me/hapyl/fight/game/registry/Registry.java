package me.hapyl.fight.game.registry;

import com.google.common.collect.Maps;
import me.hapyl.fight.util.PatternId;

import javax.annotation.Nonnull;
import java.util.Map;

public class Registry<T extends PatternId> {

    private final Class<T> clazz;
    private final Map<String, T> registered;

    protected Registry(@Nonnull Class<T> clazz) {
        this.clazz = clazz;
        this.registered = Maps.newHashMap();
    }

    @Nonnull
    public final Class<T> getClazz() {
        return clazz;
    }

    public T byId(@Nonnull String id) {
        return registered.get(id);
    }

    public final T register(T t) {
        registered.put(t.getId(), t);
        return t;
    }

    public final boolean unregister(@Nonnull T t) {
        return registered.remove(t.getId()) != null;
    }

    public static <T extends PatternId, R extends Registry<T>> T register(R registry, T value) {
        return registry.register(value);
    }

}

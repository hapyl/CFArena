package me.hapyl.fight.game.maps.supply;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.CollectionUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Supplier;

public final class Supplies {
    
    public static final HealthSupply HEALTH;
    public static final EnergySupply ENERGY;
    public static final CritBoostSupply CRIT;
    
    private static final Map<Key, Supply> values;
    
    static {
        values = Maps.newHashMap();
        
        HEALTH = register("health", HealthSupply::new);
        ENERGY = register("energy", EnergySupply::new);
        CRIT = register("crit", CritBoostSupply::new);
    }
    
    private Supplies() {
    }
    
    @Nullable
    public static Supply ofKey(@Nonnull Key key) {
        return values.get(key);
    }
    
    @Nullable
    public static Supply ofKey(@Nonnull String stringKey) {
        final Key key = Key.ofStringOrNull(stringKey);
        
        return key != null ? ofKey(key) : null;
    }
    
    @Nonnull
    public static Supply ofRandom() {
        return CollectionUtils.randomElementOrFirst(values.values());
    }
    
    private static <T extends Supply> T register(String key, Supplier<T> t) {
        final T supply = t.get();
        
        values.put(Key.ofString(key), supply);
        return supply;
    }
    
}

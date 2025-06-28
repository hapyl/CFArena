package me.hapyl.fight.game.dot;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.registry.Key;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Function;

public final class DotType {
    
    public static final PoisonDot POISON;
    public static final WitherDot WITHER;
    public static final BleedDot BLEED;
    public static final CorrosionDot CORROSION;
    public static final EntanglementDot ENTANGLEMENT;
    
    private static final Map<Key, Dot> registry;
    
    static {
        registry = Maps.newHashMap();
        
        POISON = register("poison", PoisonDot::new);
        WITHER = register("wither", WitherDot::new);
        BLEED = register("bleed", BleedDot::new);
        CORROSION = register("corrosion", CorrosionDot::new);
        ENTANGLEMENT = register("entanglement", EntanglementDot::new);
    }
    
    @Nullable
    public static Dot byKey(@Nonnull Key key) {
        return registry.get(key);
    }
    
    private static <T extends Dot> T register(@Nonnull String stringKey, @Nonnull Function<Key, T> fn) {
        final Key key = Key.ofString(stringKey);
        final T t = fn.apply(key);
        
        registry.put(key, t);
        return t;
    }
    
}

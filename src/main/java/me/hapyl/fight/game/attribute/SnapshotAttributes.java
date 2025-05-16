package me.hapyl.fight.game.attribute;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.entity.LivingGameEntity;

import javax.annotation.Nonnull;
import java.util.Map;

public class SnapshotAttributes extends BaseAttributes {
    
    private final LivingGameEntity entity;
    private final Map<AttributeType, Double> multipliers;
    
    SnapshotAttributes(@Nonnull LivingGameEntity entity) {
        this.entity = entity;
        this.multipliers = Maps.newHashMap();
    }
    
    @Override
    public double get(@Nonnull AttributeType type) {
        return type.clamp(super.get(type) * multipliers.getOrDefault(type, 1.0));
    }
    
    public void multiply(@Nonnull AttributeType type, double multiplier) {
        multipliers.compute(type, (t, m) -> (m != null ? m : 1.0) + multiplier);
    }
    
    @Nonnull
    public LivingGameEntity entity() {
        return entity;
    }
    
}

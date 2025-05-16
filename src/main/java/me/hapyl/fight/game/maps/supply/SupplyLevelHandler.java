package me.hapyl.fight.game.maps.supply;

import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.fight.game.maps.EnumLevel;

import javax.annotation.Nonnull;
import java.util.List;

public class SupplyLevelHandler implements Ticking {
    
    private final EnumLevel level;
    private final List<SupplyPlatform> platforms;
    
    public SupplyLevelHandler(@Nonnull EnumLevel level) {
        this.level = level;
        this.platforms = level.createSupplyPlatforms();
    }
    
    @Nonnull
    public EnumLevel level() {
        return level;
    }
    
    @Override
    public void tick() {
        platforms.forEach(SupplyPlatform::tick);
    }
    
    @Nonnull
    public List<SupplyPlatform> platforms() {
        return platforms;
    }
}

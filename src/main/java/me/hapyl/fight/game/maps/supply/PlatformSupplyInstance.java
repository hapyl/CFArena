package me.hapyl.fight.game.maps.supply;

import org.bukkit.Location;

import javax.annotation.Nonnull;

public class PlatformSupplyInstance extends SupplyInstance {
    
    private final SupplyPlatform platform;
    
    PlatformSupplyInstance(@Nonnull SupplyPlatform platform, @Nonnull Supply supply, @Nonnull Location location) {
        super(supply, location);
        
        this.platform = platform;
    }
    
    @Override
    public void remove() {
        super.remove();
        
        platform.instance = null;
    }
}

package me.hapyl.fight.game.talents.inferno;

import me.hapyl.eterna.module.util.Removable;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.TalentRegistry;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import javax.annotation.Nonnull;

public abstract class InfernoFire implements Ticking, Removable {
    
    protected final Location location;
    protected final Type type;
    
    InfernoFire(@Nonnull Location location, @Nonnull Type type) {
        this.location = location;
        this.type = type;
        
        // Create fire
        location.getBlock().setBlockData(type.blockData, false);
        
        // Register
        TalentRegistry.FIRE_PIT.infernoFires.add(this);
    }
    
    public abstract void touch(@Nonnull LivingGameEntity entity);
    
    @Override
    public void remove() {
        extinguish();
        
        TalentRegistry.FIRE_PIT.infernoFires.remove(this);
    }
    
    public void extinguish() {
        location.getBlock().setType(Material.AIR, false);
    }
    
    @Override
    public void tick() {
        // Tick collision
    }
    
    public enum Type {
        FIRE(Material.FIRE.createBlockData()),
        SOUL(Material.SOUL_FIRE.createBlockData());
        
        private final BlockData blockData;
        
        Type(BlockData blockData) {
            this.blockData = blockData;
        }
    }
}

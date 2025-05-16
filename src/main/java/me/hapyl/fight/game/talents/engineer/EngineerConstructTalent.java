package me.hapyl.fight.game.talents.engineer;

import me.hapyl.eterna.module.block.display.BDEngine;
import me.hapyl.eterna.module.block.display.DisplayData;
import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.engineer.EngineerData;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public abstract class EngineerConstructTalent extends Talent {
    
    @DisplayField private final int buildCost;
    @DisplayField private final int upgradeCost;
    @DisplayField private final double maxDeployDistance = 3;
    
    private final DisplayData model;
    protected double yOffset = 2.0;
    
    EngineerConstructTalent(@Nonnull Key key, @Nonnull String name, int buildCost, int upgradeCost, @Nonnull String model) {
        super(key, name);
        
        this.buildCost = buildCost;
        this.upgradeCost = upgradeCost;
        this.model = BDEngine.parse(model);
    }
    
    @Override
    public void setDescription(@Nonnull String description) {
        super.setDescription("""
                             %s
                             &6Construct
                             Constructs are interactable entities that have health and charges.
                             
                             %s
                             &8&o;;Each construct may exist only once simultaneously.
                             """.formatted(
                description,
                EngineerData.ironDescription
        ));
    }
    
    public int buildCost() {
        return buildCost;
    }
    
    public int upgradeCost() {
        return upgradeCost;
    }
    
    @Nonnull
    public abstract Construct create(@Nonnull GamePlayer player, @Nonnull Location location);
    
    @Nonnull
    @Override
    public final Response execute(@Nonnull GamePlayer player) {
        final EngineerData data = HeroRegistry.ENGINEER.getPlayerData(player);
        
        // Check for iron before any math
        if (data.getIron() < buildCost) {
            return Response.error("Not enough resources!");
        }
        
        final Location location = player.getLocation();
        final Vector vector = location.getDirection().normalize().setY(0);
        
        final double originX = location.getX();
        final double originZ = location.getZ();
        
        for (double d = 0; d < maxDeployDistance; d += 0.5) {
            final double x = vector.getX() * d;
            final double z = vector.getZ() * d;
            
            location.setX(originX + x);
            location.setZ(originZ + z);
            
            if (location.getBlock().isSolid()) {
                location.subtract(x, 0, z);
                break;
            }
        }
        
        // Anchor location down
        final Location finalLocation = LocationHelper.anchor(location);
        
        // Check if we can fit the construct
        for (int i = 1; i < yOffset - 0.6; i++) {
            final Block up = finalLocation.getBlock().getRelative(BlockFace.UP, i);
            
            if (!up.isEmpty()) {
                return Response.error("Unable to fit construct!");
            }
        }
        
        // Center
        finalLocation.setX(location.getBlockX() + 0.5);
        finalLocation.setZ(location.getBlockZ() + 0.5);
        
        // Fix yaw & pitch
        finalLocation.setYaw(0f);
        finalLocation.setPitch(0f);
        
        data.construct(this, finalLocation);
        return Response.OK;
    }
    
    @Nonnull
    public DisplayData model() {
        return model;
    }
    
}

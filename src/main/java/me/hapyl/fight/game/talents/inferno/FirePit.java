package me.hapyl.fight.game.talents.inferno;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.util.Removable;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.util.CFUtils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import javax.annotation.Nonnull;
import java.util.Map;

public class FirePit implements Removable {
    
    private final FirePitTalent talent;
    private final GamePlayer player;
    private final Location centre;
    
    private final Map<Location, InfernoFire> locations;
    
    FirePit(@Nonnull FirePitTalent talent, @Nonnull GamePlayer player, @Nonnull Location centre) {
        this.talent = talent;
        this.player = player;
        this.centre = centre;
        this.locations = Maps.newHashMap();
        
        // Prepare locations
        for (int[] offset : talent.firePitsOffsets) {
            locations.put(LocationHelper.anchor(centre.clone().add(offset[0], 0, offset[1])).subtract(0, 1, 0), null);
        }
    }
    
    @Nonnull
    public Location centre() {
        return centre;
    }
    
    public void transform(@Nonnull BlockData data) {
        locations.keySet().forEach(location -> {
            final Block block = location.getBlock();
            
            CFUtils.globalBlockChange(block.getLocation(), data);
        });
    }
    
    public void lightTheFire() {
        locations.entrySet().forEach(entry -> {
            final Location location = entry.getKey();
            
            // Check if location is valid
            entry.setValue(talent.createFire(
                    LocationHelper.addAsNew(location, 0, 1, 0), InfernoFire.Type.SOUL, entity -> {
                        if (player.isSelfOrTeammate(entity)) {
                            return;
                        }
                        
                        entity.damage(entity.getMaxHealth() * talent.damage, player, DamageCause.FIRE_PIT);
                    }
            ));
        });
        
        // Fx
        player.playWorldSound(centre, Sound.ITEM_FIRECHARGE_USE, 1.0f);
    }
    
    @Override
    public void remove() {
        locations.forEach((location, fire) -> {
            // Reset the block under fire
            location.getBlock().getState().update(true, false);
            
            // Call remove on fire, which will also remove it from a set
            if (fire != null) {
                fire.remove();
            }
        });
        
        // Fx
        player.playWorldSound(centre, Sound.BLOCK_FIRE_EXTINGUISH, 0.75f);
    }
    
}

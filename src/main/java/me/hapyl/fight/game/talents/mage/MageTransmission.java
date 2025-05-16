package me.hapyl.fight.game.talents.mage;


import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MageTransmission extends Talent {
    
    @DisplayField(suffix = " blocks") private final double maxDistance = 30.0d;
    
    public MageTransmission(@Nonnull Key key) {
        super(key, "Transmission");
        
        setDescription("""
                       Instantly &bteleport&7 to your &etarget&7 block, but lose the ability to &nmove&7 for a short duration.
                       """
        );
        
        setType(TalentType.MOVEMENT);
        setMaterial(Material.ENDER_PEARL);
        setCooldownSec(16);
    }
    
    @Override
    public @Nullable Response execute(@Nonnull GamePlayer player) {
        final Location location = getTargetLocation(player);
        
        if (location == null) {
            return Response.error("No valid block in sight!");
        }
        
        location.setYaw(player.getLocation().getYaw());
        location.setPitch(player.getLocation().getPitch());
        
        if (!location.getBlock().getType().isAir() || location.getBlock().getRelative(BlockFace.UP).getType().isOccluding()) {
            return Response.error("Location is not safe!");
        }
        
        player.teleport(location);
        
        player.addEffect(EffectType.MOVEMENT_CONTAINMENT, 20);
        player.playWorldSound(Sound.ENTITY_ENDERMAN_TELEPORT, 0.65f);
        
        if (location.getWorld() != null) {
            location.getWorld().playEffect(location, Effect.ENDER_SIGNAL, 0);
        }
        
        return Response.OK;
    }
    
    private Location getTargetLocation(GamePlayer player) {
        final Block block = player.getTargetBlockExact((int) maxDistance);
        
        if (block == null) {
            return null;
        }
        
        return block.getRelative(BlockFace.UP).getLocation().add(0.5, 0, 0.5);
    }
    
    
}

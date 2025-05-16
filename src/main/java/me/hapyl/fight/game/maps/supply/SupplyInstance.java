package me.hapyl.fight.game.maps.supply;

import me.hapyl.eterna.module.annotate.EventLike;
import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.util.Removable;
import me.hapyl.fight.CF;
import me.hapyl.fight.annotate.ClonedBeforeMutation;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.garbage.SynchronizedGarbageEntityCollector;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

import javax.annotation.Nonnull;

public class SupplyInstance extends TickingGameTask implements Removable {
    
    private static final double yOffset = 0.25;
    private static final double yCollisionOffset = yOffset * 3;
    private static final double pickupThreshold = 1.5;
    
    protected final Supply supply;
    protected final ArmorStand armorStand;
    
    public SupplyInstance(@Nonnull Supply supply, @Nonnull @ClonedBeforeMutation Location location) {
        this.supply = supply;
        
        this.armorStand = Entities.ARMOR_STAND.spawn(
                LocationHelper.addAsNew(location, 0, -yOffset, 0), self -> {
                    self.setInvulnerable(true);
                    self.setVisible(false);
                    self.setGravity(false);
                    self.setMarker(true);
                    self.setSmall(true);
                    
                    self.getEquipment().setHelmet(supply.itemStack);
                    
                    SynchronizedGarbageEntityCollector.add(self);
                }
        );
        
        runTaskTimer(0, 1);
    }
    
    @Nonnull
    public Location location() {
        return armorStand.getLocation();
    }
    
    @EventLike
    public void onPickup(@Nonnull GamePlayer player) {
        supply.pickup(player);
        remove();
    }
    
    @Override
    public void run(int tick) {
        final Location location = armorStand.getLocation();
        
        // Animation
        location.setYaw(location.getYaw() + 5);
        
        final double y = Math.sin(Math.toRadians(tick * 8)) * 0.0055;
        location.add(0, y, 0);
        
        armorStand.teleport(location);
        
        // Collision after animation before we're offsetting the location
        location.add(0, yCollisionOffset, 0);
        
        for (GamePlayer player : CF.getAlivePlayers()) {
            if (player.getLocation().distanceSquared(location) > pickupThreshold) {
                continue;
            }
            
            onPickup(player);
            return;
        }
        
        // Fx
        supply.tick(this);
    }
    
    @Override
    public void remove() {
        armorStand.remove();
        cancel();
    }
    
}

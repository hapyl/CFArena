package me.hapyl.fight.game.heroes.dylan;

import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public interface FamiliarAction {
    
    /**
     * Gets the destination for E'zel to follow.
     * <p>E'zel will continuously float towards the location until close to it.</p>
     *
     * @return the destination for E'zel to follow.
     */
    @Nonnull
    Location destination();
    
    /**
     * Gets whether this action is interruptible.
     * <p>Uninterruptible actions can only be overridden by {@link #followDylan(GamePlayer)}.</p>
     *
     * @return
     */
    default boolean isInterruptible() {
        return true;
    }
    
    /**
     * Ticks this action each tick it's active.
     *
     * @param player   - The player.
     * @param familiar - The familiar.
     */
    default void tick(@Nonnull GamePlayer player, @Nonnull DylanFamiliar familiar) {
    }
    
    @Nonnull
    static FamiliarAction followDylan(@Nonnull GamePlayer player) {
        return () -> {
            final Location location = player.getLocation();
            location.setPitch(0.0f);
            
            final Vector vector = LocationHelper.getVectorToTheRight(location);
            vector.multiply(1.5);
            vector.setY(0.85);
            
            location.add(vector);
            return location;
        };
    }
    
}

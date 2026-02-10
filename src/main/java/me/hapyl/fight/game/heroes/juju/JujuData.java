package me.hapyl.fight.game.heroes.juju;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.PlayerData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class JujuData extends PlayerData {
    
    protected boolean isClimbing;
    private ArrowType arrowType;
    
    public JujuData(@Nonnull GamePlayer player) {
        super(player);
    }
    
    public void arrowType(@Nonnull ArrowType arrowType, int duration) {
        // Cannot replace arrows
        if (this.arrowType != null) {
            Response.error(player, "Already using %s arrows!".formatted(arrowType.getName()));
            return;
        }
        
        this.arrowType = arrowType;
        this.arrowType.onEquip(player);
        
        // Schedule removal
        player.schedule(this::unequipeArrow, duration);
    }
    
    @Nullable
    public ArrowType arrowType() {
        return arrowType;
    }
    
    public void unequipeArrow() {
        if (arrowType == null) {
            return;
        }
        
        arrowType.onUnequip(player);
        arrowType = null;
    }
    
    @Override
    public void remove() {
        this.arrowType = null;
        this.isClimbing = false;
    }
}

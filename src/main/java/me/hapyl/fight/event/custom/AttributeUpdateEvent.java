package me.hapyl.fight.event.custom;

import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

/**
 * Called whenever the <b><i>base</i></b> {@link EntityAttributes} value is updated.
 */
public class AttributeUpdateEvent extends GameEntityEvent implements Cancellable {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private final AttributeType type;
    private final double oldValue;
    private final double newValue;
    
    private boolean cancel;
    
    public AttributeUpdateEvent(@Nonnull LivingGameEntity entity, @Nonnull AttributeType type, double oldValue, double newValue) {
        super(entity);
        
        this.type = type;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
    
    @Nonnull
    public AttributeType getType() {
        return type;
    }
    
    public double getOldValue() {
        return oldValue;
    }
    
    public double getNewValue() {
        return newValue;
    }
    
    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    
    @Override
    public boolean isCancelled() {
        return cancel;
    }
    
    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
    
    @Nonnull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
    
}

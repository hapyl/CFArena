package me.hapyl.fight.event.custom;

import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class AttributeChangeEvent extends GameEntityEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final AttributeType type;
    private final double oldValue;
    private final double newValue;
    private boolean cancel;

    public AttributeChangeEvent(LivingGameEntity entity, AttributeType type, double oldValue, double newValue) {
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

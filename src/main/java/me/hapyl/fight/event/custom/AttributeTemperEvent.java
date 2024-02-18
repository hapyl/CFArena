package me.hapyl.fight.event.custom;

import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class AttributeTemperEvent extends GameEntityEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Temper temper;
    private final AttributeType type;
    private final double value;
    private final int duration;
    private final boolean isSilent;
    private boolean cancel;

    public AttributeTemperEvent(LivingGameEntity entity, Temper temper, AttributeType type, double value, int duration, boolean isSilent) {
        super(entity);
        this.temper = temper;
        this.type = type;
        this.value = value;
        this.duration = duration;
        this.isSilent = isSilent;
    }

    @Nonnull
    public Temper getTemper() {
        return temper;
    }

    @Nonnull
    public AttributeType getType() {
        return type;
    }

    public double getValue() {
        return value;
    }

    public int getDuration() {
        return duration;
    }

    public boolean isInfiniteDuration() {
        return duration == -1;
    }

    public boolean isSilent() {
        return isSilent;
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
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @Nonnull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}

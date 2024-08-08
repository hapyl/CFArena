package me.hapyl.fight.event.custom;

import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AttributeTemperEvent extends GameEntityEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Temper temper;
    private final AttributeType type;
    private final double value;
    private final int duration;
    private final boolean isSilent;
    @Nullable private final LivingGameEntity applier;

    private boolean cancel;

    public AttributeTemperEvent(@Nonnull LivingGameEntity entity, @Nonnull Temper temper, @Nonnull AttributeType type, double value, int duration, boolean isSilent, @Nullable LivingGameEntity applier) {
        super(entity);
        this.temper = temper;
        this.type = type;
        this.value = value;
        this.duration = duration;
        this.isSilent = isSilent;
        this.applier = applier;
    }

    @Nullable
    public LivingGameEntity getApplier() {
        return applier;
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

    public boolean isBuff() {
        return type.isBuff(value, -value);
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
    public static AttributeTemperEvent createDummyEvent(@Nonnull LivingGameEntity entity, @Nonnull LivingGameEntity applier, boolean isBuff) {
        return new AttributeTemperEvent(entity, Temper.COMMAND, AttributeType.MAX_HEALTH, 0, 0, true, applier) {
            @Override
            public boolean isBuff() {
                return isBuff;
            }
        };
    }

    @Nonnull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}

package me.hapyl.fight.event;

import me.hapyl.fight.event.custom.GameEntityEvent;
import me.hapyl.fight.game.entity.BloodDebt;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

/**
 * Called whenever {@link LivingGameEntity} {@link BloodDebt} is changed.
 * <br>
 * This is NOT called when the debt is {@link BloodDebt#reset()}!
 */
public class BloodDebtChangeEvent extends GameEntityEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final double previousValue;
    private final double newValue;

    private boolean cancel;

    public BloodDebtChangeEvent(@Nonnull LivingGameEntity gameEntity, double previousValue, double newValue) {
        super(gameEntity);

        this.previousValue = previousValue;
        this.newValue = newValue;
    }

    public double previousValue() {
        return previousValue;
    }

    public double newValue() {
        return newValue;
    }

    public boolean isIncrement() {
        return newValue > previousValue;
    }

    public boolean isDecrement() {
        return newValue < previousValue;
    }

    public boolean hasChanged() {
        return newValue != previousValue;
    }

    @Override
    public boolean isCancelled() {
        return this.cancel;
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

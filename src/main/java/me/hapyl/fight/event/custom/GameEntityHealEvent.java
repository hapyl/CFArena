package me.hapyl.fight.event.custom;

import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GameEntityHealEvent extends GameEntityEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    @Nullable
    private final LivingGameEntity healer;
    private final double healingAmount;
    private final double healthBeforeHealing;
    private final double healthAfterHealing;
    private final double actualHealing;
    private final double excessHealing;
    private boolean cancel;

    public GameEntityHealEvent(@Nonnull LivingGameEntity entity, @Nullable LivingGameEntity healer, double amount, double actualHealing, double excessHealing, double healthBeforeHealing, double healthAfterHealing) {
        super(entity);

        this.healer = healer;
        this.healingAmount = amount;
        this.actualHealing = actualHealing;
        this.excessHealing = excessHealing;
        this.healthBeforeHealing = healthBeforeHealing;
        this.healthAfterHealing = healthAfterHealing;
    }

    @Nullable
    public LivingGameEntity getHealer() {
        return healer;
    }

    /**
     * Gets the healing entity received.
     *
     * @return the healing entity received.
     */
    public double getHealingAmount() {
        return healingAmount;
    }

    /**
     * Gets the amount of health entity restored.
     *
     * @return the amount of health entity has restored.
     */
    public double getActualHealing() {
        return actualHealing;
    }

    /**
     * Gets the excess healing.
     *
     * @return excess healing.
     */
    public double getExcessHealing() {
        return excessHealing;
    }

    /**
     * Gets entity's health before healing.
     *
     * @return health before healing.
     */
    public double getHealthBeforeHealing() {
        return healthBeforeHealing;
    }

    /**
     * Gets entity's health after healing.
     *
     * @return health after healing.
     */
    public double getHealthAfterHealing() {
        return healthAfterHealing;
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

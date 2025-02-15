package me.hapyl.fight.event.custom;

import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The event for monitoring a {@link DamageInstance}.
 * <p>This event does not allow accessing the damage instance itself, only reading.</p>
 */
public class GameDamageMonitorEvent extends CustomEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final DamageInstance instance;

    public GameDamageMonitorEvent(@Nonnull DamageInstance instance) {
        this.instance = instance;
    }

    public double getDamage() {
        return instance.getDamage();
    }

    @Nonnull
    public LivingGameEntity getEntity() {
        return instance.getEntity();
    }

    @Nullable
    public GameEntity getDamager() {
        return instance.getDamager();
    }

    @Nullable
    public DamageCause getCause() {
        return instance.getCause();
    }

    public boolean isCritical() {
        return instance.isCrit();
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

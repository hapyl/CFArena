package me.hapyl.fight.event.custom;

import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Called whenever {@link LivingGameEntity} takes lethal damage.
 */
public class GameDeathEvent extends CustomEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final DamageInstance instance;
    private boolean cancel;

    public GameDeathEvent(DamageInstance instance) {
        this.instance = instance;
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
    public EnumDamageCause getCause() {
        return instance.getCause();
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
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
        return HANDLERS;
    }
}

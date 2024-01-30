package me.hapyl.fight.event.custom;

import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GameDamageEvent extends CustomEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final DamageInstance instance;
    private boolean cancel;

    public GameDamageEvent(@Nonnull DamageInstance instance) {
        this.instance = instance;
        this.cancel = false;
    }

    @Nonnull
    public LivingGameEntity getEntity() {
        return instance.getEntity();
    }

    @Nullable
    public GameEntity getDamager() {
        return instance.getDamager();
    }

    public double getDamage() {
        return instance.getDamage();
    }

    public void setDamageMultiplier(double multiplier) {
        instance.multiplyDamage(multiplier);
    }

    @Nullable
    public EnumDamageCause getCause() {
        return instance.getCause();
    }

    public boolean isCrit() {
        return instance.isCrit();
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
        return HANDLERS;
    }

    @Nonnull
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}

package me.hapyl.fight.event.custom;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.LivingGameEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GameDamageEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    public final LivingGameEntity entity;
    @Nullable public final GameEntity damager;
    public final double damage;
    @Nullable public final EnumDamageCause cause;
    public final boolean isCrit;

    private boolean cancel;

    public GameDamageEvent(LivingGameEntity entity, @Nullable GameEntity damager, double damage, @Nullable EnumDamageCause cause, boolean isCrit) {
        this.entity = entity;
        this.damager = damager;
        this.damage = damage;
        this.cause = cause;
        this.isCrit = isCrit;
        this.cancel = false;
    }

    public LivingGameEntity getEntity() {
        return entity;
    }

    @Nullable
    public GameEntity getDamager() {
        return damager;
    }

    public double getDamage() {
        return damage;
    }

    @Nullable
    public EnumDamageCause getCause() {
        return cause;
    }

    public boolean isCrit() {
        return isCrit;
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

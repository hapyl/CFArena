package me.hapyl.fight.event.custom;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PlayerShieldEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Entity damager;
    private boolean cancel;

    public PlayerShieldEvent(@Nonnull Player player, @Nullable Entity damager) {
        super(player);

        this.damager = damager;
    }

    @Nullable
    public Entity getDamager() {
        return damager;
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

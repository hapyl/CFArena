package me.hapyl.fight.event.custom;

import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class EnderPearlTeleportEvent extends GamePlayerEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Location location;
    private boolean cancel;

    public EnderPearlTeleportEvent(GamePlayer player, Location location) {
        super(player);
        this.location = location;
    }

    @Nonnull
    public Location getLocation() {
        return location;
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

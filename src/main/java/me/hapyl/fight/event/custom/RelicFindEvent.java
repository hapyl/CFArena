package me.hapyl.fight.event.custom;

import me.hapyl.fight.game.collectible.relic.Relic;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import javax.annotation.Nonnull;

public class RelicFindEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Relic relic;
    private boolean cancel;

    public RelicFindEvent(@Nonnull Player player, @Nonnull Relic relic) {
        super(player);

        this.relic = relic;
    }

    @Nonnull
    public Relic getRelic() {
        return relic;
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

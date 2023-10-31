package me.hapyl.fight.event.custom;

import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class PlayerClickAtEntityEvent extends GamePlayerEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Entity entity;
    private final boolean isLeftClick;
    private boolean cancel;

    public PlayerClickAtEntityEvent(@Nonnull GamePlayer player, @Nonnull Entity entity, boolean isLeftClick) {
        super(player);
        this.entity = entity;
        this.isLeftClick = isLeftClick;
    }

    @Nonnull
    public Entity getEntity() {
        return entity;
    }

    public boolean isLeftClick() {
        return isLeftClick;
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

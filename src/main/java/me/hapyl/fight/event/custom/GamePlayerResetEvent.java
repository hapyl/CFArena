package me.hapyl.fight.event.custom;

import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class GamePlayerResetEvent extends GamePlayerEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    public GamePlayerResetEvent(@Nonnull GamePlayer gamePlayer) {
        super(gamePlayer);
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

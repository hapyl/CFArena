package me.hapyl.fight.event.custom.game;

import me.hapyl.fight.game.GameInstance;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class GameStartEvent extends GameEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    public GameStartEvent(GameInstance instance) {
        super(instance);
    }

    @Nonnull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}

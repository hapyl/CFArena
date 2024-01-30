package me.hapyl.fight.event.custom.game;

import me.hapyl.fight.game.GameInstance;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class GameEndEvent extends GameEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    public GameEndEvent(GameInstance instance) {
        super(instance);
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

package me.hapyl.fight.event.custom.game;

import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.State;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class GameChangeStateEvent extends GameEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final State[] states;
    private boolean cancel;

    public GameChangeStateEvent(GameInstance instance, State oldState, State newState) {
        super(instance);

        this.states = new State[] { oldState, newState };
    }

    @Nonnull
    public State getOldState() {
        return states[0];
    }

    @Nonnull
    public State getNewState() {
        return states[1];
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

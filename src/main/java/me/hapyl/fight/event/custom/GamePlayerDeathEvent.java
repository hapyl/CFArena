package me.hapyl.fight.event.custom;

import me.hapyl.fight.game.entity.GamePlayer;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

// Players don't die.
public class GamePlayerDeathEvent extends CustomEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final GamePlayer player;

    public GamePlayerDeathEvent(GamePlayer player) {
        this.player = player;
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


package me.hapyl.fight.event.custom;

import me.hapyl.fight.game.entity.GamePlayer;

import javax.annotation.Nonnull;

public abstract class GamePlayerEvent extends CustomEvent {

    protected final GamePlayer player;

    public GamePlayerEvent(@Nonnull GamePlayer gamePlayer) {
        player = gamePlayer;
    }

    @Nonnull
    public GamePlayer getPlayer() {
        return player;
    }
}

package me.hapyl.fight.event.custom.game;

import me.hapyl.fight.event.custom.CustomEvent;
import me.hapyl.fight.game.GameInstance;

import javax.annotation.Nonnull;

public abstract class GameEvent extends CustomEvent {

    private final GameInstance instance;

    public GameEvent(GameInstance instance) {
        this.instance = instance;
    }

    @Nonnull
    public GameInstance getInstance() {
        return instance;
    }
}

package me.hapyl.fight.game.task;

import me.hapyl.fight.game.Event;
import me.hapyl.fight.game.entity.GamePlayer;

import javax.annotation.Nonnull;

/**
 * GameTask that is automatically canceled if a player has died.
 */
public abstract class PlayerGameTask extends GameTask {

    private final GamePlayer player;

    public PlayerGameTask(@Nonnull GamePlayer player) {
        this.player = player;
        this.player.addTask(this);
    }

    @Nonnull
    public GamePlayer getPlayer() {
        return player;
    }

    @Event
    public void onTaskStopBecauseOfDeath() {
    }

    public void cancelBecauseOfDeath() {
        cancel0();
        onTaskStopBecauseOfDeath();
    }
}

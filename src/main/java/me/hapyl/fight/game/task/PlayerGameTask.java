package me.hapyl.fight.game.task;

import me.hapyl.fight.game.Event;
import me.hapyl.fight.game.entity.GamePlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * GameTask that is automatically canceled if a player has died.
 */
public abstract class PlayerGameTask extends GameTask {

    @Nullable
    private final Enum<?> name;
    private final GamePlayer player;

    public PlayerGameTask(@Nullable Enum<?> name, @Nonnull GamePlayer player) {
        this.name = name;
        this.player = player;
        this.player.addTask(this);
    }

    public PlayerGameTask(@Nonnull GamePlayer player) {
        this(null, player);
    }

    @Nullable
    public Enum<?> getName() {
        return name;
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

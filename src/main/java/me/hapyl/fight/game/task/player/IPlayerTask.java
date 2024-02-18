package me.hapyl.fight.game.task.player;

import me.hapyl.fight.game.Event;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.task.GameTask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IPlayerTask {

    /**
     * Gets the player associated with this task.
     *
     * @return the player associated with this task.
     */
    @Nonnull
    GamePlayer getPlayer();

    /**
     * Gets the enum associated with this task.
     * Tasks with enums are considered named, and will replace the task with the same enum.
     *
     * @return the enum associated with this task.
     */
    @Nullable
    default Enum<?> getEnum() {
        return null;
    }

    /**
     * Called upon player dying while the task is active.
     */
    @Event
    default void onTaskStopBecauseOfDeath() {
    }

    default void cancelBecauseOfDeath() {
        if (!(this instanceof GameTask task)) {
            throw new IllegalStateException("Must extend GameTask.");
        }

        task.cancel0();
        onTaskStopBecauseOfDeath();
    }

}

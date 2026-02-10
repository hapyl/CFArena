package me.hapyl.fight.game.entity.task;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.task.player.IPlayerTask;

import javax.annotation.Nonnull;
import java.util.Map;

public class PlayerTaskList {

    private final GamePlayer player;
    private final Map<TaskHash, IPlayerTask> tasks;

    public PlayerTaskList(GamePlayer player) {
        this.player = player;
        this.tasks = Maps.newHashMap();
    }

    @Nonnull
    public GamePlayer getPlayer() {
        return player;
    }

    public void add(@Nonnull IPlayerTask task) {
        final Class<?> name = task.getEnum();

        final IPlayerTask previousTask = tasks.put(new TaskHash(name), task);

        if (previousTask != null) {
            previousTask.cancelBecauseOfDeath(); // not sure if to cancel or cancelBecauseOfDeath here 🤔
        }
    }

    public void cancelAll() {
        tasks.values().forEach(IPlayerTask::cancelBecauseOfDeath);
        tasks.clear();
    }
}

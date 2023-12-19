package me.hapyl.fight.game.entity;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.fight.game.task.PlayerGameTask;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;

public class PlayerTaskList {

    private final GamePlayer player;
    private final Set<PlayerGameTask> anonymousTasks;
    private final Map<Enum<?>, PlayerGameTask> namedTasks;

    public PlayerTaskList(GamePlayer player) {
        this.player = player;
        this.anonymousTasks = Sets.newHashSet();
        this.namedTasks = Maps.newHashMap();
    }

    public void add(@Nonnull PlayerGameTask task) {
        final Enum<?> name = task.getName();

        if (name == null) {
            anonymousTasks.add(task);
        }
        else {
            final PlayerGameTask oldTask = namedTasks.put(name, task);

            if (oldTask != null) {
                oldTask.cancelBecauseOfDeath(); // not sure if to cancel or cancelBecauseOfDeath here 🤔
            }
        }
    }

    public void cancel(@Nonnull Enum<?> name) {
        final PlayerGameTask task = namedTasks.remove(name);

        if (task != null) {
            task.cancel();
        }
    }

    public void cancelAll() {
        anonymousTasks.forEach(PlayerGameTask::cancelBecauseOfDeath);
        anonymousTasks.clear();

        namedTasks.values().forEach(PlayerGameTask::cancelBecauseOfDeath);
        namedTasks.clear();
    }
}

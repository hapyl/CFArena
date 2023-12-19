package me.hapyl.fight.game.entity;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.task.player.IPlayerTask;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;

public class PlayerTaskList {

    private final GamePlayer player;
    private final Set<IPlayerTask> anonymousTasks;
    private final Map<Enum<?>, IPlayerTask> namedTasks;

    public PlayerTaskList(GamePlayer player) {
        this.player = player;
        this.anonymousTasks = Sets.newHashSet();
        this.namedTasks = Maps.newHashMap();
    }

    public void add(@Nonnull IPlayerTask task) {
        final Enum<?> name = task.getEnum();

        if (name == null) {
            anonymousTasks.add(task);
        }
        else {
            final IPlayerTask oldTask = namedTasks.put(name, task);

            if (oldTask != null) {
                oldTask.cancelBecauseOfDeath(); // not sure if to cancel or cancelBecauseOfDeath here 🤔
            }
        }
    }

    public void cancelAll() {
        anonymousTasks.forEach(IPlayerTask::cancelBecauseOfDeath);
        anonymousTasks.clear();

        namedTasks.values().forEach(IPlayerTask::cancelBecauseOfDeath);
        namedTasks.clear();
    }
}

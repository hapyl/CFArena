package me.hapyl.fight.game.task;

import me.hapyl.fight.Main;
import me.hapyl.fight.game.GameElement;
import me.hapyl.eterna.module.util.DependencyInjector;

import java.util.concurrent.ConcurrentHashMap;

public class TaskList extends DependencyInjector<Main> implements GameElement {

    public final ConcurrentHashMap<Integer, GameTask> byId;

    public TaskList(Main main) {
        super(main);
        this.byId = new ConcurrentHashMap<>();
    }

    public void register(GameTask task) {
        this.byId.put(task.getId(), task);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {
        byId.forEach((id, task) -> {
            if (task.getShutdownAction() == ShutdownAction.CANCEL) {
                task.deepCancel();
                byId.remove(id);
            }
        });
    }
}

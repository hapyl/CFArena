package kz.hapyl.fight.game.task;

import kz.hapyl.fight.game.GameElement;

import java.util.concurrent.ConcurrentHashMap;

public class TaskList implements GameElement {

	public final ConcurrentHashMap<Integer, GameTask> byId;

	public TaskList() {
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

package kz.hapyl.fight.game.report;

import kz.hapyl.fight.game.GameInstance;

import java.util.Map;
import java.util.TreeMap;

/**
 * This will track any action that happened in the game.
 */
public class GameReport {

	private final Map<Long, GameAction> actions = new TreeMap<>();
	private final GameInstance instance;

	public GameReport(GameInstance instance) {
		this.instance = instance;
	}

	public void addAction(GameAction action) {
		// there is no way that an action is happened at the same millis as the other one
		actions.put(System.currentTimeMillis(), action);
	}

	public GameInstance getInstance() {
		return instance;
	}

	public Map<Long, GameAction> getActions() {
		return actions;
	}
}

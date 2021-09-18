package kz.hapyl.fight;

import kz.hapyl.fight.game.Manager;

public class GameRunnable implements Runnable {

	@Override
	public void run() {
		if (Manager.current().isGameInProgress()) {

		}
	}

}

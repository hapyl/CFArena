package kz.hapyl.fight.game.gamemode;

import kz.hapyl.fight.game.GameInstance;
import kz.hapyl.fight.game.GamePlayer;

import javax.annotation.Nonnull;

public abstract class CFGameMode {

	private final String name;
	private final int timeLimit;

	private String info;
	private int playerRequirements;

	public CFGameMode(String name, int timeLimit) {
		this.name = name;
		this.timeLimit = timeLimit;
		this.info = "";
		this.playerRequirements = 2;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getInfo() {
		return info;
	}

	public void setPlayerRequirements(int playerRequirements) {
		this.playerRequirements = playerRequirements;
	}

	public int getPlayerRequirements() {
		return playerRequirements;
	}

	public int getTimeLimit() {
		return timeLimit;
	}

	public String getName() {
		return name;
	}

	public abstract boolean testWinCondition(@Nonnull GameInstance instance);

	public void onDeath(@Nonnull GameInstance instance, @Nonnull GamePlayer player) {

	}

	/**
	 * Use this to calculate winners if not default.
	 *
	 * @param instance - game instance.
	 * @return false to mark all alive players as winners.
	 */
	public boolean onStop(@Nonnull GameInstance instance) {
		return false;
	}

}

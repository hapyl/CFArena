package kz.hapyl.fight.game.gamemode.modes;

import kz.hapyl.fight.game.GameInstance;
import kz.hapyl.fight.game.gamemode.CFGameMode;

import javax.annotation.Nonnull;

public class FreeForAll extends CFGameMode {
	public FreeForAll() {
		super("Fre for All", 600);
		this.setPlayerRequirements(2);
	}

	@Override
	public boolean testWinCondition(@Nonnull GameInstance instance) {
		return instance.getAlivePlayers().size() <= 1;
	}

}

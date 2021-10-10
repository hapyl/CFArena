package kz.hapyl.fight.game.gamemode.modes;

import kz.hapyl.fight.game.GameInstance;
import kz.hapyl.fight.game.gamemode.CFGameMode;

import javax.annotation.Nonnull;

public class CaptureTheFlag extends CFGameMode {
	public CaptureTheFlag() {
		super("Capture the Flag", 400);
	}

	@Override
	public boolean testWinCondition(@Nonnull GameInstance instance) {
		return false;
	}
}

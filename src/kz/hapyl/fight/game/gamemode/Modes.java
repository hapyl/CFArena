package kz.hapyl.fight.game.gamemode;

import kz.hapyl.fight.game.GameInstance;
import kz.hapyl.fight.game.gamemode.modes.Deathmatch;
import kz.hapyl.fight.game.gamemode.modes.FreeForAll;
import kz.hapyl.spigotutils.module.util.Validate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum Modes {

	FFA(new FreeForAll()),
	DEATH_MATCH(new Deathmatch()),
	;

	private final CFGameMode mode;

	Modes(CFGameMode mode) {
		this.mode = mode;
	}

	public CFGameMode getMode() {
		return mode;
	}

	public boolean testWinCondition(@Nonnull GameInstance instance) {
		return this.mode.testWinCondition(instance);
	}

	public boolean onStop(@Nonnull GameInstance instance) {
		return this.mode.onStop(instance);
	}

	@Nullable
	public static Modes byName(String name, @Nullable Modes def) {
		final Modes value = Validate.getEnumValue(Modes.class, name == null ? FFA.name() : name);
		return value == null ? def : value;
	}

}

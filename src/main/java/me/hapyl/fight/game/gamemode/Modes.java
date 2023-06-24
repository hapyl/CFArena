package me.hapyl.fight.game.gamemode;

import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.gamemode.modes.Deathmatch;
import me.hapyl.fight.game.gamemode.modes.DeathmatchKills;
import me.hapyl.fight.game.gamemode.modes.FreeForAll;
import me.hapyl.fight.game.gamemode.modes.Rush;
import me.hapyl.spigotutils.module.util.Validate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum Modes {

    FFA(new FreeForAll()),
    DEATH_MATCH(new Deathmatch()),
    DEATH_MATCH_KILLS(new DeathmatchKills()),
	RUSH(new Rush()),
	//TTT(new AmongUs()), todo
    ;

    private final CFGameMode mode;

    Modes(CFGameMode mode) {
        this.mode = mode;
    }

    public CFGameMode getMode() {
        return mode;
	}

	public boolean isSelected() {
		return Manager.current().getCurrentMode() == this;
	}

	public void select() {
		if (isSelected()) {
			return;
		}

		Manager.current().setCurrentMode(this);
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

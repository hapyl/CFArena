package kz.hapyl.fight.game.heroes.storage;

import kz.hapyl.fight.game.heroes.Hero;
import kz.hapyl.fight.game.talents.Talent;

public class Taker extends Hero {

	public Taker() {
		super("");
	}

	@Override
	public Talent getFirstTalent() {
		return null;
	}

	@Override
	public Talent getSecondTalent() {
		return null;
	}

	@Override
	public Talent getPassiveTalent() {
		return null;
	}
}

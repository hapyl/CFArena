package kz.hapyl.fight.game.heroes.storage;

import kz.hapyl.fight.game.heroes.ComplexHero;
import kz.hapyl.fight.game.heroes.Hero;
import kz.hapyl.fight.game.talents.Talent;

public class DarkMage extends Hero implements ComplexHero {

	public DarkMage() {
		super("Dark Mage");
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
	public Talent getThirdTalent() {
		return null;
	}

	@Override
	public Talent getPassiveTalent() {
		return null;
	}

	@Override
	public Talent getFourthTalent() {
		return null;
	}

	@Override
	public Talent getFifthTalent() {
		return null;
	}
}

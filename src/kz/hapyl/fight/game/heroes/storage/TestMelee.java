package kz.hapyl.fight.game.heroes.storage;

import kz.hapyl.fight.game.heroes.Hero;
import kz.hapyl.fight.game.talents.Talent;
import kz.hapyl.fight.game.talents.UltimateTalent;
import kz.hapyl.fight.game.weapons.Weapons;

public class TestMelee extends Hero {
	public TestMelee() {
		super("test melee class");
		this.setWeapon(Weapons.DEFAULT);
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
	public UltimateTalent getUltimate() {
		return null;
	}

	@Override
	public Talent getPassiveTalent() {
		return null;
	}
}

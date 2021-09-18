package kz.hapyl.fight.game.heroes;

import kz.hapyl.fight.game.heroes.storage.Archer;
import kz.hapyl.fight.game.heroes.storage.TestMelee;

public enum Heroes {

	TEST(new TestMelee()),
	ARCHER(new Archer());

	private final Hero hero;

	Heroes(Hero hero) {
		this.hero = hero;
	}

	public Hero getHero() {
		return hero;
	}

}

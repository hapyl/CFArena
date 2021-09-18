package kz.hapyl.fight.game.talents;

import kz.hapyl.fight.game.talents.storage.BoomBow;
import kz.hapyl.fight.game.talents.storage.HawkeyeArrow;
import kz.hapyl.fight.game.talents.storage.ShockDark;
import kz.hapyl.fight.game.talents.storage.TripleShot;

public enum Talents {

	// Archer
	TRIPLE_SHOT(new TripleShot()),
	SHOCK_DARK(new ShockDark()),
	HAWKEYE_ARROW(new HawkeyeArrow()),
	BOOM_BOW(new BoomBow()),

	;

	private final Talent talent;

	Talents(Talent talent) {
		this.talent = talent;
	}

	public Talent getTalent() {
		return talent;
	}
}

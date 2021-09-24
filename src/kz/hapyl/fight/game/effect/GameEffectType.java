package kz.hapyl.fight.game.effect;

import kz.hapyl.fight.game.effect.storage.Corrosion;
import kz.hapyl.fight.game.talents.storage.Amnesia;
import kz.hapyl.fight.game.talents.storage.ParanoiaEffect;

public enum GameEffectType {

	CORROSION(new Corrosion()),
	PARANOIA(new ParanoiaEffect()),
	AMNESIA(new Amnesia()),

	;

	private final GameEffect gameEffect;

	GameEffectType(GameEffect gameEffect) {
		this.gameEffect = gameEffect;
	}

	public GameEffect getGameEffect() {
		return gameEffect;
	}
}

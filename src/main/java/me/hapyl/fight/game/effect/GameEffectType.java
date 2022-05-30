package me.hapyl.fight.game.effect;

import me.hapyl.fight.game.effect.storage.*;
import me.hapyl.fight.game.talents.storage.Amnesia;
import me.hapyl.fight.game.talents.storage.ParanoiaEffect;

public enum GameEffectType {

	CORROSION(new Corrosion()),
	PARANOIA(new ParanoiaEffect()),
	AMNESIA(new Amnesia()),
	FALL_DAMAGE_RESISTANCE(new FallDamageResistance()),
	STUN(new Stun()),
	VULNERABLE(new Vulnerable()),
	IMMOVABLE(new Immovable()),
	INVISIBILITY(new Invisibility()),
	RESPAWN_RESISTANCE(new RespawnResistance()),
	RIPTIDE(new Riptide()),
	LOCK_DOWN(new LockdownEffect()),

	;

	private final GameEffect gameEffect;

	GameEffectType(GameEffect gameEffect) {
		this.gameEffect = gameEffect;
	}

	public GameEffect getGameEffect() {
		return gameEffect;
	}
}

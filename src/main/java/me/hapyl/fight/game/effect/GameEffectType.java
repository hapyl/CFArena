package me.hapyl.fight.game.effect;

import me.hapyl.fight.game.effect.storage.*;

public enum GameEffectType {

    CORROSION(new Corrosion()),
    PARANOIA(new ParanoiaEffect()),
    AMNESIA(new Amnesia()),
    CANCEL_FALL_DAMAGE(new CancelFallDamage()),
    FALL_DAMAGE_RESISTANCE(new FallDamageResistance()),
    STUN(new Stun()),
    VULNERABLE(new Vulnerable()),
    IMMOVABLE(new Immovable()),
    INVISIBILITY(new Invisibility()),
    RESPAWN_RESISTANCE(new RespawnResistance()),
    RIPTIDE(new Riptide()),
    LOCK_DOWN(new LockdownEffect()),
    ARCANE_MUTE(new ArcaneMuteEffect());

    private final GameEffect gameEffect;

    GameEffectType(GameEffect gameEffect) {
        this.gameEffect = gameEffect;
    }

    public GameEffect getGameEffect() {
        return gameEffect;
    }
}

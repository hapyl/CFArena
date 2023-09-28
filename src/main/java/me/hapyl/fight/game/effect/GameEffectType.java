package me.hapyl.fight.game.effect;

import me.hapyl.fight.game.effect.archive.*;

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
    ARCANE_MUTE(new ArcaneMuteEffect()),
    SLOWING_AURA(new SlowingAuraEffect()),
    BLEED(new BleedEffect()),

    ORC_GROWL(new OrcGrowlEffect()),
    WITHER_BLOOD(new WitherBlood()),
    POISON_IVY(new PoisonIvy()),
    SADNESS(new SadnessEffect()),

    ;

    private final GameEffect gameEffect;

    GameEffectType(GameEffect gameEffect) {
        this.gameEffect = gameEffect;
    }

    public GameEffect getGameEffect() {
        return gameEffect;
    }
}

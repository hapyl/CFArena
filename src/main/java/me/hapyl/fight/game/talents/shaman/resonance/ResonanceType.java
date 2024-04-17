package me.hapyl.fight.game.talents.shaman.resonance;

import javax.annotation.Nonnull;

public enum ResonanceType {
    STANDBY(new StandbyResonance()),
    HEALING_AURA(new HealResonance()),
    CYCLONE_AURA(new CycloneResonance()),
    DAMAGE_AURA(new DamageResonance()),
    ;

    private final TotemResonance resonance;

    ResonanceType(TotemResonance resonance) {
        this.resonance = resonance;
    }

    @Nonnull
    public TotemResonance getResonance() {
        return resonance;
    }

    @Override
    public String toString() {
        return resonance.getDescription();
    }
}

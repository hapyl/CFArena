package me.hapyl.fight.game.talents.knight;

import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.game.talents.TalentRegistry;

import javax.annotation.Nonnull;

public class QuantumEnergyPassive extends PassiveTalent {
    public QuantumEnergyPassive(@Nonnull DatabaseKey key) {
        super(key, "Quantum Energy");

        setDescription("""
                &8Blocking&7 damage with your &bshield&7 will accumulate &dQuantum Energy&7.
                
                Using &a%s&7 can manipulate the energy to create &fNova Explosion&7.
                """.formatted(TalentRegistry.DISCHARGE.getName())
        );
    }
}

package me.hapyl.fight.terminology.terms;

import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.damage.DamageFlag;
import me.hapyl.fight.terminology.Term;

public class TermPiercingDamage extends Term {

    public TermPiercingDamage() {
        setName("Piercing Damage");

        setShortDescription("""
                Piercing damage ignores shields.
                """);

        final StringBuilder stringBuilder = new StringBuilder("""
                Piercing damage ignores shields.
                
                &7&oPiercing damage includes:
                """);

        int shownDamage = 0;
        for (DamageCause cause : DamageCause.values()) {
            if (shownDamage > 5) {
                break;
            }

            if (cause.hasFlag(DamageFlag.PIERCING_DAMAGE)) {
                stringBuilder.append("&f- &l").append(cause.getReadableName()).append("\n");
                shownDamage++;
            }
        }

        setDescription(stringBuilder.toString());
    }

}

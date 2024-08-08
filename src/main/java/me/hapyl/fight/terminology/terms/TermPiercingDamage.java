package me.hapyl.fight.terminology.terms;

import me.hapyl.fight.game.damage.DamageFlag;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.terminology.Term;
import me.hapyl.eterna.module.chat.Chat;

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
        for (EnumDamageCause cause : EnumDamageCause.values()) {
            if (shownDamage > 5) {
                break;
            }

            if (cause.hasFlag(DamageFlag.PIERCING_DAMAGE)) {
                stringBuilder.append("&f- &l").append(Chat.capitalize(cause)).append("\n");
                shownDamage++;
            }
        }

        setDescription(stringBuilder.toString());
    }

}

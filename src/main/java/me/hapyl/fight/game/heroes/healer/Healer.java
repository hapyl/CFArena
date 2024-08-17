package me.hapyl.fight.game.heroes.healer;

import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.game.Disabled;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.healer.HealingOrb;
import me.hapyl.fight.game.talents.Talent;

import javax.annotation.Nonnull;

public class Healer extends Hero implements Disabled {

    public Healer(@Nonnull DatabaseKey key) {
        super(key, "Healer");

        setItem("null");
    }

    @Override
    public HealingOrb getFirstTalent() {
        return (HealingOrb) Talents.HEALING_ORB.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return null;
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.REVIVE.getTalent();
    }
}

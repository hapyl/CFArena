package me.hapyl.fight.game.heroes.healer;


import me.hapyl.fight.game.Disabled;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.healer.HealingOrb;
import me.hapyl.fight.registry.Key;

import javax.annotation.Nonnull;

public class Healer extends Hero implements Disabled {

    public Healer(@Nonnull Key key) {
        super(key, "Healer");

        setItem("null");
    }

    @Override
    public HealingOrb getFirstTalent() {
        return TalentRegistry.HEALING_ORB;
    }

    @Override
    public Talent getSecondTalent() {
        return null;
    }

    @Override
    public Talent getPassiveTalent() {
        return TalentRegistry.REVIVE;
    }
}

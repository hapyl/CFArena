package me.hapyl.fight.game.heroes.archive.healer;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.DisabledHero;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.UltimateCallback;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.archive.healer.HealingOrb;

import javax.annotation.Nonnull;

public class Healer extends Hero implements DisabledHero {

    public Healer(@Nonnull Heroes handle) {
        super(handle, "Healer");

        setItem("null");
    }

    @Override
    public UltimateCallback useUltimate(@Nonnull GamePlayer player) {
        return UltimateCallback.OK;
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

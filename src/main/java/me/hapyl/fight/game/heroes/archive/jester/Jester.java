package me.hapyl.fight.game.heroes.archive.jester;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.DisabledHero;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.UltimateCallback;
import me.hapyl.fight.game.talents.archive.techie.Talent;

import javax.annotation.Nonnull;

public class Jester extends Hero implements DisabledHero {
    public Jester() {
        super("Jester");
    }

    @Override
    public UltimateCallback useUltimate(@Nonnull GamePlayer player) {
        return UltimateCallback.OK;
    }

    @Override
    public Talent getFirstTalent() {
        return null;
    }

    @Override
    public Talent getSecondTalent() {
        return null;
    }

    @Override
    public Talent getPassiveTalent() {
        return null;
    }
}

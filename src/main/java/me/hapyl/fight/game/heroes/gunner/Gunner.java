package me.hapyl.fight.game.heroes.gunner;

import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.talents.Talent;

import javax.annotation.Nonnull;

public class Gunner extends Hero {
    public Gunner(@Nonnull Heroes handle) {
        super(handle, "Gunner");

        setDescription("""
                """);

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

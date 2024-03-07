package me.hapyl.fight.game.heroes.archive.jester;

import me.hapyl.fight.game.Disabled;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.talents.archive.techie.Talent;

public class Jester extends Hero implements Disabled {

    public Jester(Heroes handle) {
        super(handle, "Jester");
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

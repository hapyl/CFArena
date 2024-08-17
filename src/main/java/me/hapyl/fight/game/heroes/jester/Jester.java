package me.hapyl.fight.game.heroes.jester;

import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.game.Disabled;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.talents.Talent;

public class Jester extends Hero implements Disabled {

    public Jester(DatabaseKey key) {
        super(key, "Jester");
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

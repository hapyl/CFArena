package me.hapyl.fight.game.heroes.geo;


import me.hapyl.fight.game.Disabled;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.registry.Key;

import javax.annotation.Nonnull;

public class Geo extends Hero implements Disabled {
    public Geo(@Nonnull Key key) {
        super(key, "Geo");
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

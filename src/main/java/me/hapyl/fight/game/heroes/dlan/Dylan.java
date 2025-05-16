package me.hapyl.fight.game.heroes.dlan;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.talents.Talent;

import javax.annotation.Nonnull;

public class Dylan extends Hero {
    public Dylan(@Nonnull Key key) {
        super(key, "D'lan");
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

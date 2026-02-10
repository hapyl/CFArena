package me.hapyl.fight.game.heroes.gunner;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Disabled;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.talents.Talent;

import javax.annotation.Nonnull;

public class Gunner extends Hero implements Disabled {
    public Gunner(@Nonnull Key key) {
        super(key, "Gunner");

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

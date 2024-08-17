package me.hapyl.fight.game.heroes.gunner;

import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.game.Disabled;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.talents.Talent;

import javax.annotation.Nonnull;

public class Gunner extends Hero implements Disabled {
    public Gunner(@Nonnull DatabaseKey key) {
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

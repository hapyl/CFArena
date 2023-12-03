package me.hapyl.fight.game.heroes;

import me.hapyl.fight.game.talents.Talent;

/**
 * Represents a complex with up to 3 additional talents.
 */
public interface ComplexHero {

    Talent getThirdTalent();

    Talent getFourthTalent();

    Talent getFifthTalent();

}

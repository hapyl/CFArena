package me.hapyl.fight.game;

import me.hapyl.fight.game.talents.Talent;

import javax.annotation.Nonnull;

public interface TalentReference<T extends Talent> {

    @Nonnull
    T getTalent();

}

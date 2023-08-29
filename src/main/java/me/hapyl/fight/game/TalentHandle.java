package me.hapyl.fight.game;

import me.hapyl.fight.game.talents.Talent;

import javax.annotation.Nonnull;

public interface TalentHandle<T extends Talent> {

    @Nonnull
    T getTalent();

}

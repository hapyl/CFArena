package me.hapyl.fight.game;

import me.hapyl.fight.game.talents.archive.techie.Talent;

import javax.annotation.Nonnull;

public interface TalentReference<T extends Talent> {

    @Nonnull
    T getTalent();

}

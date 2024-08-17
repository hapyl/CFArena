package me.hapyl.fight.game;

import me.hapyl.fight.game.heroes.Hero;

import javax.annotation.Nonnull;

@Deprecated(forRemoval = true)
public interface HeroReference<T extends Hero> {

    @Nonnull
    T getHero();

}

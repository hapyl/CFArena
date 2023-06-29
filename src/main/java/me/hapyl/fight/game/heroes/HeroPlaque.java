package me.hapyl.fight.game.heroes;

import javax.annotation.Nonnull;

public interface HeroPlaque {

    @Nonnull
    String text();

    default long until() {
        return -1L;
    }

}

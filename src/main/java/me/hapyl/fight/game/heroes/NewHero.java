package me.hapyl.fight.game.heroes;

public interface NewHero {

    default long until() {
        return -1L;
    }

}

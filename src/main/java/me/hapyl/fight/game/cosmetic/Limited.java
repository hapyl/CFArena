package me.hapyl.fight.game.cosmetic;

public interface Limited {

    default long limitedUntil() {
        return -1L;
    }

}

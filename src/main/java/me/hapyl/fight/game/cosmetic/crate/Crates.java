package me.hapyl.fight.game.cosmetic.crate;

public enum Crates {

    NOVICE(null),

    ;

    private final Crate crate;

    Crates(Crate crate) {
        this.crate = crate;
    }

    public Crate getCrate() {
        return crate;
    }
}

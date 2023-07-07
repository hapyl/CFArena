package me.hapyl.fight.game.attribute;

public enum Temper {

    COMMAND(true),   // for testing

    FLOWER_BREEZE(true),
    BERSERK_MODE(true),

    POISON_IVY(false),

    ;

    private final boolean isBuff;

    Temper(boolean isBuff) {
        this.isBuff = isBuff;
    }

    public boolean isBuff() {
        return isBuff;
    }

    public boolean isDebuff() {
        return !isBuff();
    }
}

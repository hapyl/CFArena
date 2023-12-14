package me.hapyl.fight.game.dot;

import me.hapyl.fight.game.dot.archive.BleedDot;
import me.hapyl.fight.util.EnumWrapper;

import javax.annotation.Nonnull;

public enum DamageOverTime implements EnumWrapper<Dot> {

    BLEED(new BleedDot()),

    ;

    private final Dot dot;

    DamageOverTime(Dot dot) {
        this.dot = dot;
    }

    @Nonnull
    @Override
    public Dot get() {
        return dot;
    }
}

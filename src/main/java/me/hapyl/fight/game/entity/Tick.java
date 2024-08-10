package me.hapyl.fight.game.entity;

import me.hapyl.fight.util.MutableNumericObject;
import me.hapyl.fight.util.Ticking;

import javax.annotation.Nonnull;

public sealed class Tick implements Ticking, MutableNumericObject permits EntityTick {

    protected final TickDirection direction;

    protected int tick;

    public Tick(@Nonnull TickDirection direction) {
        this.direction = direction;
    }

    @Override
    public void tick() {
        tick = Math.max(direction.tick(tick), 0);
    }

    @Override
    public int toInt() {
        return tick;
    }

    @Override
    public void setInt(final int newTick) {
        this.tick = newTick;
    }

    @Override
    public void min() {
        setInt(direction.defaultValue());
    }

    @Override
    public String toString() {
        return String.valueOf(tick);
    }

}

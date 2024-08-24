package me.hapyl.fight.game.entity;

import javax.annotation.Nonnull;

public sealed class Tick permits EntityTick {

    protected final TickDirection direction;

    protected int tick;

    Tick(@Nonnull TickDirection direction) {
        this.direction = direction;
    }

    public int getTick() {
        return tick;
    }

    @Override
    public String toString() {
        return String.valueOf(tick);
    }

    protected void zero() {
        tick = 0;
    }

    protected void tick() {
        tick = Math.max(direction.tick(tick), 0);
    }

    protected void setInt(final int newTick) {
        this.tick = newTick;
    }

    protected void min() {
        setInt(direction.defaultValue());
    }

}

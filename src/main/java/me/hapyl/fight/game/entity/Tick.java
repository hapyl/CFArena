package me.hapyl.fight.game.entity;

import me.hapyl.fight.util.MutableNumericObject;
import me.hapyl.fight.util.Ticking;

public class Tick implements Ticking, MutableNumericObject {

    protected final int[] limits;
    protected int tick;

    public Tick() {
        this(0);
    }

    public Tick(final int minTick) {
        this(minTick, Integer.MAX_VALUE);
    }

    public Tick(final int minTick, final int maxTick) {
        this.limits = new int[] { minTick, maxTick };
    }

    @Override
    public void tick() {
        tick = Math.clamp(tick - 1, limits[0], limits[1]);
    }

    public final void tick(boolean condition) {
        if (condition) {
            tick();
        }
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
        setInt(limits[0]);
    }

    @Override
    public void max() {
        setInt(limits[1]);
    }

    @Override
    public final String toString() {
        return String.valueOf(tick);
    }

}

package me.hapyl.fight.game.entity;

public class IncrementingTick extends Tick {

    @Override
    public final void tick() {
        tick = Math.clamp(tick + 1, limits[0], limits[1]);
    }
}

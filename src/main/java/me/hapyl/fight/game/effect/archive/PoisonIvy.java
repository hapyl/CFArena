package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.fight.game.entity.GameEntity;

public class PoisonIvy extends GameEffect {
    public PoisonIvy() {
        super("Poison Ivy");

        setPositive(false);
    }

    @Override
    public void onStart(GameEntity entity) {
    }

    @Override
    public void onStop(GameEntity entity) {

    }

    @Override
    public void onTick(GameEntity entity, int tick) {

    }
}

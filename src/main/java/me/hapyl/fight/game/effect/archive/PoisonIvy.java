package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.fight.game.entity.LivingGameEntity;

public class PoisonIvy extends GameEffect {
    public PoisonIvy() {
        super("Poison Ivy");

        setPositive(false);
    }

    @Override
    public void onStart(LivingGameEntity entity) {
    }

    @Override
    public void onStop(LivingGameEntity entity) {

    }

    @Override
    public void onTick(LivingGameEntity entity, int tick) {

    }
}

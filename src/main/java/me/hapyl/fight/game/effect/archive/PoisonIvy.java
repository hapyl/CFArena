package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.fight.game.entity.LivingGameEntity;

import javax.annotation.Nonnull;

public class PoisonIvy extends GameEffect {
    public PoisonIvy() {
        super("Poison Ivy");

        setPositive(false);
    }

    @Override
    public void onStart(@Nonnull LivingGameEntity entity) {
    }

    @Override
    public void onStop(@Nonnull LivingGameEntity entity) {

    }

    @Override
    public void onTick(@Nonnull LivingGameEntity entity, int tick) {

    }
}

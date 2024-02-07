package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.game.effect.Effect;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.LivingGameEntity;

import javax.annotation.Nonnull;

public class PoisonIvy extends Effect {
    public PoisonIvy() {
        super("Poison Ivy", EffectType.NEGATIVE);
    }

    @Override
    public void onStart(@Nonnull LivingGameEntity entity, int amplifier, int duration) {
    }

    @Override
    public void onStop(@Nonnull LivingGameEntity entity, int amplifier) {

    }

}

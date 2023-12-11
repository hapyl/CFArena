package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.fight.game.entity.LivingGameEntity;

import javax.annotation.Nonnull;

public class FallDamageResistance extends GameEffect {

    public FallDamageResistance() {
        super("Fall Damage Resistance");
        this.setDescription("Negates all fall damage until it's taken.");
    }

    @Override
    public void onTick(@Nonnull LivingGameEntity entity, int tick) {

    }

    @Override
    public void onStart(@Nonnull LivingGameEntity entity) {

    }

    @Override
    public void onStop(@Nonnull LivingGameEntity entity) {

    }
}

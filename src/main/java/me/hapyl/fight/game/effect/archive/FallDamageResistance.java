package me.hapyl.fight.game.effect.archive;

import me.hapyl.fight.game.effect.GameEffect;
import me.hapyl.fight.game.entity.GameEntity;

public class FallDamageResistance extends GameEffect {

    public FallDamageResistance() {
        super("Fall Damage Resistance");
        this.setDescription("Negates all fall damage until it's taken.");
    }

    @Override
    public void onTick(GameEntity entity, int tick) {

    }

    @Override
    public void onStart(GameEntity entity) {

    }

    @Override
    public void onStop(GameEntity entity) {

    }
}
